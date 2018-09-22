package com.negroroberto.uhealth.unity;

public final class AppProtocol {
    //region Android-side constants
    public static final String UNITY_GAMEOBJECT = "SceneScripts";
    public static final String UNITY_METHOD = "ReceiveRawValue";
    //endregion

    //region Unity -> App
    public static final class UnityToApp {
        public static final String UNITY_LOADED = "UNITY_LOADED";

        public static final String ERROR_COMMAND = "ERROR_COMMAND";
        public static final String ERROR_PARAMS = "ERROR_PARAMS";

        public static final String AVATAR_LOADING = "AVATAR_LOADING";
        public static final String AVATAR_FINISH = "AVATAR_FINISH";

        public static final String SPEECH_START = "SPEECH_START";
        public static final String SPEECH_FINISH = "SPEECH_FINISH";
    }
    //endregion

    //region App -> Unity
    public static final class AppToUnity {
        public static final String PARAM_TRUE = "TRUE";
        public static final String PARAM_FALSE = "FALSE";

        public static final String PARAM_CAMERA_TARGET_HEAD = "HEAD";
        public static final String PARAM_CAMERA_TARGET_UPPER_BODY = "UPPER_BODY";
        public static final String PARAM_CAMERA_TARGET_HALF_BODY = "HALF_BODY";

        public static final String ERROR_COMMAND = "ERROR_COMMAND";
        public static final String ERROR_PARAMS = "ERROR_PARAMS";

        public static final String BG_COLOR = "BG_COLOR";
        // 1 string parameter: color (hex)

        public static final String SPEECH = "SPEECH";
        // 1 string parameter: filename

        public static final String ANIM_RUN = "ANIM_RUN";
        // 1 string parameter param_true or param_false

        public static final String ANIM_GREETINGS = "ANIM_GREETINGS";

        public static final String CAMERA_TARGET = "CAMERA_TARGET";
        // 1 string parameter: PARAM_CAMERA_TARGET_...
        public static final String CAMERA_ANGLE = "CAMERA_ANGLE";
        // 1 string parameter: angle
        public static final String CAMERA_DISTANCE = "CAMERA_DISTANCE";
        // 1 string parameter: distance
        public static final String CAMERA_HEIGHT = "CAMERA_HEIGHT";
        // 1 string parameter: height

        public static final String AVATAR_REMOVE = "AVATAR_REMOVE";
        public static final String AVATAR_CREATE = "AVATAR_CREATE";
        // 5 string parameters: player id, body id, avatar id, hair id, hair color (hex)

        public static final String TEST = "TEST";
    }
    //endregion

    //region Methods
    public static String CreateCommand(String cmd) {
        if (cmd == null)
            return null;

        String res = "#{" + cmd + "}";
        return res;
    }

    public static String CreateCommand(String cmd, String param) {
        if (cmd == null)
            return null;

        if (param == null)
            CreateCommand(cmd);

        String res = "#{" + cmd + "=" + param + "}";
        return res;
    }

    public static String CreateCommand(String cmd, String[] param) {
        if (cmd == null)
            return null;

        if (param == null)
            return CreateCommand(cmd);

        String res = "#{" + cmd + "=";
        for (int i = 0; i < param.length; i++) {
            res += param[i];
            if (i < param.length - 1)
                res += ",";
        }

        res += "}";
        return res;
    }

    public static boolean IsCommand(String s) {
        if (s == null)
            return false;

        return s.startsWith("#{") && s.endsWith("}");
    }

    public static String GetCommand(String s) {
        if (s == null)
            return null;

        String res = s.substring(2);
        if (res.contains("="))
            res = res.substring(0, res.indexOf('='));
        else
            res = res.substring(0, res.length() - 1);
        return res;
    }

    public static String GetParam(String s) {
        if (s == null)
            return null;

        String res = "";

        if (s.contains("=") && s.indexOf("=") < s.length() - 1) {
            res = s.substring(s.indexOf('=') + 1);
            res = res.substring(0, res.length() - 1);
        }

        return res;
    }

    public static String[] GetParams(String s) {
        if (s == null)
            return null;

        String param = GetParam(s);

        if (param.contains(","))
            return GetParam(s).split(",");
        else
            return new String[]{param};
    }
    //endregion
}

