package com.negroroberto.uhealth.unity;

import com.negroroberto.uhealth.utils.Debug;
import com.unity3d.player.UnityPlayer;

public final class AppSocket {
    public enum SendType {
        BG_COLOR, ANIM_RUN_TRUE, ANIM_RUN_FALSE, ANIM_GREETINGS, AVATAR_REMOVE, AVATAR_CREATE, SPEECH, CAMERA_TARGET_HEAD, CAMERA_TARGET_UPPER_BODY, CAMERA_TARGET_HALF_BODY, CAMERA_ANGLE, CAMERA_DISTANCE, CAMERA_HEIGHT, TEST, ERROR_PARAMS, ERROR_COMMAND
    }

    public enum ReceiveType {
        UNITY_LOADED, AVATAR_LOADING, AVATAR_FINISH, SPEECH_START, SPEECH_FINISH, ERROR_COMMAND, ERROR_PARAMS
    }

    public static void SendCommand(SendType sendType, String... parameters) {
        switch (sendType) {
            case BG_COLOR:
                SendRawValue(AppProtocol.CreateCommand(AppProtocol.AppToUnity.BG_COLOR, parameters));
                break;
            case ANIM_RUN_TRUE:
                SendRawValue(AppProtocol.CreateCommand(AppProtocol.AppToUnity.ANIM_RUN, AppProtocol.AppToUnity.PARAM_TRUE));
                break;
            case ANIM_RUN_FALSE:
                SendRawValue(AppProtocol.CreateCommand(AppProtocol.AppToUnity.ANIM_RUN, AppProtocol.AppToUnity.PARAM_FALSE));
                break;
            case ANIM_GREETINGS:
                SendRawValue(AppProtocol.CreateCommand(AppProtocol.AppToUnity.ANIM_GREETINGS));
                break;
            case AVATAR_REMOVE:
                SendRawValue(AppProtocol.CreateCommand(AppProtocol.AppToUnity.AVATAR_REMOVE));
                break;
            case AVATAR_CREATE:
                SendRawValue(AppProtocol.CreateCommand(AppProtocol.AppToUnity.AVATAR_CREATE, parameters));
                break;
            case SPEECH:
                SendRawValue(AppProtocol.CreateCommand(AppProtocol.AppToUnity.SPEECH, parameters));
                break;
            case CAMERA_TARGET_HEAD:
                SendRawValue(AppProtocol.CreateCommand(AppProtocol.AppToUnity.CAMERA_TARGET, AppProtocol.AppToUnity.PARAM_CAMERA_TARGET_HEAD));
                break;
            case CAMERA_TARGET_UPPER_BODY:
                SendRawValue(AppProtocol.CreateCommand(AppProtocol.AppToUnity.CAMERA_TARGET, AppProtocol.AppToUnity.PARAM_CAMERA_TARGET_UPPER_BODY));
                break;
            case CAMERA_TARGET_HALF_BODY:
                SendRawValue(AppProtocol.CreateCommand(AppProtocol.AppToUnity.CAMERA_TARGET, AppProtocol.AppToUnity.PARAM_CAMERA_TARGET_HALF_BODY));
                break;
            case CAMERA_ANGLE:
                SendRawValue(AppProtocol.CreateCommand(AppProtocol.AppToUnity.CAMERA_ANGLE, parameters));
                break;
            case CAMERA_DISTANCE:
                SendRawValue(AppProtocol.CreateCommand(AppProtocol.AppToUnity.CAMERA_DISTANCE, parameters));
                break;
            case CAMERA_HEIGHT:
                SendRawValue(AppProtocol.CreateCommand(AppProtocol.AppToUnity.CAMERA_HEIGHT, parameters));
                break;
            case TEST:
                SendRawValue(AppProtocol.CreateCommand(AppProtocol.AppToUnity.TEST));
                break;
            case ERROR_PARAMS:
                SendRawValue(AppProtocol.CreateCommand(AppProtocol.AppToUnity.ERROR_PARAMS));
                break;
            case ERROR_COMMAND:
            default:
                SendRawValue(AppProtocol.CreateCommand(AppProtocol.AppToUnity.ERROR_COMMAND));
                break;
        }
    }

    private static void CheckReceivedValue(String value) {
        if (AppProtocol.IsCommand(value)) {
            String command = AppProtocol.GetCommand(value);

            switch (command) {
                case AppProtocol.UnityToApp.UNITY_LOADED:
                    if (mReceiveListener != null)
                        mReceiveListener.onReceive(ReceiveType.UNITY_LOADED);
                    break;
                case AppProtocol.UnityToApp.AVATAR_LOADING:
                    if (mReceiveListener != null)
                        mReceiveListener.onReceive(ReceiveType.AVATAR_LOADING);
                    break;
                case AppProtocol.UnityToApp.AVATAR_FINISH:
                    if (mReceiveListener != null)
                        mReceiveListener.onReceive(ReceiveType.AVATAR_FINISH);
                    break;
                case AppProtocol.UnityToApp.SPEECH_START: {
                    String filename = AppProtocol.GetParam(value);
                    if (mReceiveListener != null)
                        mReceiveListener.onReceive(ReceiveType.SPEECH_START, filename);
                }
                break;
                case AppProtocol.UnityToApp.SPEECH_FINISH: {
                    String filename = AppProtocol.GetParam(value);
                    if (mReceiveListener != null)
                        mReceiveListener.onReceive(ReceiveType.SPEECH_FINISH, filename);
                }
                break;
                case AppProtocol.UnityToApp.ERROR_COMMAND:
                    Debug.Err(AppSocket.class, "Received error from Unity: Invalid command");
                    if (mReceiveListener != null)
                        mReceiveListener.onReceive(ReceiveType.ERROR_COMMAND);
                    break;
                case AppProtocol.UnityToApp.ERROR_PARAMS:
                    Debug.Err(AppSocket.class, "Received error from Unity: Wrong parameters");
                    if (mReceiveListener != null)
                        mReceiveListener.onReceive(ReceiveType.ERROR_PARAMS);
                    break;
                default:
                    SendCommand(SendType.ERROR_COMMAND);
                    break;
            }
        }
    }

    private static OnReceiveListener mReceiveListener;

    public static void SetOnReceiveListener(OnReceiveListener listener) {
        mReceiveListener = listener;
    }

    public interface OnReceiveListener {
        void onReceive(ReceiveType receiveType, String... parameters);
    }

    private static void SendRawValue(String value) {
        Debug.Log(AppSocket.class, "Sent: " + value);

        UnityPlayer.UnitySendMessage(AppProtocol.UNITY_GAMEOBJECT, AppProtocol.UNITY_METHOD, value);
    }

    public static void ReceiveRawValue(String value) {
        Debug.Log(AppSocket.class, "Received: " + value);

        CheckReceivedValue(value);
    }
}
