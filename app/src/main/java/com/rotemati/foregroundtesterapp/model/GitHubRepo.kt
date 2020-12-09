package com.rotemati.foregroundtesterapp.model

import com.google.gson.annotations.SerializedName
import com.rotemati.foregroundtesterapp.webservices.Service
import java.util.concurrent.Future

class GitHubRepo(private val service: Service) {

	suspend fun getReposSuspend(): List<Repo> {
		return service.getReposSuspend("rotman")
	}

	fun getRepos(): Future<List<Repo>> {
		return service.getRepos("rotman")
	}
}

data class Repo(
		@SerializedName("id")
		var id: String,
		@SerializedName("node_id")
		var nodeId: String
)