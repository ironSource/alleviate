package com.rotemati.foregroundtesterapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rotemati.foregroundsdk.external.scheduler.ForegroundTasksSchedulerWrapper
import com.rotemati.foregroundsdk.external.taskinfo.foregroundTaskInfo
import com.rotemati.foregroundsdk.external.taskinfo.network.NetworkType
import com.rotemati.foregroundtesterapp.model.GitHubRepo
import com.rotemati.foregroundtesterapp.services.ReposForegroundService
import java.util.concurrent.TimeUnit

class MainViewModel(private val repository: GitHubRepo) : ViewModel() {

	companion object {
		/**
		 * Factory for creating [MainViewModel]
		 *
		 * @param arg the repository to pass to [MainViewModel]
		 */
		val FACTORY = singleArgViewModelFactory(::MainViewModel)
	}

	private val _spinner = MutableLiveData(false)
	val spinner: LiveData<Boolean>
		get() = _spinner

	private val _snackBar = MutableLiveData<String?>()
	val snackbar: LiveData<String?>
		get() = _snackBar

	fun onSnackbarShown() {
		_snackBar.value = null
	}

	fun onFetchReposButtonClicked() {
		val foregroundTaskInfo = foregroundTaskInfo(11200) {
			networkType = NetworkType.Any
			persisted = true
			minLatencyMillis = TimeUnit.SECONDS.toMillis(5)
			timeoutMillis = TimeUnit.SECONDS.toMillis(7)
		}
		ForegroundTasksSchedulerWrapper().scheduleForegroundTask(
				ReposForegroundService::class.java,
				foregroundTaskInfo
		)
		//			foregroundTasksSchedulerWrapper.cancel(11200)
	}
}