# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# --- Obfuscation Dictionary (Arabic) ---
-obfuscationdictionary obfuscation-dictionary.txt
-classobfuscationdictionary obfuscation-dictionary.txt
-packageobfuscationdictionary obfuscation-dictionary.txt

# --- Advanced Obfuscation ---
# 允许改变访问修饰符，极大增加混淆机会
# -allowaccessmodification
# 允许更激进的重载混淆
-overloadaggressively
# 混淆包结构：将所有混淆后的类移动到根包下，完全隐藏 com.iceberry 路径
-repackageclasses ''
# 扁平化包结构
# -flattenpackagehierarchy ''
# 混淆所有包名
-packageobfuscationdictionary obfuscation-dictionary.txt

# 强制允许对 com.iceberry 下的所有类进行混淆，即使它们被其他规则 keep 了类名
-keep,allowobfuscation class com.iceberry.** { *; }
-keep,allowobfuscation interface com.iceberry.** { *; }
-keep,allowobfuscation enum com.iceberry.** { *; }

# --- General Android ---
-keepattributes !SourceFile,!LineNumberTable
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# --- NotifyBox specific ---
# 移除之前的特定包 keep，由上方的通配符 allowobfuscation 处理

# --- Kotlin Serialization ---
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable *;
}
-keepclassmembers class ** {
    @kotlinx.serialization.SerialName *;
}
-keepclassmembers class * {
    *** Companion;
}
-keep @kotlinx.serialization.Serializable class *
-keepclassmembers class * {
    static ** Companion;
}
-keep @kotlinx.serialization.Serializable class * {
    public static ** Companion;
}
-keepclassmembers class * {
    @kotlinx.serialization.Transient *;
}

# --- EventBus ---
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# --- Hilt / Dagger ---
# 仅保留 ViewModel 的构造函数，允许类名和内部方法混淆
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    public <init>(...);
}
-keep,allowobfuscation class * extends androidx.lifecycle.ViewModel

# --- Protobuf Lite ---
-keep class com.google.protobuf.** { *; }

# --- MPAndroidChart ---
-keep class com.github.mikephil.charting.** { *; }
-dontwarn com.github.mikephil.charting.**

# --- Timber ---
# Optionally strip Timber log calls in release
-assumenosideeffects class timber.log.Timber {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
}

# --- Room ---
# 仅保留 Database 类名，其实现由 Room 生成，无需保留所有成员
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# --- Support / Jetpack ---
-dontwarn androidx.paging.compose.**
-dontwarn androidx.compose.ui.platform.**
