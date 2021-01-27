package us.mifeng.jetpacket1.utils;

import android.app.Application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//通过全局变量来管理context 上下文对象，通过反射的方式来获取AcitivityThread 中currentThread()方法来获取Application对象
public class AppGlobals {
    private static Application mApplilcation;

    public static Application getmApplilcation() {
        if (null == mApplilcation) {


            //1.通过反射获取currentApplication()方法
            try {
                Method method = Class.forName("android.app.ActivityThread").getDeclaredMethod("currentApplication");
                //2。调用该方法就可以获取Application的实例
                try {
                    mApplilcation = (Application) method.invoke(null, null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return mApplilcation;
    }

}
