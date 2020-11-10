package com.rotemati.foregroundtesterapp.ui.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.rotemati.foregroundsdk.extensions.getNotificationManager
import com.rotemati.foregroundsdk.extensions.scheduleForeground
import com.rotemati.foregroundsdk.jobinfo.ForegroundJobInfo
import com.rotemati.foregroundsdk.notification.NotificationDescriptor
import com.rotemati.foregroundtesterapp.R
import com.rotemati.foregroundtesterapp.model.GitHubRepo
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

//            viewModel.onFetchReposButtonClicked()

			val channel = "General updates"
			val notificationChannel =
					NotificationChannel(channel, channel, NotificationManager.IMPORTANCE_DEFAULT)
			notificationChannel.setSound(null, null)
			context?.getNotificationManager()?.createNotificationChannel(notificationChannel)
			val notificationDescriptor = NotificationDescriptor(
                    title = "Rotem",
                    body = "Matityahu",
                    iconResId = R.drawable.ic_launcher_foreground
            )
			val foregroundJobInfo = ForegroundJobInfo(
                    id = 11200,
                    networkType = JobInfo.NETWORK_TYPE_ANY,
                    persisted = true,
                    minLatencyMillis = TimeUnit.SECONDS.toMillis(0),
                    timeout = TimeUnit.SECONDS.toMillis(10),
                    notificationDescriptor = notificationDescriptor,
//                foregroundObtainer = ReposForegroundObtainer(),
                    rescheduleOnFail = true,
                    maxRetries = 3
            )

			scheduleForeground(this.requireContext(), foregroundJobInfo)
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