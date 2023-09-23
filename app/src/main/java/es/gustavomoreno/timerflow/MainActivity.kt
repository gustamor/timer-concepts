package es.gustavomoreno.timerflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import es.gustavomoreno.timerflow.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var current: Int = 0
    private var duration: Int = 10

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtMain.text = "clock"
        current = duration
        binding.fabMain.setOnClickListener {

            GlobalScope.launch {
                startCountDownWorkDelay()
            }

        }
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

    private fun startCountDownSystemClock() {}
    private fun startCountDownTimerSchedueler() {}
    private fun startCountDownWorkManager() {}

}



