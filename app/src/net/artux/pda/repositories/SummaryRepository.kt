package net.artux.pda.repositories

import net.artux.pda.model.Summary
import net.artux.pda.model.chat.UserMessage
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummaryRepository @Inject constructor(
    private val summaryCache: Cache<Summary>
) {

    fun clear() {
        summaryCache.clear()
    }

    fun remove(id: String) {
        summaryCache.remove(id)
    }

    fun getCachedSummary(id: String): Result<Summary> {
        val cache = summaryCache.get(id)
        return if (cache != null)
            Result.success(cache)
        else Result.failure(Exception("Cache isn't found"))
    }

    fun putSummary(id: String, summary: Summary) {
        summaryCache.put(id, summary)
    }

    fun updateSummary() {
        getCachedSummary(Summary.currentId)
            .onSuccess {
                it.messages.addAll(summaryMessages)
                putSummary(it.title, it)
            }
            .onFailure {
                val summary = Summary()
                summary.messages = summaryMessages
                putSummary(summary.title, summary)
            }
    }

    var summaryMessages: LinkedList<UserMessage> = LinkedList()

    fun check(message: UserMessage){
        summaryMessages.add(message)
    }

    fun getAll(): List<Summary> {
        return summaryCache.all
    }

}