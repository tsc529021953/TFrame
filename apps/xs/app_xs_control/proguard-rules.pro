# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see:
#   http://developer.android.com/guide/developing/tools/proguard.html

################ARouter#################
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

# 如果使用了 byType 的方式获取 Service，需添加下面规则，保护接口
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider

# 如果使用了 单类注入，即不定义接口实现 IProvider，需添加下面规则，保护实现
-keep class * implements com.alibaba.android.arouter.facade.template.IProvider

#logg
-keep class ch.qos.** { *; }
-keep class org.slf4j.** { *; }

#实体类不参与混淆
#自定义控件不参与混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepnames class * implements java.io.Serializable
-keep public class * implements java.io.Serializable {*;}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object writeResolve();
}
-keepattributes Signature
-keep class **.R$*{*;}
-ignorewarnings
-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclasseswithmembernames class * { # 保持native方法不被混淆
    native <methods>;
}

-keepclassmembers enum * {  # 使用enum类型时需要注意避免以下两个方法混淆，因为enum类的特殊性，以下两个方法会被反射调用，
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

##---------------Begin: proguard configuration for Gson  ----------
-keepattributes Signature
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**

# Application classes that will be serialized/deserialized over Gson
-keep class com.sc.xs_control.bean.** { <fields>; }

-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

##---------------End: proguard configuration for Gson  ----------

################liveeventbus###############
-dontwarn com.jeremyliao.liveeventbus.**
-keep class com.jeremyliao.liveeventbus.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.arch.core.** { *; }

################Timber#################
-dontwarn org.jetbrains.annotations.**

-keep class ch.qos.** { *; }
-keep class org.slf4j.** { *; }

################实体类#################
-keep class com.sc.xs_control.bean.** { *; }
-keep class com.sc.xs_control.constant.** { *; }
