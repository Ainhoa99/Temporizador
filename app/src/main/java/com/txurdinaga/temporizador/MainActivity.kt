package com.txurdinaga.temporizador

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var countdownServiceIntent: Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val timeEditTextHour = findViewById<EditText>(R.id.timeEditTextHour)
        val timeEditTextMinute = findViewById<EditText>(R.id.timeEditTextMinute)
        val timeEditTextSecond = findViewById<EditText>(R.id.timeEditTextSecond)

        val startButton= findViewById<Button>(R.id.startButton)
        val stopButton= findViewById<Button>(R.id.stopButton)

        // Register the broadcast receiver
        val filter = IntentFilter("COUNTDOWN_UPDATE")
        registerReceiver(broadcastReceiver, filter)

        // Crear un intent para iniciar el servicio de descuento
        countdownServiceIntent = Intent(applicationContext, Temporizador::class.java)
        startButton.setOnClickListener {
            val time = calcularSegundos(timeEditTextHour.text.toString().toInt(), timeEditTextMinute.text.toString().toInt(), timeEditTextSecond.text.toString().toInt())
            // Pasar el tiempo de descuento al servicio
            countdownServiceIntent.putExtra("time", time.toString().toInt())


            // Iniciar el servicio de descuento
            startService(countdownServiceIntent)

            // Inhabilitar el botón de inicio
            startButton.isEnabled = false
            timeEditTextSecond.isEnabled= false
            timeEditTextHour.isEnabled= false
            timeEditTextMinute.isEnabled= false


            stopButton.isEnabled = true
            // Register the broadcast receiver
            val filter = IntentFilter("COUNTDOWN_UPDATE")
            registerReceiver(broadcastReceiver, filter)
        }

        stopButton.setOnClickListener {
            // Detener/destruir el servicio de descuento
            stopService(countdownServiceIntent)

            // Habilitar el botón de inicio
            startButton.isEnabled = true
            timeEditTextSecond.isEnabled= true
            timeEditTextHour.isEnabled= true
            timeEditTextMinute.isEnabled= true

            stopButton.isEnabled = false
        }

    }
    fun calcularSegundos(h:Int, m:Int, s:Int) : Int{
        return (h*3600) +(m*60) + s
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "COUNTDOWN_UPDATE"){
                val timeEditTextHour = findViewById<EditText>(R.id.timeEditTextHour)
                val timeEditTextMinute = findViewById<EditText>(R.id.timeEditTextMinute)
                val timeEditTextSecond = findViewById<EditText>(R.id.timeEditTextSecond)

                val tiempo = intent.getStringExtra("countdown")
                val resultado = ponerHoras(tiempo.toString().toInt())
                val (horas, minutos, segundos) = resultado
                timeEditTextSecond.setText(segundos.toString())
                timeEditTextMinute.setText(minutos.toString())
                timeEditTextHour.setText(horas.toString())

            }

        }
    }

    fun ponerHoras(seg:Int):Triple<Int, Int, Int>{
        val h = seg / 3600
        val m = (seg % 3600) /60
        val s = seg % 60
        return Triple(h, m, s)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister the broadcast receiver
        unregisterReceiver(broadcastReceiver)
    }
}