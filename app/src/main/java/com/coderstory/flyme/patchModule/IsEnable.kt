package com.coderstory.flyme.patchModule


import com.coderstory.flyme.tools.XposedHelper
import com.coderstory.flyme.xposed.IModule
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class IsEnable : XposedHelper(), IModule {
    override fun handleInitPackageResources(resparam: InitPackageResourcesParam) {}
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.packageName == "com.coderstory.flyme") {
            XposedHelper.Companion.findAndHookMethod("com.coderstory.flyme.activity.MainActivity", lpparam.classLoader, "isEnable", XC_MethodReplacement.returnConstant(true))
        }
    }

    override fun initZygote(paramStartupParam: StartupParam?) {}
}