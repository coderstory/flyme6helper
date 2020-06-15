package com.coderstory.flyme.module;

import android.content.Context;

import com.coderstory.flyme.plugins.IModule;
import com.coderstory.flyme.utils.XposedHelper;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class FlymeRoot extends XposedHelper implements IModule {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {

        if (loadPackageParam.packageName.equals("com.meizu.mznfcpay") && prefs.getBoolean("HideRootWithPay", false)) {
            findAndHookMethod("com.meizu.cloud.a.a.a", loadPackageParam.classLoader, "c", Context.class, XC_MethodReplacement.returnConstant(false));
            // 6.0.7
            findAndHookMethod("com.meizu.cloud.a.a.a", loadPackageParam.classLoader, "b", Context.class, XC_MethodReplacement.returnConstant(false));
        }
        if (loadPackageParam.packageName.equals("com.meizu.flyme.update") && prefs.getBoolean("HideRootWithUpgrade", false)) {
            // DEVICE_STATE_SERVICE
            if (findClassWithoutLog("com.meizu.cloud.a.a.a", loadPackageParam.classLoader) != null) {
                findAndHookMethod("com.meizu.cloud.a.a.a", loadPackageParam.classLoader, "b", Context.class, XC_MethodReplacement.returnConstant(false));
            }
           //                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           findAndHookMethod("com.meizu.cloud.a.b.a", loadPackageParam.classLoader, "c", Context.class, XC_MethodReplacement.returnConstant(false));
        }
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam initPackageResourcesParam) {
    }
}