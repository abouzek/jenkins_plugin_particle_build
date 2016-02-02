package org.jenkinsci.plugins.particle;

import hudson.model.BallColor;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Form;

public final class ParticleUtil {

	public static final String BASE_API_STRING = "https://api.particle.io/v1/devices/";
    public static final String PUBLISH_API_STRING = BASE_API_STRING + "events";

    public static int sendParticleEvent(
        String accessToken, String eventName, BallColor ballColor) {
        try {
            int code = Request.Post(PUBLISH_API_STRING)
                        .bodyForm(Form.form()
                            .add("name", eventName)
                            .add("data", convertToString(ballColor))
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

    private static String convertToString(BallColor ballColor) {
        if (ballColor == BallColor.RED) {
            return "FAILURE";
        }
        else if (ballColor == BallColor.BLUE) {
            return "SUCCESS";
        }
        else if (ballColor == BallColor.YELLOW) {
            return "BUILDING";
        }
        else {
            return "NONE";
        }
    }

}