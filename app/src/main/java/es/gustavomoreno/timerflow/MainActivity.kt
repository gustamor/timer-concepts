package es.gustavomoreno.timerflow

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import es.gustavomoreno.timerflow.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var current: Int = 0
    private var duration: Int = 10
    private var isRunning = false

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtMain.text = "clock"
        current = duration

        binding.fabMain.setOnClickListener {
            GlobalScope.launch {
                toggleIsRunning()
                //startCountDownWorkDelay()
                //startCountDownTimerScheduler()
                //startCountDownTimerClock()
                //startCountDownSystemClock()
                startCountDownWorkManager()
            }

        }
    }

    fun toggleIsRunning() {
        isRunning = !isRunning
    }

    private fun startCountDownWorkDelay() {
        current = duration
        CoroutineScope(Dispatchers.IO).launch {
            while (current > -1) {
                runOnUiThread {
                    binding.txtMain.text = current.toString()
                }

                delay(1000)
                Log.i("seconds", current.toString())
                runOnUiThread {
                    binding.txtMain.text = current.toString()
                }
                current--
            }
            current = 0
        }
    }

    fun startCountDownTimerScheduler() {
        val timer = Timer()

        val task = object : TimerTask() {

            override fun run() {
                if (isRunning) {
                    if (current > 0) {
                        runOnUiThread {
                            binding.txtMain.text = current.toString()
                        }
                        current--
                    } else {
                        current = 0
                        runOnUiThread {
                            binding.txtMain.text = current.toString()
                        }
                        timer.cancel()
                        isRunning = false
                        current = duration
                    }
                }
            }
        }
        timer.scheduleAtFixedRate(task, 0, 1000)
    }

    private fun startCountDownTimerClock() {
        var elapsed = duration
        if (isRunning) {
            runOnUiThread {
                object : CountDownTimer(duration.toLong() * 1000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        isRunning = true
                        elapsed--
                        binding.txtMain.text = elapsed.toString()
                    }

                    override fun onFinish() {
                        elapsed = 0
                        isRunning = false
                        binding.txtMain.text = elapsed.toString()
                    }
                }.start()
            }
        }

    }

    private fun currentTime(): Long {
        return SystemClock.elapsedRealtime()
    }

    private suspend fun startCountDownSystemClock() {
        var current = duration
        val startedAt = currentTime()
        runOnUiThread {
            binding.txtMain.text = current.toString()
        }
        while (current > -1) {
            val elapsedTime: Long =
                startedAt - currentTime() // the time that passed after the last time the user clicked the button
            if (elapsedTime % 1000 == 0.toLong()) {
                delay(1)
                current--
                runOnUiThread {
                    binding.txtMain.text = current.toString()
                }
            }
        }
    }

    private fun startCountDownWorkManager() {
        val workRequest = OneTimeWorkRequestBuilder<MyWorker>()
            .setExpedited(OutOfQuotaPolicy.DROP_WORK_REQUEST)
            .build()
        toggleIsRunning()
        WorkManager.getInstance(this).enqueue(workRequest)
    }

}

class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val timer = Timer()
        var current = 10
        var isRunning = true
        val task = object : TimerTask() {
            override fun run() {
                if (isRunning) {
                    if (current > 0) {
                        Log.d("sec", current.toString())
                        current--
                    } else {
                        current = 0
                        Log.d("sec", current.toString())

                        timer.cancel()
                        isRunning = false
                        current = 10
                    }
                }
            }
        }
        timer.scheduleAtFixedRate(task, 0, 1000)
        return Result.success()
    }

}



