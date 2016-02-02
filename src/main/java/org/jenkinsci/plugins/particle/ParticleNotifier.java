package org.jenkinsci.plugins.particle;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BallColor;
import hudson.model.BuildListener;
import hudson.model.AbstractProject;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;

import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import javax.servlet.ServletException;

import org.apache.http.client.fluent.Request;

public class ParticleNotifier extends Notifier {

    private final String eventName;
    private final String successEventData;
    private final String failureEventData;
    private final String noneEventData;

    @DataBoundConstructor
    public ParticleNotifier(String eventName, String successEventData,
        String failureEventData, String noneEventData) {
        this.eventName = eventName;
        this.successEventData = successEventData;
        this.failureEventData = failureEventData;
        this.noneEventData = noneEventData;
    }

    public String getEventName() {
        return eventName;
    }

    public String getSuccessEventData() {
        return successEventData;
    }

    public String getFailureEventData() {
        return failureEventData;
    }

    public String getNoneEventData() {
        return noneEventData;
    }

    private String convertToString(BallColor ballColor) {
        if (ballColor == BallColor.RED) {
            return getFailureEventData();
        }
        else if (ballColor == BallColor.BLUE) {
            return getSuccessEventData();
        }
        else {
            return getNoneEventData();
        }
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        BallColor ballColor = build.getResult().color;
        String accessToken = getDescriptor().getAccessToken();
        int code = ParticleUtil.sendParticleEvent(accessToken, getEventName(), convertToString(ballColor));
        listener.getLogger().println("POST-BUILD STEP Particle device return code: " + code);
        return (code == 200);
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        private String accessToken;

        public DescriptorImpl() {
            load();
        }

        public String getAccessToken() {
            return accessToken;
        }

        public FormValidation doTestParticleConnection(
                @QueryParameter("particle.accessToken") final String token)
                throws IOException, ServletException {

            int code = 0;

            try {
                code = Request.Get(ParticleUtil.BASE_API_STRING)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Accept", "*/*")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .execute().returnResponse().getStatusLine().getStatusCode();
            } catch (Exception e) {
                e.printStackTrace();
                return FormValidation.error(
                    "Error occured: " + e.getMessage() + " (Token: " + accessToken + ")"
                );
            }

            if (code != 200) {
                return FormValidation.warning(
                    "Return code: " + code + " (Token: " + accessToken + ")"
                );
            }

            return FormValidation.ok();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return "Send build status to a Particle device";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            this.accessToken = formData.getString("accessToken");
            save();
            return super.configure(req, formData);
        }

    }

}
