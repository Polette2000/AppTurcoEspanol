package com.example.appturcoespanol.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appturcoespanol.model.Idioma

/** Primera pantalla: la persona elige su idioma ANTES de crear o abrir una cuenta. */
@Composable
fun BienvenidaScreen(
    onComenzar: () -> Unit,
    idiomaInterfaz: Idioma? = null,
    onSeleccionarIdioma: (Idioma) -> Unit = {}
) {
    val crema = Color(0xFFFFF5E5)
    val turquesa = Color(0xFF197E89)
    val oscuro = Color(0xFF243A53)
    val menta = Color(0xFFBCE9CE)
    val turco = idiomaInterfaz == Idioma.TURCO

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(crema)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 25.dp),
        verticalArrangement = Arrangement.spacedBy(17.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))
        Text("EsTurk", fontSize = 43.sp, fontWeight = FontWeight.ExtraBold, color = turquesa)
        Text("🦊  🐼  🐱", fontSize = 46.sp)
        Text(
            text = when (idiomaInterfaz) {
                Idioma.TURCO -> "Merhaba! Maceraya hoş geldin!"
                Idioma.ESPANOL -> "¡Hola! ¡Bienvenido a la aventura!"
                null -> "¡Hola! / Merhaba!"
            },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = oscuro,
            textAlign = TextAlign.Center
        )
        Text(
            text = when (idiomaInterfaz) {
                Idioma.TURCO -> "İspanyolca ve Türkçeyi oyun oynayarak keşfet."
                Idioma.ESPANOL -> "Aprende español y turco jugando."
                null -> "Elige tu idioma para comenzar. / Başlamak için dilini seç."
            },
            color = oscuro,
            textAlign = TextAlign.Center,
            fontSize = 15.sp
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (turco) "Hangi dili konuşuyorsun?" else
                        "¿Qué idioma hablas? / Hangi dili konuşuyorsun?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = oscuro,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Idioma.entries.forEach { idioma ->
                    val seleccionado = idiomaInterfaz == idioma
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(
                            if (seleccionado) 2.dp else 1.dp,
                            if (seleccionado) turquesa else Color(0xFFD9E7E3)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (seleccionado) menta else Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSeleccionarIdioma(idioma) }
                                .padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(if (idioma == Idioma.ESPANOL) "🇪🇸" else "🇹🇷", fontSize = 27.sp)
                            Text(
                                if (idioma == Idioma.ESPANOL) "Español" else "Türkçe",
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.SemiBold,
                                color = oscuro
                            )
                            RadioButton(
                                selected = seleccionado,
                                onClick = { onSeleccionarIdioma(idioma) }
                            )
                        }
                    }
                }
            }
        }

        if (idiomaInterfaz != null) {
            Text(
                text = if (turco) {
                    "Talimatlar Türkçe olacak. Çocuk İspanyolca öğrenecek."
                } else {
                    "Las instrucciones estarán en español. El niño aprenderá turco."
                },
                color = turquesa,
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
        }
        Button(
            onClick = onComenzar,
            enabled = idiomaInterfaz != null,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = turquesa)
        ) {
            Text(
                text = if (turco) "Maceraya başla!" else "¡Comenzar aventura!",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        Text(
            text = if (turco) "Dilini değiştirmek için buradan seçim yapabilirsin." else
                "Puedes cambiar el idioma antes de comenzar.",
            color = oscuro,
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )
    }
}
