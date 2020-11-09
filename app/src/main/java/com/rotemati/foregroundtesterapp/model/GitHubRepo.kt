package com.rotemati.foregroundtesterapp.model

import com.google.gson.annotations.SerializedName
import com.rotemati.foregroundtesterapp.webservices.Service

class GitHubRepo(private val service: Service) {

    suspend fun getRepos(): List<Repo> {
        return service.getRepos("rotman")
    }
}

data class Repo(
    @SerializedName("id")
    var id: String,
    @SerializedName("node_id")
    var nodeId: String
)