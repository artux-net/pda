package net.artux.pda.scripting

import org.luaj.vm2.Globals

interface ICommandController {
    fun getLuaGlobals(): Globals
    fun openNotification(args: List<String>)
    fun clearCache()
    fun cacheCommands(commands: Map<String, List<String>>)
    fun cacheCommand(key: String, params: List<String>)
    fun openSeller(args: List<String>)
    fun openStage(list: List<String>)
    fun exitStory()
    fun showAd(types: List<String>)
    fun showAd(type: String)
    fun runLua(scripts: List<String>)
}