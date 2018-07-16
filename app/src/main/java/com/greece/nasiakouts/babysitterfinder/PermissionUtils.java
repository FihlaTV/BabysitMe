package com.greece.nasiakouts.babysitterfinder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

public class PermissionUtils {
    public static int requestCode = 111;

    public static String[] neededPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void checkAndAskForPermissions(Activity activity) {
        if(!arePermissionsGranted(activity)){
            ActivityCompat.requestPermissions(activity, neededPermissions, requestCode);
        }
    }

    public static boolean arePermissionsGranted(Context context){
        if(context == null) return true;

        for(String permission : neededPermissions){
            if(ActivityCompat
                    .checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    public static void onPermissionResult(Context context,
                                          int code,
                                          @NonNull String[] permissions,
                                          @NonNull int[] grantedResults){
        if(code != requestCode) return;

        for(int result : grantedResults){
            if(result != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(context,
                        context.getString(R.string.permissions_not_granted),
                        Toast.LENGTH_LONG).show();
                break;
            }
        }
    }
}
