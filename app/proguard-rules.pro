# Add project specific ProGuard rules here.
# Minification is disabled by default (isMinifyEnabled = false) for this app,
# so these rules are kept minimal and only apply if minification is enabled later.

-keepattributes *Annotation*
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class com.bonaventure.psautier.data.** {
    <fields>;
}
