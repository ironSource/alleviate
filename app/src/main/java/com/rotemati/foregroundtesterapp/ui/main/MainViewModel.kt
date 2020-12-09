package com.rotemati.foregroundtesterapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotemati.foregroundtesterapp.model.GitHubRepo
import kotlinx.coroutines.launch

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
		viewModelScope.launch {
			try {
				_spinner.value = true
				val repos = repository.getReposSuspend()
				_snackBar.value = "${repos.size} repos fetched"
			} catch (exception: Exception) {
				_snackBar.value = exception.message
			} finally {
				_spinner.value = false
			}
		}
	}
}