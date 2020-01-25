package com.app.klock.kgk.k.services;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Dhaval on 01/09/16.
 */
public class UnInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, AppCheckServices.class));
        // fetching package names from extras
        final PackageManager pm = context.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);


        for (ApplicationInfo packageInfo : packages) {
            Log.d("installed", "Installed package :" + packageInfo.packageName);
            Log.d("launchActivity", "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
        }
        if (packages != null) {

            for (ApplicationInfo packageInfo : packages) {
                String appPkgName = "applock.com.applock";
                if (appPkgName != null && appPkgName.equals(packageInfo.packageName)) {
                    // if (packageInfo != null && packageInfo.equals("applock.com.applock")) {
                    // User has selected our application under the Manage Apps settings
                    // now initiating background thread to watch for activity

                    Log.e("appPkgName", appPkgName + "");
                    new ListenActivities(context).start();

                }
            }
        }
    }

    private class ListenActivities extends Thread {

        boolean exit = false;
        ActivityManager am = null;
        Context context = null;

        public ListenActivities(Context con) {
            context = con;
            am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        }

        public void run() {

            Looper.prepare();

            while (!exit) {
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(5);
                ActivityManager mActivityManager = null;
                // ComponentName topActivity = mActivityManager.getRunningTasks(1).get(0).topActivity;

                // get the info from the currently running task

                String activityName = taskInfo.get(0).topActivity.getClassName();


                Log.d("topActivity", "CURRENT Activity ::"
                        + activityName);


                if (activityName.equals("com.android.packageinstaller.UninstallerActivity")) {
                    // User has clicked on the Uninstall button under the Manage Apps settings
                    Toast.makeText(context, "Done with preuninstallation tasks... Exiting Now", Toast.LENGTH_LONG).show();

                 /*   Intent i = new Intent(context, PasswordActivity.class);
                    context.startActivity(i);
*/
                    //do whatever pre-uninstallation task you want to perform here
                    // show dialogue or start another activity or database operations etc..etc..

                    // context.startActivity(new Intent(context, MyPreUninstallationMsgActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    exit = true;

                } else if (activityName.equals("com.android.settings.ManageApplications")) {
                    // back button was pressed and the user has been taken back to Manage Applications window
                    // we should close the activity monitoring now
                    exit = true;
                }
            }
            Looper.loop();
        }

    }
}
