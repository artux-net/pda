package net.artux.pda.repositories

import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LuaHelper @Inject constructor(
    val commanController: CommandController
) {

    fun showNotification() {
        runScript("notificationController.")
    }

    fun runScript(script: String) {
        commanController.process(mapOf(Pair("lua", listOf(script))))
    }


}