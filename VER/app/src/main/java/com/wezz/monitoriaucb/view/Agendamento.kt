package com.wezz.monitoriaucb.view

import android.graphics.Color
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.wezz.monitoriaucb.R
import com.wezz.monitoriaucb.databinding.ActivityAgendamentoBinding

class Agendamento : AppCompatActivity() {

    private lateinit var binding: ActivityAgendamentoBinding
    private val calendar: java.util.Calendar = java.util.Calendar.getInstance()
    private var data: String = ""
    private var hora: String = ""


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgendamentoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.hide()
        val nome = intent.extras?.getString("nome").toString()

        val dataPicker = binding.datePicker
        dataPicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->

            calendar.set(Calendar.YEAR,year)
            calendar.set(Calendar.MONTH,monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)

            var dia = dayOfMonth.toString()
            val mes: String

            if(dayOfMonth < 10) {
                dia = "0$dayOfMonth"
            }
            if (monthOfYear < 10) {
                mes = "" + (monthOfYear)

            } else {
                mes = (monthOfYear +1).toString()
            }
            data = "$dia / $mes / $year"
        }
        binding.timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->

            val minuto: String

            if(minute < 10) {
                minuto = "@$minute"
            } else {
                minuto = minute.toString()
            }
            hora = "$hourOfDay:$minuto"
        }
        binding.timePicker.setIs24HourView(true)

        binding.btAgendar.setOnClickListener {

            val monitor1 = binding.monitor1
            val monitor2 = binding.monitor2
            val monitor3 = binding.monitor3

            when{
                hora.isEmpty() -> {
                    mensagem(it, "Preencha o horário", "#FF0000")

                }
                hora <"0:00" && hora > "19:00" -> {
                    mensagem(it, "Não há monitores disponíveis - horário de atendimento das 08:00 as 19:00!", "#FF0000")
                }
                data.isEmpty() -> {
                    mensagem(it, "Coloque uma data!", "#FF0000")

                }
                monitor1.isChecked && data.isEmpty() && hora.isEmpty() -> {
                    salvarAgendamento(it,nome,"Wesley Lima",data,hora)

                }
                monitor2.isChecked && data.isNotEmpty() && hora.isEmpty() -> {
                    salvarAgendamento(it,nome,"Maiara Cordeiro",data,hora)


                }
                monitor3.isChecked && data.isNotEmpty() && hora.isEmpty() -> {
                    salvarAgendamento(it,nome,"Ariane Sousa",data,hora)


                }
                else -> {
                    mensagem(it, "Escolha um monitor!", "#FF0000")

                }
            }
        }
    }
    private fun mensagem(view: View, mensagem: String, cor: String){
        val snackbar = Snackbar.make(view,mensagem,Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor(cor))
        snackbar.setTextColor(Color.parseColor("#FFFFFF"))
        snackbar.show()
    }
    private fun salvarAgendamento(view: View, cliente: String, monitor: String, data: String, hora: String){
        val db = FirebaseFirestore.getInstance()

        val dadosUsuario = hashMapOf(
            "estudante" to cliente,
            "monitor" to monitor,
            "data" to data,
            "hota" to hora
        )

        db.collection("agendamento").document(cliente).set(dadosUsuario).addOnCompleteListener {
            mensagem(view, "Agendamento realizado com sucesso!", "#FF03DAC5")
        }.addOnFailureListener {
            mensagem(view, "Erro ao salvar no servidor!", "#FF0000")
        }
    }
}