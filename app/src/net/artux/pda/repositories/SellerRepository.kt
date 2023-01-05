package net.artux.pda.repositories

import net.artux.pdanetwork.api.DefaultApi
import net.artux.pdanetwork.model.SellerDto
import net.artux.pdanetwork.model.Status
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class SellerRepository @Inject constructor(
    private val webservice: DefaultApi,
    private val cache: Cache<SellerDto>,
) {

    fun clearCache() {
        cache.clear()
    }

    fun getCachedSeller(id: Long): Result<SellerDto> {
        val result = cache.get(id.toString())
        return if (result != null)
            Result.success(result)
        else Result.failure(Exception("Seller isn't found"))
    }

    suspend fun getSeller(id: Long): Result<SellerDto> {
        return suspendCoroutine {
            webservice.getSeller(id).enqueue(object : Callback<SellerDto> {
                override fun onResponse(
                    call: Call<SellerDto>,
                    response: Response<SellerDto>
                ) {
                    val data = response.body()
                    if (data != null) {
                        cache.put(id.toString(), data)
                        it.resume(Result.success(data))
                    } else
                        it.resume(Result.failure(Exception("Seller null")))
                }

                override fun onFailure(call: Call<SellerDto>, t: Throwable) {
                    it.resume(Result.failure(java.lang.Exception(t)))
                }

            })
        }
    }

    suspend fun actionWithItem(
        operationType: OperationType,
        uuid: UUID,
        sellerId: Long,
        quantity: Int
    ): Result<Status> {
        return suspendCoroutine {
            webservice.actionWithItem(operationType.name, sellerId, uuid, quantity)
                .enqueue(object : Callback<Status> {
                    override fun onResponse(
                        call: Call<Status>,
                        response: Response<Status>
                    ) {
                        val data = response.body()
                        if (data != null) {
                            it.resume(Result.success(data))
                        } else
                            it.resume(Result.failure(Exception("Error with item")))
                    }

                    override fun onFailure(call: Call<Status>, t: Throwable) {
                        it.resume(Result.failure(java.lang.Exception(t)))
                    }

                })
        }
    }

    enum class OperationType {
        BUY,
        SELL
    }

}