package com.example.costofelectricity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.round

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Sininen pääväri kaikille komponenteille
            MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF3F51B5))) {
                // Taustapinta koko ruudulle
                Surface(modifier = Modifier.fillMaxSize()) {
                    // Piirretään oma näkymä
                    ElectricityCost()
                }
            }
        }
    }
}

@Composable
fun ElectricityCost() {
    // Tilamuuttujat: kun nämä muuttuvat, ruutu piirtyy uudelleen
    var consumptionText by remember { mutableStateOf("") }   // tekstikentän sisältö
    var useReducedVat by remember { mutableStateOf(false) }  // onko checkbox päällä

    // Sliderin tila: muistaa sliderin arvon (0–1), alussa 0
    val sliderState = remember { SliderState(0f) }

    // Laskenta
    // Pyöristetään hinta kahteen desimaaliin: 0.2537 -> 0.25
    val pricePerKwH = round(sliderState.value * 100) / 100

    // Teksti numeroksi. Jos tyhjä tai virheellinen -> 0
    val consumptionKwH = consumptionText.toFloatOrNull() ?: 0f

    // ALV on 24 %, paitsi jos checkbox on päällä -> 10 %
    var vatPercent = 24
    if (useReducedVat) {
        vatPercent = 10
    }

    val cost = consumptionKwH * pricePerKwH           // hinta ilman veroa
    val payment = cost + cost * vatPercent / 100      // hinta + vero
    val paymentRounded = round(payment).toInt()       // kokonaisluvuksi

    // Käyttöliittymä: kaikki allekkain
    Column(modifier = Modifier.padding(16.dp)) {
        // Otsikko
        Text(text = "Cost of electricity", fontSize = 22.sp)

        // Kulutuksen syöttökenttä (numeronäppäimistö)
        OutlinedTextField(
            value = consumptionText,
            onValueChange = { consumptionText = it },
            label = { Text("Consumption in kWh") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Hinta tekstinä sliderin yläpuolella
        Text(text = "Price/kWh $pricePerKwH", modifier = Modifier.padding(top = 16.dp))

        // Slider: pallo-nuppi
        Slider(
            state = sliderState,
            onValueChange = { sliderState.value = it },  // tallennetaan uusi arvo
            modifier = Modifier.padding(horizontal = 8.dp),
            thumb = {
                // Nupin paikalle 20 dp:n sininen pallo
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            },
            track = {
                // Ohut rata ilman rakoa ja ilman pistettä lopussa
                SliderDefaults.Track(
                    sliderState = sliderState,
                    modifier = Modifier.height(4.dp),   // ohut rata
                    thumbTrackGapSize = 0.dp,           // ei rakoa pallon ympärille
                    drawStopIndicator = null            // ei pistettä oikeaan reunaan
                )
            }
        )

        // Checkbox ja sen teksti vierekkäin
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = useReducedVat, onCheckedChange = { useReducedVat = it })
            Text(text = "VAT 10%")
        }

        // Tuloslaatikko keskellä
        Surface(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "$paymentRounded€", modifier = Modifier.padding(12.dp))
        }
    }
}