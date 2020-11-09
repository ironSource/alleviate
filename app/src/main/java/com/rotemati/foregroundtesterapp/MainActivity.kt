package com.rotemati.foregroundtesterapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rotemati.foregroundtesterapp.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    companion object {
        const val TEST = "TESTTTTT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
//        val foregroundJobInfo = ForegroundJobInfo(
//            id = 11200,
//            networkType = JobInfo.NETWORK_TYPE_ANY,
//            isPersisted = true,
//            minLatencyMillis = TimeUnit.SECONDS.toMillis(0),
//            timeout = 30000,
//                notification = notification,
//                foregroundObtainer = ReposForegroundObtainer(),
//            rescheduleOnFail = true
//        )
//        val intent = Intent(this, TestActivity::class.java).apply {
//            putExtra(TEST, foregroundJobInfo)
//        }
//        startActivity(intent)
    }
}