-keep class org.** { *; }
-keep class java.** { *; }
-keep class javax.** { *; }
-keep class com.badlogic.** { *; }

# required for lua scripting
-keep class net.artux.pda.map.di.components.** { *; }
-keep class net.artux.pda.scripting.** { *; }
-keep class net.artux.pda.map.engine.** { *; }
-keep class net.artux.pda.map.managers.** { *; }
-keep class net.artux.pda.map.repository.** { *; }
-keep class net.artux.pda.map.ecs.** { *; }
-keep class net.artux.pda.map.scenes.** { *; }
-keep class net.artux.pda.map.content.** { *; }


-verbose

-dontwarn android.support.**
-dontwarn com.badlogic.gdx.backends.android.AndroidFragmentApplication
-dontwarn com.badlogic.gdx.utils.GdxBuild
-dontwarn com.badlogic.gdx.physics.box2d.utils.Box2DBuild
-dontwarn com.badlogic.gdx.jnigen.BuildTarget*
-dontwarn com.badlogic.gdx.graphics.g2d.freetype.FreetypeBuild

-keep class com.badlogic.gdx.controllers.android.AndroidControllers

-keepclassmembers class com.badlogic.gdx.backends.android.AndroidInput* {
   <init>(com.badlogic.gdx.Application, android.content.Context, java.lang.Object, com.badlogic.gdx.backends.android.AndroidApplicationConfiguration);
}

-keepclassmembers class com.badlogic.gdx.physics.box2d.World {
   boolean contactFilter(long, long);
   void    beginContact(long);
   void    endContact(long);
   void    preSolve(long, long);
   void    postSolve(long, long);
   boolean reportFixture(long);
   float   reportRayFixture(long, float, float, float, float, float);
}

-verbose

-dontwarn com.badlogic.gdx.backends.android.AndroidFragmentApplication

# Required if using Gdx-Controllers extension
-keep class com.badlogic.gdx.controllers.android.AndroidControllers

-keep public class com.badlogic.** { *; }

-keep enum * { *; }

-keep public class net.artux.pda.model.** { *; }
-keep public class net.artux.pdanetwork.** { *; }
-keep public class net.artux.pda.map.engine.entities.** { *; }

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Gson specific classes
-keep class sun.** { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }
##---------------End: proguard configuration for Gson  ----------

# Required if using Box2D extension
-keepclassmembers class com.badlogic.gdx.physics.box2d.World {
   boolean contactFilter(long, long);
   void    beginContact(long);
   void    endContact(long);
   void    preSolve(long, long);
   void    postSolve(long, long);
   boolean reportFixture(long);
   float   reportRayFixture(long, float, float, float, float, float);
}

 # Keep generic signature of RxJava3 (R8 full mode strips signatures from non-kept items).
-keep class retrofit2.** { *; }
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# This is generated automatically by the Android Gradle plugin.
-dontwarn javax.script.AbstractScriptEngine
-dontwarn javax.script.Bindings
-dontwarn javax.script.Compilable
-dontwarn javax.script.CompiledScript
-dontwarn javax.script.ScriptContext
-dontwarn javax.script.ScriptEngine
-dontwarn javax.script.ScriptEngineFactory
-dontwarn javax.script.ScriptException
-dontwarn javax.script.SimpleBindings
-dontwarn javax.script.SimpleScriptContext
-dontwarn javax.servlet.http.HttpServletRequest
-dontwarn org.apache.bcel.classfile.Field
-dontwarn org.apache.bcel.classfile.JavaClass
-dontwarn org.apache.bcel.classfile.Method
-dontwarn org.apache.bcel.generic.AASTORE
-dontwarn org.apache.bcel.generic.ALOAD
-dontwarn org.apache.bcel.generic.ANEWARRAY
-dontwarn org.apache.bcel.generic.ASTORE
-dontwarn org.apache.bcel.generic.ArrayInstruction
-dontwarn org.apache.bcel.generic.ArrayType
-dontwarn org.apache.bcel.generic.BasicType
-dontwarn org.apache.bcel.generic.BranchHandle
-dontwarn org.apache.bcel.generic.BranchInstruction
-dontwarn org.apache.bcel.generic.ClassGen
-dontwarn org.apache.bcel.generic.CompoundInstruction
-dontwarn org.apache.bcel.generic.ConstantPoolGen
-dontwarn org.apache.bcel.generic.FieldGen
-dontwarn org.apache.bcel.generic.FieldInstruction
-dontwarn org.apache.bcel.generic.GETSTATIC
-dontwarn org.apache.bcel.generic.GOTO
-dontwarn org.apache.bcel.generic.IFEQ
-dontwarn org.apache.bcel.generic.IFNE
-dontwarn org.apache.bcel.generic.Instruction
-dontwarn org.apache.bcel.generic.InstructionConstants
-dontwarn org.apache.bcel.generic.InstructionFactory
-dontwarn org.apache.bcel.generic.InstructionHandle
-dontwarn org.apache.bcel.generic.InstructionList
-dontwarn org.apache.bcel.generic.InvokeInstruction
-dontwarn org.apache.bcel.generic.LineNumberGen
-dontwarn org.apache.bcel.generic.LocalVariableGen
-dontwarn org.apache.bcel.generic.LocalVariableInstruction
-dontwarn org.apache.bcel.generic.MethodGen
-dontwarn org.apache.bcel.generic.NEW
-dontwarn org.apache.bcel.generic.ObjectType
-dontwarn org.apache.bcel.generic.PUSH
-dontwarn org.apache.bcel.generic.PUTSTATIC
-dontwarn org.apache.bcel.generic.ReturnInstruction
-dontwarn org.apache.bcel.generic.StackInstruction
-dontwarn org.apache.bcel.generic.Type
-dontwarn org.int4.dirk.extensions.assisted.AssistedAnnotationStrategy
-dontwarn org.int4.dirk.extensions.assisted.AssistedTypeRegistrationExtension
-dontwarn org.int4.dirk.extensions.assisted.ConfigurableAssistedAnnotationStrategy
-dontwarn org.int4.dirk.extensions.proxy.ByteBuddyProxyStrategy
-dontwarn org.json.JSONWriter
-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn org.slf4j.impl.StaticMDCBinder
-dontwarn org.slf4j.impl.StaticMarkerBinder
