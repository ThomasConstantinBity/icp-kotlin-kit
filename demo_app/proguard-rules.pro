-printseeds proguard/seeds.txt
-printusage proguard/unused.txt
-printmapping proguard/mapping.txt

# Keep Kotlin's default constructor markers
-keep class kotlin.jvm.** { *; }

# Keep parameter names for reflection
# -keepattributes *Annotation*, Signature, EnclosingMethod, InnerClasses, SourceFile, LineNumberTable, LocalVariableTable, LocalVariableTypeTable

-keepattributes *Annotation*, Signature, InnerClasses, Synthetic, MethodParameters, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, Record, InnerClasses, EnclosingMethod
-keep,allowshrinking class kotlin.jvm.internal.DefaultConstructorMarker

-keep class kotlin.jvm.** { *; }
-keepclassmembers class kotlin.jvm.**$* { *; }
-keepclassmembernames class kotlin.jvm.**$* { *; }

-keep class kotlin.reflect.** { *; }
-keepclassmembers class kotlin.reflect.**$* { *; }
-keepclassmembernames class kotlin.reflect.**$* { *; }

-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-keep class com.bity.icp_kotlin_kit.data.datasource.** { *; }
-keepclassmembers class com.bity.icp_kotlin_kit.data.datasource.**$* { *; }
-keepclassmembernames class com.bity.icp_kotlin_kit.data.datasource.**$* { *; }

-keep class com.bity.icp_kotlin_kit.candid.** { *; }
-keepclassmembers class com.bity.icp_kotlin_kit.candid.**$* { *; }
-keepclassmembernames class com.bity.icp_kotlin_kit.candid.**$* { *; }

-keep class com.bity.icp_kotlin_kit.domain.generated_file.** { *; }
-keepclassmembers class com.bity.icp_kotlin_kit.domain.generated_file.** { *; }
-keepclassmembernames class com.bity.icp_kotlin_kit.domain.generated_file.**$* { *; }


# kotlin.jvm.internal.DefaultConstructorMarker
-keep class kotlin.jvm.internal.DefaultConstructorMarker.** { *; }

-keep class kotlin.Metadata { *; }

## Kotlin reflect
# Keep Metadata annotations so they can be parsed at runtime.
-keep class kotlin.Metadata { *; }
# Keep implementations of service loaded interfaces
# R8 will automatically handle these these in 1.6+
-keep interface kotlin.reflect.jvm.internal.impl.builtins.BuiltInsLoader
-keep class * implements kotlin.reflect.jvm.internal.impl.builtins.BuiltInsLoader { public protected *; }
-keep interface kotlin.reflect.jvm.internal.impl.resolve.ExternalOverridabilityCondition
-keep class * implements kotlin.reflect.jvm.internal.impl.resolve.ExternalOverridabilityCondition { public protected *; }
# Keep generic signatures and annotations at runtime.
# R8 requires InnerClasses and EnclosingMethod if you keepattributes Signature.
-keepattributes InnerClasses, Signature, RuntimeVisible*Annotations, EnclosingMethod, Annotation
# Don't note on API calls from different JVM versions as they're gated properly at runtime.
-dontnote kotlin.internal.PlatformImplementationsKt
# Don't note on internal APIs, as there is some class relocating that shrinkers may unnecessarily find suspicious.
-dontwarn kotlin.reflect.jvm.internal.**
-keep class kotlin.reflect.** { *; }
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-dontwarn kotlin.reflect.jvm.internal.**
-keep class kotlin.reflect.jvm.internal.** { *; }
-keepattributes Kotlin.Metadata
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }
-keepattributes *Annotation*, Signature, Kotlin.Metadata