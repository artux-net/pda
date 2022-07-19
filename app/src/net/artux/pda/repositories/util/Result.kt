package net.artux.pda.repositories.util

sealed class Result<out T> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()

    fun isSuccess(): Boolean {
        return this !is Error;
    }

    fun <R> map(transform: (value: T) -> R): Result<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(exception)
        }
    }

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }

    fun getOrNull(): T? {
        return when (this){
            is Success -> data;
            is Error -> null;
        }
    }

    fun getOrDefault(defaultValue: @UnsafeVariance T): T {
        return when (this) {
            is Success -> data
            is Error -> defaultValue
        }
    }

    fun getOrThrow(): T {
        return when (this) {
            is Success -> data
            is Error -> throw RuntimeException("Error object.")
        }
    }

}