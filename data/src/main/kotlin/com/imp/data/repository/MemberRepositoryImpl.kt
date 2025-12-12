package com.imp.data.repository

import android.annotation.SuppressLint
import com.imp.data.BuildConfig
import com.imp.data.mapper.CommonMapper
import com.imp.data.remote.api.ApiMember
import com.imp.data.util.ApiClient
import com.imp.data.util.HttpConstants
import com.imp.data.util.extension.isSuccess
import com.imp.domain.model.AddressModel
import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.model.MemberModel
import com.imp.domain.repository.MemberRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

/**
 * Member Repository Implementation
 */
@SuppressLint("CheckResult")
class MemberRepositoryImpl @Inject constructor() : MemberRepository {

    /** Member Data */
    private var memberData: MemberModel? = null

    /**
     * Login
     */
    override suspend fun login(id: String, password: String, token: String, successCallback: (MemberModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {

        val params: MutableMap<String, Any> = HashMap()

        params["id"] = id
        params["password"] = password
        params["token"] = token

        ApiClient.getClient().create(ApiMember::class.java).login(params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->

                if (response.isSuccess()) {
                    response.data?.let {
                        memberData = it
                        successCallback.invoke(it)
                    }
                } else {
                    errorCallback.invoke(CommonMapper.mappingErrorCallbackData(response))
                }

            }, { error ->
                errorCallback.invoke(CommonMapper.mappingErrorData(error))
            })
    }

    /**
     * Register
     */
    override suspend fun register(data: MemberModel, successCallback: (MemberModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {

        val params: MutableMap<String, Any> = HashMap()

        params["id"] = data.id ?: ""
        params["password"] = data.password ?: ""
        params["name"] = data.name ?: ""
        params["birth"] = data.birth ?: ""
        params["address"] = data.address
        params["gender"] = data.gender ?: "N"

        ApiClient.getClient().create(ApiMember::class.java).register(params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->

                if (response.isSuccess()) {
                    response.data?.let { successCallback.invoke(it) }
                } else {
                    errorCallback.invoke(CommonMapper.mappingErrorCallbackData(response))
                }

            }, { error ->
                errorCallback.invoke(CommonMapper.mappingErrorData(error))
            })
    }

    /**
     * Check Email Validation
     */
    override suspend fun checkEmail(id: String, successCallback: (Boolean) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {

        ApiClient.getClient().create(ApiMember::class.java).checkEmail(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->

                successCallback.invoke(response.isSuccess())

            }, { error ->
                errorCallback.invoke(CommonMapper.mappingErrorData(error))
            })
    }

    /**
     * Search Address
     */
    override suspend fun searchAddress(search: String, successCallback: (AddressModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {

        Retrofit.Builder()
            .baseUrl(HttpConstants.KAKAO_BASE_HOST)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiMember::class.java)
            .search(
                key = "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}",
                query = search
            )
            .enqueue(object: Callback<AddressModel> {
                override fun onResponse(call: Call<AddressModel>, response: Response<AddressModel>) {

                    response.body()?.let { successCallback.invoke(it) }
                }
                override fun onFailure(call: Call<AddressModel>, t: Throwable) {

                    errorCallback.invoke(CommonMapper.mappingErrorData(t))
                }
            })
    }

    /**
     * Get Member Data
     */
    override suspend fun getMemberData(successCallback: (MemberModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {

        if (memberData != null) {
            successCallback.invoke(memberData!!)
            return
        }
    }

    /**
     * Edit Profile
     */
    override suspend fun editProfile(data: MemberModel, successCallback: (MemberModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {

        val params: MutableMap<String, Any> = HashMap()

        params["name"] = data.name ?: ""
        params["birth"] = data.birth ?: ""
        params["address"] = data.address
        params["gender"] = data.gender ?: "N"

        ApiClient.getClient().create(ApiMember::class.java).editProfile(data.id?: "", params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->

                if (response.isSuccess()) {
                    response.data?.let {
                        memberData = it
                        successCallback.invoke(it)
                    }
                } else {
                    errorCallback.invoke(CommonMapper.mappingErrorCallbackData(response))
                }

            }, { error ->
                errorCallback.invoke(CommonMapper.mappingErrorData(error))
            })
    }
}
