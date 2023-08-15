package net.artux.pda.scripting

import kotlinx.coroutines.Job
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaTable

@Suppress("redundantVisibilityModifier")
interface ICommandController {

    public fun getLuaGlobals(): Globals
    public fun openNotification(args: List<String>)
    public fun openNotification(title: String, message: String)
    public fun openSeller(args: List<String>)
    public fun openSeller(sellerId: Int)
    public fun openStage(list: List<String>)

    /**
     * Открытие стадии через :
     * @property arg строка - chapterId:stageId или stageId
     */
    public fun openStage(arg: String)
    public fun openStage(chapterId: Int, stageId: Int)
    public fun openStage(stageId: Int)

    /**
     * Показ рекламы
     * @property type принимает значения video и simple
     */
    public fun showAd(type: String)

    /**
     * Показ рекламы, работает как @method showAd
     */
    public fun showAd(types: List<String>)

    /**
     * Выполняет команды на сервере без команды-триггера
     */
    fun processWithServer(actions: Map<String, List<String>>): Job

    /**
     * Кэширует команды, они выполнятся при следующей команде-триггер (syncNow, exitStory, finishStory)
     */
    fun process(commands: Map<String, List<String>>?)
}