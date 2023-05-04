package net.artux.pda.repositories

import net.artux.pda.model.Summary
import net.artux.pda.model.chat.UserMessage
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

    fun updateSummary(messages: MutableList<UserMessage>) {
        getCachedSummary(Summary.currentId)
            .onSuccess {
                it.messages.addAll(messages)
                putSummary(it.title, it)
            }
            .onFailure {
                val summary = Summary()
                summary.messages = messages
                putSummary(summary.title, summary)
            }
    }

    fun getAll(): List<Summary> {
        return summaryCache.all
    }

}