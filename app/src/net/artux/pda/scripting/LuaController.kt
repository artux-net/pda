package net.artux.pda.scripting

import net.artux.pda.utils.TimberPrintStream
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaError
import org.luaj.vm2.WeakTable
import org.luaj.vm2.lib.DebugLib
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.luaj.vm2.lib.jse.JsePlatform
import timber.log.Timber
import javax.inject.Singleton


@Singleton
class LuaController {

    fun getLuaGlobals(): Globals = globals
    val globals = JsePlatform.standardGlobals()
        @JvmName("getLuaTable")
        get

    init {
        Timber.i("Lua init")
        globals.setmetatable(WeakTable.make(false, true))
        globals.load(DebugLib())
        globals.STDOUT = TimberPrintStream()
        globals.useWeakKeys()

        runLua(listOf("print 'hello from lua'"))
    }

    fun putObjectToScriptContext(name: String, o: Any?): LuaController {
        if (o != null) {
            globals.set(name, CoerceJavaToLua.coerce(o))
            Timber.i("Object was putted to Lua Context: $name (${o.javaClass.simpleName})")
        } else
            Timber.e("Object is not putted to Lua Context: $name")

        return this
    }

    fun runLua(scripts: List<String>) {
        for (script in scripts) {
            Timber.tag("LUA Script").i("Running Lua Script: \n$script")
            try {
                val chunk = globals.load(script, "script.lua")
                chunk.call()
            } catch (e: LuaError) {
                Timber.tag("LUA Script").e(e)
            }
        }
    }


}