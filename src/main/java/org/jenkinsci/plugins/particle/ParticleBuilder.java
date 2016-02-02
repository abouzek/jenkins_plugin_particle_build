package org.jenkinsci.plugins.particle;

import jenkins.model.Jenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BallColor;
import hudson.model.BuildListener;
import hudson.model.AbstractProject;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;

import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import javax.servlet.ServletException;

public class ParticleBuilder extends Builder {

	private final String eventName;

	@DataBoundConstructor
	public ParticleBuilder(String eventName) {
		this.eventName = eventName;
	}

	public String getEventName() {
		return eventName;
	}

	@Override
	public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
		BallColor ballColor = BallColor.YELLOW;
		String accessToken = Jenkins.getInstance().getDescriptorByType(ParticleNotifier.DescriptorImpl.class).getAccessToken();
		int code = ParticleUtil.sendParticleEvent(accessToken, getEventName(), ballColor);
        listener.getLogger().println("BUILD STEP Particle device return code: " + code);
		return (code == 200);	
	}

	@Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }
      
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return "Send build status to a Particle device";
        }

    }

}