package controller.client.AccessGoogle;

import utils.EnvConfig;

public class Constants {

    // ===== FACEBOOK =====
    public static final String FACEBOOK_APP_ID =
            EnvConfig.get("FACEBOOK_APP_ID");

    public static final String FACEBOOK_APP_SECRET =
            EnvConfig.get("FACEBOOK_APP_SECRET");

    public static final String FACEBOOK_REDIRECT_URL =
            EnvConfig.get("FACEBOOK_REDIRECT_URL");

    public static final String FACEBOOK_LINK_GET_TOKEN =
            "https://graph.facebook.com/v20.0/oauth/access_token";

    public static final String FACEBOOK_LINK_GET_USER_INFO =
            "https://graph.facebook.com/me?fields=id,name,email,picture&access_token=";

    // ===== GOOGLE =====
    public static final String GOOGLE_CLIENT_ID =
            EnvConfig.get("GOOGLE_CLIENT_ID");

    public static final String GOOGLE_CLIENT_SECRET =
            EnvConfig.get("GOOGLE_CLIENT_SECRET");

    public static final String GOOGLE_REDIRECT_URI =
            EnvConfig.get("GOOGLE_REDIRECT_URI");

    public static final String GOOGLE_LINK_GET_TOKEN =
            "https://accounts.google.com/o/oauth2/token";

    public static final String GOOGLE_LINK_GET_USER_INFO =
            "https://www.googleapis.com/oauth2/v1/userinfo?access_token=";

    public static final String GOOGLE_GRANT_TYPE =
            "authorization_code";
}
