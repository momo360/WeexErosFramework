package com.benmu.framework;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.benmu.framework.activity.AbstractWeexActivity;
import com.benmu.framework.adapter.router.RouterTracker;
import com.benmu.framework.constant.Constant;
import com.benmu.framework.debug.ws.DebuggerWebSocket;
import com.benmu.framework.extend.adapter.DefaultTypefaceAdapter;
import com.benmu.framework.manager.ManagerFactory;
import com.benmu.framework.manager.impl.GlobalEventManager;
import com.benmu.framework.manager.impl.LifecycleManager;
import com.benmu.framework.update.VersionChecker;
import com.taobao.weex.WXSDKInstance;

import java.util.List;

/**
 * Created by Carry on 2017/9/4.
 */

public class BMWXApplication extends Application {
    private static BMWXApplication mInstance;
    private WXSDKInstance mMediator;
    private VersionChecker mVersionChecker;
    private DebuggerWebSocket debugSocket;
    private DefaultTypefaceAdapter typefaceAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        if (shouldInit()) {
            mInstance = this;
            initWeex();
            mVersionChecker = new VersionChecker(this);
            registerLifecycle();
//            initShare();
            initDebugSocket();
        }
    }

    private void initDebugSocket() {
        debugSocket = new DebuggerWebSocket(this);
        debugSocket.init();
    }

    public DefaultTypefaceAdapter getTypefaceAdapter() {
        return typefaceAdapter;
    }

    public void setTypefaceAdapter(DefaultTypefaceAdapter typefaceAdapter) {
        this.typefaceAdapter = typefaceAdapter;
    }


    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(base);
        super.attachBaseContext(base);
    }


    private void registerLifecycle() {
        LifecycleManager lifecycleManager = ManagerFactory.getManagerService(LifecycleManager
                .class);
        lifecycleManager.register(this).setOnTaskSwitchListenner(new LifecycleManager
                .OnTaskSwitchListener() {


            @Override
            public void onTaskSwitchToForeground() {
                Activity activity = RouterTracker.peekActivity();
                if (activity != null) {
                    GlobalEventManager.appActive(((AbstractWeexActivity) activity)
                            .getWXSDkInstance());
                }
                //app resume  try check version
                if (mVersionChecker != null) {
                    mVersionChecker.checkVersion();
                }
            }

            @Override
            public void onTaskSwitchToBackground() {
                Activity activity = RouterTracker.peekActivity();
                if (activity != null) {
                    GlobalEventManager.appDeactive(((AbstractWeexActivity) activity)
                            .getWXSDkInstance());
                }
            }
        });
    }


    private void initWeex() {
        BMWXEngine.initialize(this, new BMInitConfig.Builder().isActiceInterceptor(Constant
                .INTERCEPTOR_ACTIVE).build());

    }


    public static BMWXApplication getWXApplication() {
        return mInstance;
    }
}
