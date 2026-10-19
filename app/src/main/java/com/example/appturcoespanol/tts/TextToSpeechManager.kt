
package com.example.appturcoespanol.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TextToSpeechManager(
    context: Context
) : TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null

    private var inicializado = false

    private var idiomaDisponible = false

    init {
        textToSpeech = TextToSpeech(
            context.applicationContext,
            this
        )
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {

            val resultado = textToSpeech?.setLanguage(
                Locale.forLanguageTag("es-ES")
            )

            inicializado = true

            idiomaDisponible =
                resultado != null &&
                        resultado != TextToSpeech.LANG_MISSING_DATA &&
                        resultado != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    fun hablar(texto: String): Boolean {

        if (!inicializado || !idiomaDisponible) {
            return false
        }

        val resultado = textToSpeech?.speak(
            texto,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "palabra_${System.nanoTime()}"
        )

        return resultado == TextToSpeech.SUCCESS
    }

    fun detener() {
        textToSpeech?.stop()
    }

    fun liberar() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()

        textToSpeech = null
        inicializado = false
        idiomaDisponible = false
    }
}
