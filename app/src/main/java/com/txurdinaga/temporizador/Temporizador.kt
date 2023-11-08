package com.txurdinaga.temporizador

import android.app.Service
import android.content.ContentValues.TAG
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class Temporizador : Service() {
    private lateinit var countdownTextView: TextView
    val countdownJob = CoroutineScope(Dispatchers.Default)


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var time = intent?.getIntExtra("time", 0).toString().toInt()

        // Simular la cuenta atrás
        countdownJob.launch {
            while (time>=0){
                Log.i(TAG, time.toString())
                val broadcastIntent = Intent("COUNTDOWN_UPDATE")
                broadcastIntent.putExtra("countdown", time.toString())
                sendBroadcast(broadcastIntent)
                time--
                // Realizar la cuenta atrás
                delay(1000)
                if (time <= 0) {
                    val mediaPlayer = MediaPlayer.create(applicationContext, R.raw.ping4)
                    mediaPlayer.start()
                }
            }

            // Autodestruir el servicio
            stopSelf()
        }

        return START_STICKY // Reiniciar el servicio en caso de ser destruido
    }

    override fun onDestroy() {
        countdownJob.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
