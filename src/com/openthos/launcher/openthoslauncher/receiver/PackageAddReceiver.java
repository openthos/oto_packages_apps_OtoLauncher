package com.openthos.launcher.openthoslauncher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

public class PackageAddReceiver extends BroadcastReceiver {
    private String mAppNeedFonts = "persit.app.fontsapp";
    private String mTag = "OpenthosFontsAPP";
    private String mFontPath = "/system/fonts";
    private String mSimHeiPath = "SimHei.ttf";
    private Context mContext;
    private ParseAppPath mParseAppPath;

    public void onReceive(final Context context, final Intent intent) {
        mContext =context;
        mParseAppPath = new ParseAppPath(mContext);
        ArrayList<String>applistString = mParseAppPath.getAppList();
        //Log.d(mTag,"Get app list:" + applistString);
        String pakageName = intent.getData().getSchemeSpecificPart();
        //Log.d(mTag,"Install app: " + pakageName);
        for (int i = 0; i < applistString.size(); i++) {
            //Log.d(mTag,"app: " + applistString.get(i));
            if (applistString.get(i).matches(pakageName)) {
                //Log.d(mTag,"will resolve app: " + pakageName);
                Map<String, String> appPath = mParseAppPath.getAppPrivatePath(pakageName);
                for (Map.Entry<String, String> entry : appPath.entrySet()) {
                    //Log.d(mTag,"App Info" + entry.getKey() + " " + entry.getValue());
                    checkDir(entry.getValue());
                    copyFile(entry.getKey(), entry.getValue());
                }
            }
        }
        linkFont(pakageName);
    }

    private void checkDir(String path) {
        File file = new File(path);
        if(!file.exists()){
            //Log.d(mTag,"create path: " + path);
            String[] cmd = new String[]{"su", "-c", "mkdir -p " + path.replace(" ", "\\ ")};
            nativeCmd(cmd);
        } else {
            //Log.e(mTag,path + " exists");
        }
    }

    private void CopyFonts(String packageName) {
        ParseAppPath parseAppPath = new ParseAppPath(mContext);
        Map<String,String> appPath = parseAppPath.getAppPrivatePath(packageName);
        for (Map.Entry<String, String> entry : appPath.entrySet()) {
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            copyFile(entry.getKey(), entry.getValue());
        }
    }

    private void copyFile(String source, String destPath) {
        String[] cmd = new String[]{"su", "-c", "cp "
                + source.replace(" ","\\ ") + " " + destPath.replace(" ","\\ ")};
        //Log.d(mTag,"copyFile " + "ln -s "
        //      + source.replace(" ","\\ ") + " " + destPath.replace(" ","\\ "));
        nativeCmd(cmd);
        return;
    }

    private void linkFont(String packageName) {
        String[] cmd =
               new String[] {"su", "-c", "ls /data/data/ -l|awk '/" + packageName + "/ {print $2}'"};
        //Log.d(mTag," " + "ls /data/data/ -l|awk '/" + packageName + "/ {print $2}'");
        String app = nativeCmd(cmd);
        cmd = new String[]{"su", "-c", "chown -R "
               + app + ":" + app + " /data/data/" + packageName.replace(" ","\\ ")};
        String result = nativeCmd(cmd);
        //Log.d(mTag," cmd: " + "chown -R "
        //     + app + ":" + app +" /data/data/" + packageName.replace(" ","\\ "));
        //Log.d(mTag," result: " + result);
        return;
    }

    private String nativeCmd(String[] cmd) {
        Process process = null;
        String result = "";
        try {
            String line = "";
            process = Runtime.getRuntime().exec(cmd);
            BufferedReader input = new BufferedReader(
                   new InputStreamReader(process.getInputStream()));
            while ((line = input.readLine()) != null) {
                if (!TextUtils.isEmpty(line)){
                 result = line;
                }
                //Log.d(mTag, "nativeCmd shell output: " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
