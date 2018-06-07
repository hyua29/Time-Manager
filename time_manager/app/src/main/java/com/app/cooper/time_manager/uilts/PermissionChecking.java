package com.app.cooper.time_manager.uilts;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * check whether this application has the permission and redirect user to setting if permission not granted
 */
public class PermissionChecking {

    /**
     * check whether permissions are given to a particular activity
     * @param context
     * @param permissions
     * @return
     */
    public static boolean hasPermissions(Context context, String[] permissions) {
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {

                    listPermissionsNeeded.add(permission);
                }
            }
        }


        return true;
    }


    /**
     * redirect user to the setting
     */
    private void redirectToSetting() {
    }

}
