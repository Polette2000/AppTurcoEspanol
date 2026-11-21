package com.example.appturcoespanol.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import com.example.appturcoespanol.model.Idioma
import java.util.Locale

class TextToSpeechManager(context: Context) : TextToSpeech.OnInitListener {

    private var motor: TextToSpeech? = null
    private var inicializado = false

    init {
        motor = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        inicializado = status == TextToSpeech.SUCCESS
    }

    // Compatibilidad con el código anterior.
    fun hablar(texto: String): Boolean = hablar(texto, Idioma.ESPANOL)

    // Nueva reproducción en español o turco.
    fun hablar(texto: String, idioma: Idioma): Boolean {
        if (!inicializado || texto.isBlank()) return false
        val tts = motor ?: return false

        val locale = if (idioma == Idioma.TURCO) {
            Locale.forLanguageTag("tr-TR")
        } else {
            Locale.forLanguageTag("es-ES")
        }

        val disponibilidad = tts.setLanguage(locale)
        if (disponibilidad == TextToSpeech.LANG_MISSING_DATA ||
            disponibilidad == TextToSpeech.LANG_NOT_SUPPORTED
        ) {
            return false
        }

        return tts.speak(
            texto,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "esturk_${System.nanoTime()}"
        ) == TextToSpeech.SUCCESS
    }

    fun detener() {
        motor?.stop()
    }

    fun liberar() {
        motor?.stop()
        motor?.shutdown()
        motor = null
        inicializado = false
    }
}
