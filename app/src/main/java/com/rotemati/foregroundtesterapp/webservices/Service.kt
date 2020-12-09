package com.rotemati.foregroundtesterapp.webservices

import com.rotemati.foregroundtesterapp.model.Repo
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.Future

private val service: Service by lazy {
	val okHttpClient = OkHttpClient.Builder()
			.build()

	val retrofit: Retrofit = Retrofit.Builder()
			.baseUrl("https://api.github.com/")
			.client(okHttpClient)
			.addConverterFactory(GsonConverterFactory.create())
			.build()

	retrofit.create(Service::class.java)
}

fun getNetworkService() = service

interface Service {
	@GET("users/{user}/repos")
	suspend fun getReposSuspend(@Path("user") user: String?): List<Repo>

	@GET("users/{user}/repos")
	fun getRepos(@Path("user") user: String?): Future<List<Repo>>
}