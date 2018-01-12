package com.hypeagle.runtimepermission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/10.
 */

public class RPUtils {
    public static final String[] RUNTIME_PERMISSION = new String[]{
//            Manifest.permission_group.CALENDAR
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
//            Manifest.permission_group.CAMERA
            Manifest.permission.CAMERA,
//            Manifest.permission_group.CONTACTS
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
//            Manifest.permission_group.LOCATION
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission_group.MICROPHONE
            Manifest.permission.RECORD_AUDIO,
//            Manifest.permission_group.PHONE
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.ADD_VOICEMAIL,
            Manifest.permission.USE_SIP,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
//            Manifest.permission_group.SENSORS
            Manifest.permission.BODY_SENSORS,
//            Manifest.permission_group.SMS
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECEIVE_MMS,
//            Manifest.permission_group.STORAGE
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * @param context
     * @param permission
     * @return true：已授权； false：未授权
     */
    public static boolean checkPermission(Context context, String permission) {
        return permission != null && ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * @param context
     * @param permissions
     * @return 未授权权限列表
     */
    public static List<String> checkMorePermissions(Context context, String[] permissions) {
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (!checkPermission(context, permission))
                permissionList.add(permission);
        }
        return permissionList;
    }

    /**
     * @param context
     * @return 未授权运行时权限列表
     */
    public static List<String> checkRuntimesPermissions(Context context) {
        List<String> permissionList = new ArrayList<>();
        for (String permission : RUNTIME_PERMISSION) {
            if (!checkPermission(context, permission))
                permissionList.add(permission);
        }
        return permissionList;
    }

    /**
     * @param context
     * @param permission
     * @param requestCode
     */
    public static void requestPermission(Context context, String permission, int requestCode) {
        if (!(context instanceof Activity) || permission == null) {
            return;
        }

        ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode);
    }

    /**
     * @param context
     * @param permissionList
     * @param requestCode
     */
    public static void requestMorePermissions(Context context, List<String> permissionList, int requestCode) {
        if (permissionList == null) {
            return;
        }

        String[] permissions = (String[]) permissionList.toArray(new String[permissionList.size()]);
        requestMorePermissions(context, permissions, requestCode);
    }

    /**
     * @param context
     * @param permissions
     * @param requestCode
     */
    public static void requestMorePermissions(Context context, String[] permissions, int requestCode) {
        if (!(context instanceof Activity) || permissions == null || permissions.length == 0) {
            return;
        }

        ActivityCompat.requestPermissions((Activity) context, permissions, requestCode);
    }

    /**
     * @param grantResult
     * @return true 授权成功，false 授权失败
     */
    public static boolean isPermissionRequestSuccess(int grantResult) {
        return grantResult == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * @param context
     * @param permission
     * @return
     * @describe :如果应用之前请求过此权限但用户拒绝，此方法将返回 true;
     * -----------如果应用第一次请求权限 或
     * -----------用户在过去拒绝了权限请求并在权限请求系统对话框中选择了 Don't ask again 选项，此方法将返回 false。
     */
    public static boolean judgePermission(Context context, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission);
    }

    /**
     * @param context
     * @param permission
     * @param requestCode
     * @return true 需要申请权限，false 不需要申请权限
     */
    public static boolean checkAndRequestPermission(Context context, String permission, int requestCode) {
        if (!checkPermission(context, permission)) {
            requestPermission(context, permission, requestCode);
            return true;
        }

        return false;
    }

    /**
     * @param context
     * @param permissions
     * @param requestCode
     * @return true 需要申请权限，false 不需要申请权限
     */
    public static boolean checkAndRequestMorePermissions(Context context, String[] permissions, int requestCode) {
        List<String> permissionList = checkMorePermissions(context, permissions);
        if (permissionList.size() > 0) {
            requestMorePermissions(context, permissionList, requestCode);
            return true;
        }

        return false;
    }

    /**
     * 跳转到setting
     *
     * @param context
     */
    public static void toAppSetting(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(intent);
    }
}
