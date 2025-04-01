# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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
-dontwarn java.lang.invoke.StringConcatFactory

# Menjaga sealed class Resource dan subclass-nya
-keep class com.saefulrdevs.mubeego.core.data.Resource { *; }
-keep class com.saefulrdevs.mubeego.core.data.Resource$* { *; }

# Menjaga semua model domain
-keep class com.saefulrdevs.mubeego.core.domain.model.** { *; }

# Menjaga repository dan use case
-keep class com.saefulrdevs.mubeego.core.domain.repository.** { *; }
-keep class com.saefulrdevs.mubeego.core.domain.usecase.** { *; }

# Menjaga module dependency injection (Koin)
-keep class com.saefulrdevs.mubeego.core.di.** { *; }

# Menjaga utility class
-keep class com.saefulrdevs.mubeego.core.util.** { *; }

-keep class com.saefulrdevs.mubeego.favorite.** { *; }