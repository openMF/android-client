package com.mifos.feature.client.clientDetails.domain.usecase

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.ClientDetailsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 18/03/24.
 */

class UploadClientImageUseCase @Inject constructor(private val repository: ClientDetailsRepository) {

    operator fun invoke(id: Int, pngFile: File): Flow<Resource<ResponseBody>> = callbackFlow {
        try {
            trySend(Resource.Loading())
            val requestFile = pngFile.asRequestBody("image/png".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", pngFile.name, requestFile)
            repository.uploadClientImage(id, body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<ResponseBody>() {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        trySend(Resource.Error("Unable to update image"))
                    }

                    override fun onNext(response: ResponseBody) {
                        trySend(Resource.Success(response))
                    }
                })

            awaitClose { channel.close() }

        } catch (e: Exception) {
            trySend(Resource.Error(e.message.toString()))
        }
    }

}