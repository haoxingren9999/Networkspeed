# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\android-sdk-windows\sdk/tools/proguard/proguard-android.txt
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

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-ignorewarnings
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes *Annotation*,*Exceptions*,Signature

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep	class	com.dianxinos.DXStatService.stat.TokenManager	{
	public	static	java.lang.String	getToken(android.content.Context);
}

-dontwarn com.google.android.**

-dontwarn com.nineoldandroids.**
-keep class com.nineoldandroids.** { *; }
-keep interface com.nineoldandroids.** { *; }
-keep public class * extends com.nineoldandroids.**
-keep public class * extends android.graphics.drawable.Drawable

-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep class android.support.v4.app.Fragment { *; }

-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.app.** { *; }
-keep public class * extends android.support.v7.**

-keep public class cn.android.vip.feng.**{*;}
-keep public interface cn.android.vip.feng.** {*;}
-keep public class com.sevensdk.ge.** {*;}
-keep class com.shere.ad.**{*;}

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

-keep class com.google.android.gms.ads.identifier.** { *; }
-keep class com.google.android.gms.common.ConnectionResult {
    int SUCCESS;
}

-keep class com.google.android.gms.common.GooglePlayServicesUtil {
    int isGooglePlayServicesAvailable (android.content.Context);
}

-assumenosideeffects class android.util.Log { public static *** d(...); public static *** v(...); public static *** i(...);public static *** e(...);}

-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep class !android.support.v7.internal.view.menu.*MenuBuilder*,android.support.** {*;}

-keep class com.shere.easynetworkspeed.mvp.** { *; }
-keep public class * extends android.support.v7.app.AppCompatActivity
-keep class com.shere.easynetworkspeed.setting.SettingPresenter

-keepattributes SourceFile,LineNumberTable
