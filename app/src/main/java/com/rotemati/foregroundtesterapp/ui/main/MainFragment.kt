package com.rotemati.foregroundtesterapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.rotemati.foregroundtesterapp.R
import com.rotemati.foregroundtesterapp.model.GitHubRepo
import com.rotemati.foregroundtesterapp.webservices.getNetworkService
import kotlinx.android.synthetic.main.main_fragment.*

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
			viewModel.onFetchReposButtonClicked()
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