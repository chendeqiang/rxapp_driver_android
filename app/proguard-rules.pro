# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Applications/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#oss---------------------------------------------------
-keep class com.alibaba.sdk.android.oss.** { *; }
-dontwarn okio.**
-dontwarn org.apache.commons.codec.binary.**
#---------------------------------------------------
-dontwarn org.conscrypt.*

#retrofit2-----------------------------------------------
-dontwarn retrofit2.**
-keepclassmembernames,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

#eventbus--------------------------------------------
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# And if you use AsyncExecutor:
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#-----------------------------------------------------
-keep public class * extends android.app.Service

-keepattributes *Annotation*
-keepattributes Signature
-keepclassmembers enum * { *; }

#okhttp3-----------------------------------------------
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn sun.misc.**
#-----------------------------------------------
-keep class javax.annotation.** { *; }
-dontwarn javax.annotation.**

-keep class android.graphics.**  {* ;}
-dontwarn android.graphics.**
#个推
-dontwarn com.igexin.**
-keep class com.igexin.** { *; }

#androidx-------------------------------------------
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
#---------------------------------------------------

-keep class com.umeng.** {*;}

-keep class org.repackage.** {*;}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}



#rxjava-----------------------------------------------
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
#----------------------------------------------------------

#butterknife--------------------------------------------------------
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
#--------------------------------------------------------

# 保留所有的本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留R下面的资源
-keep class **.R$* {*;}

# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}


# Okio
-dontwarn com.squareup.**
-dontwarn okio.**
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }
#gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class com.bumptech.glide.** { *; }
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}


#org.conscrypt
-dontwarn org.conscrypt.**
-keep class org.conscrypt.** { *; }
-keep interface org.conscrypt.** { *; }


# Otto
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}
#七牛云
-keep class com.qiniu.**{*;}
-keep class com.qiniu.**{public <init>();}

#实体类
-keep class com.mxingo.driver.model.**{*;}
-keep class com.mxingo.driver.module.**{*;}


-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.support.multidex.MultiDexApplication
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService

##忽略警告，编译通过
-ignorewarnings


#3D 地图 V5.0.0之后：
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.**{*;}
-keep   class com.amap.api.trace.**{*;}

#定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
