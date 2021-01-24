package com.rotemati.foregroundtesterapp.model

import com.google.gson.annotations.SerializedName
import com.rotemati.foregroundtesterapp.webservices.Service
import retrofit2.Call

class GitHubRepo(private val service: Service) {

	fun getRepos(): Call<List<Repo>> {
		return service.getRepos("rotman")
	}
}

data class Repo(
		@SerializedName("id")
		var id: String,
		@SerializedName("node_id")
		var nodeId: String
)