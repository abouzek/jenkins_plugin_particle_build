package org.jenkinsci.plugins.particle;

import hudson.model.BallColor;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Form;

public final class ParticleUtil {

	public static final String BASE_API_STRING = "https://api.particle.io/v1/devices/";
    public static final String PUBLISH_API_STRING = BASE_API_STRING + "events";

    public static int sendParticleEvent(
        String accessToken, String eventName, String eventData) {
        try {
            int code = Request.Post(PUBLISH_API_STRING)
                        .bodyForm(Form.form()
                            .add("name", eventName)
                            .add("data", eventData)
                            .build()
                        )
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Accept", "*/*")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .execute().returnResponse().getStatusLine().getStatusCode();
            return code;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}