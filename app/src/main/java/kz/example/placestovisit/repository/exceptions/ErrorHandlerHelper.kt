package kz.example.placestovisit.repository.exceptions

import android.content.Context
import com.google.gson.Gson
import io.reactivex.Single
import kz.example.placestovisit.R
import kz.example.placestovisit.model.ErrorModel
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

class RestApiException(val error: String?) : Exception()

class NoConnectionException(override val message: String) : Throwable()

fun <T> Single<Response<T>>.mapBodyMessages(gson: Gson): Single<T> = map {
    if (it.isSuccessful) {
        it.body()
    } else {
        val errorResponse = gson.fromJson(it.errorBody()?.string(), ErrorModel::class.java)
        throw RestApiException(errorResponse.error?.info ?: "")
    }
}

fun <T> Single<T>.mapNetworkErrors(gson: Gson): Single<T> =
    this.onErrorResumeNext { error ->
        error.printStackTrace()
        when (error) {
            is NoConnectionException -> Single.error(NoConnectionException(error.message))
            is SocketTimeoutException, is ConnectException, is UnknownHostException, is SSLException -> {
                Single.error(NoConnectionException("Error occurred retry later"))
            }
            is HttpException -> {
                val errorBody = error.response()?.errorBody()?.string()
                val response = gson.fromJson(errorBody, ErrorModel::class.java)
                Single.error(Throwable(response.error?.info ?: ""))
            }
            else -> Single.error(Throwable(error.message))
        }
    }