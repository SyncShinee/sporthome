/*
 * Copyright (C) 2021 Baidu, Inc. All Rights Reserved.
 */
package sporthome.demo.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import sporthome.demo.data.model.LoggedInUser;

import org.json.JSONObject;

public final class Preferences {
    public static SharedPreferences sSHARED_REFERENCES = null;
    private static Context sAPPLICATION_CONTEXT;
    private static String sDEVICE_ID;
    private static String sVERSION_NAME;
    private static int sUSER_ID = -1;

    public Preferences() {
    }

    public static boolean isLogin() {
        return getAccountId() > 0 ? true : false;
    }

    public static boolean isShowWelcome() {
        try {
            PackageInfo info = sAPPLICATION_CONTEXT.getPackageManager()
                    .getPackageInfo(sAPPLICATION_CONTEXT.getPackageName(), 0);
            // 当前版本的版本号
            int versionCode = info.versionCode;
            int saveVersionCode = sSHARED_REFERENCES.getInt("version_code", 0);
            return versionCode != saveVersionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }

    public static boolean updateVersionCode() {
        try {
            PackageInfo info = sAPPLICATION_CONTEXT.getPackageManager()
                    .getPackageInfo(sAPPLICATION_CONTEXT.getPackageName(), 0);
            // 当前版本的版本号
            int versionCode = info.versionCode;
            Editor editor = sSHARED_REFERENCES.edit();
            editor.putInt("version_code", versionCode);
            return editor.commit();
        } catch (NameNotFoundException e) {
            return false;
        }
    }


    public static void init(Context context) {

        if (sSHARED_REFERENCES == null) {
            sAPPLICATION_CONTEXT = context.getApplicationContext();
            sSHARED_REFERENCES = PreferenceManager
                    .getDefaultSharedPreferences(sAPPLICATION_CONTEXT);
        }
    }

    public static SharedPreferences getSharedPreferences() {
        return sSHARED_REFERENCES;
    }


    public static String getVersionName() {
        if (sVERSION_NAME == null) {
            try {
                PackageInfo info = sAPPLICATION_CONTEXT.getPackageManager()
                        .getPackageInfo(sAPPLICATION_CONTEXT.getPackageName(),
                                0);
                // 当前应用的版本名称
                sVERSION_NAME = info.versionName;
                // 当前版本的版本号
                // int versionCode = info.versionCode;

                // 当前版本的包名
                // String packageNames = info.packageName;
            } catch (NameNotFoundException e) {
            }
        }
        return sVERSION_NAME;
    }


    /**
     * 获取当前登录的用户
     *
     * @return 如果已经登录返回User，否则返回null
     */
    public static LoggedInUser getAccountUser() {
        LoggedInUser user = new LoggedInUser();
        sUSER_ID = sSHARED_REFERENCES.getInt("user_id", 0);
        if (sUSER_ID == 0) {
            return null;
        }
        JSONObject jsonObject;
        //          jsonObject = new JSONObject(sSHARED_REFERENCES.getString(
        //                  "user_json", null));
        user.setToken(sSHARED_REFERENCES.getString("access_token", null));
        user.setUserId(sSHARED_REFERENCES.getInt("user_id", 0));
        user.setDisplayName(sSHARED_REFERENCES.getString("bind_name", null));

        return user;
    }

    public static boolean setAccountUser(LoggedInUser user, String accessToken) {
        Editor editor = sSHARED_REFERENCES.edit();
        editor.putString("access_token", accessToken);
        editor.putInt("user_id", user.getUserId());
        editor.putString("bind_name", user.getDisplayName());

//      editor.putString("user_json", user.toJSON().toString());

        // TODO json format save
        if (editor.commit()) {
            sUSER_ID = user.getUserId();
            return true;
        }
        return false;
    }


    public static boolean setDeviceToken(String device_token){
        Editor editor = sSHARED_REFERENCES.edit();
        editor.putString("device_token", device_token);
        if(editor.commit()){
            return true;
        }
        return false;

    }

    public static String getDeviceToken(){
        String device_token = sSHARED_REFERENCES.getString("device_token", null);
        return device_token;
    }

    public static boolean userClear() {
        Editor editor = sSHARED_REFERENCES.edit();
        editor.putString("access_token", "");
        editor.putInt("user_id", 0);
        editor.putString("create_time", "");
        // TODO json format save
        if (editor.commit()) {
            sUSER_ID = 0;
            return true;
        }
        return false;
    }



    public static boolean updateAccountUser(LoggedInUser user) {
        Editor editor = sSHARED_REFERENCES.edit();
        editor.putInt("user_id", user.getUserId());
        //editor.putString("user_json", user.toJSON().toString());
        if (editor.commit()) {
            sUSER_ID = user.getUserId();
            return true;
        }
        return false;
    }

    /**
     * 获取登录用户的ID
     *
     * @return 如果已登录返回用户ID，否则返回0
     */
    public static int getAccountId() {
        if (sUSER_ID == -1) {
            sUSER_ID = sSHARED_REFERENCES.getInt("user_id", 0);
        }
        return sUSER_ID;
    }

    public static String getToken() {
        return sSHARED_REFERENCES.getString("access_token", null);
    }

}
