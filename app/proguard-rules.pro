# Project-specific ProGuard/R8 rules.
# Dependency-provided consumer rules are intentionally not duplicated here.

# --- Obfuscation Dictionary ---
-obfuscationdictionary obfuscation-dictionary.txt
-classobfuscationdictionary obfuscation-dictionary.txt
-packageobfuscationdictionary obfuscation-dictionary.txt

# --- Diagnostics / Kotlin Metadata ---
-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature,InnerClasses,EnclosingMethod
-renamesourcefileattribute SourceFile

# --- Android Manifest Entry Points ---
-keep class com.iceberry.notifybox.NBApplication
-keep class com.iceberry.notifybox.MainActivity
-keep class com.iceberry.notifybox.bg.service.AppNotificationListenerService
-keep class com.iceberry.notifybox.bg.service.NearByAdvertisingService
-keep class com.iceberry.notifybox.bg.service.NearByDiscoverService

# --- Navigation Typed Routes ---
# Typed navigation uses serializable route class names at runtime.
-keepnames class com.iceberry.notifybox.navigation.Route
-keepnames class com.iceberry.notifybox.navigation.Route$*
# Enum route arguments are resolved by Navigation using their default FQCN.
-keep enum com.iceberry.notifybox.ui.setting.SettingOptions$ImportMethod { *; }

# --- Protobuf Lite Generated Messages ---
# Lite runtime reflects over generated message field names such as isAnlsRunning_.
-keepclassmembers class com.iceberry.notifybox.datastore.** extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
}

# --- Enum Runtime Access ---
# Used by enumConstants, Navigation arguments, and Kotlin enum entries access.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    public static ** getEntries();
}
