package com.rotemati.foregroundtesterapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.rotemati.foregroundsdk.foregroundtask.external.scheduler.ForegroundTasksSchedulerWrapper
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.foregroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.network.NetworkType
import com.rotemati.foregroundtesterapp.R
import com.rotemati.foregroundtesterapp.logger.TesterAppLogger
import com.rotemati.foregroundtesterapp.model.GitHubRepo
import com.rotemati.foregroundtesterapp.services.ReposForegroundService
import com.rotemati.foregroundtesterapp.webservices.getNetworkService
import kotlinx.android.synthetic.main.main_fragment.*
import java.util.concurrent.TimeUnit

class MainFragment : Fragment() {

	companion object {
		fun newInstance() = MainFragment()
	}

	private lateinit var viewModel: MainViewModel

	override fun onCreateView(
			inflater: LayoutInflater, container: ViewGroup?,
			savedInstanceState: Bundle?
	): View {
		return inflater.inflate(R.layout.main_fragment, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val repository = GitHubRepo(getNetworkService())
		viewModel = ViewModelProviders
				.of(this, MainViewModel.FACTORY(repository))
				.get(MainViewModel::class.java)

		fetchReposButton.setOnClickListener {
//			viewModel.onFetchReposButtonClicked()
			TesterAppLogger.d("fetchReposButton clicked")
			val foregroundTaskInfo = foregroundTaskInfo(11200) {
				networkType = NetworkType.Any
				persisted = true
				minLatencyMillis = TimeUnit.SECONDS.toMillis(0)
				timeoutMillis = TimeUnit.SECONDS.toMillis(15)
			}
			ForegroundTasksSchedulerWrapper().scheduleForegroundTask(
					ReposForegroundService::class.java,
					foregroundTaskInfo
			)
		}

		viewModel.spinner.observe(viewLifecycleOwner) { value ->
			spinner.visibility = if (value) View.VISIBLE else View.GONE
		}

		viewModel.snackbar.observe(viewLifecycleOwner) { value ->
			value?.let {
				Snackbar.make(view, value.toString(), Snackbar.LENGTH_SHORT).show()
				viewModel.onSnackbarShown()
			}
		}
	}
}