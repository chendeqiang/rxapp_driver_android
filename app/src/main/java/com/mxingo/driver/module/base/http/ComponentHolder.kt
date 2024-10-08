package com.mxingo.driver.module.base.http

/**
 *
 * 由于 Kotlin 中没有 static 方法，因此相应的方法会在 companion object(对象声明)中生成。
 * 如果仍然需要从 Java 中调用这些方法，需要添加@JvmStatic注解
 */
object ComponentHolder {
    @JvmStatic
    var appComponent: AppComponent? = null

//    java代码：
//    private AppComponent appComponent;
//
//    public AppComponent getAppComponent() {
//        return appComponent;
//    }
//
//    public void setAppComponent(AppComponent appComponent) {
//        this.appComponent = appComponent;
//    }
}