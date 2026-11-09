package com.example.trilogi

import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trilogi.ui.theme.TrilogiTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrilogiTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FormulirBeasiswaScreen()
                }
            }
        }
    }
}

@Composable
fun FormulirBeasiswaScreen() {
    // STATE MANAGEMENT: rememberSaveable = padanan onSaveInstanceState di Compose.
    // Mencegah 'The Disappearing Data Problem' (state loss) saat layar diputar.
    var nama by rememberSaveable { mutableStateOf("") }
    var nim by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var ipk by rememberSaveable { mutableStateOf("") }

    // Pesan error spesifik per field
    var namaError by rememberSaveable { mutableStateOf<String?>(null) }
    var nimError by rememberSaveable { mutableStateOf<String?>(null) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var ipkError by rememberSaveable { mutableStateOf<String?>(null) }
    var statusMessage by rememberSaveable { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()          // agar tidak tertutup status bar / keyboard
            .padding(24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Formulir Pendaftaran Beasiswa",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider()

        // INPUT: Nama
        OutlinedTextField(
            value = nama,
            onValueChange = {
                nama = it
                namaError = null   // instant feedback: error hilang saat mengetik
            },
            label = { Text("Nama Lengkap") },
            isError = namaError != null,
            supportingText = { namaError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        // INPUT: NIM (8-10 digit angka)
        OutlinedTextField(
            value = nim,
            onValueChange = {
                nim = it
                nimError = null
            },
            label = { Text("NIM") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = nimError != null,
            supportingText = { nimError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        // INPUT: Email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
            },
            label = { Text("Email Mahasiswa") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = emailError != null,
            supportingText = { emailError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        // INPUT: IPK (Double 0.00 - 4.00)
        OutlinedTextField(
            value = ipk,
            onValueChange = {
                ipk = it
                ipkError = null
            },
            label = { Text("IPK") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = ipkError != null,
            supportingText = { ipkError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                var isValid = true

                // Validasi Nama
                if (nama.isBlank()) {
                    namaError = "Nama wajib diisi!"
                    isValid = false
                }

                // Validasi NIM: hanya angka, panjang 8-10 digit
                if (!nim.all { it.isDigit() } || nim.length !in 8..10) {
                    nimError = "NIM harus berupa angka 8 hingga 10 digit!"
                    isValid = false
                }

                // Validasi Email: Patterns.EMAIL_ADDRESS
                if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                    emailError = "Format email tidak valid!"
                    isValid = false
                }

                // Validasi IPK: koma diterima sebagai desimal; konversi aman dengan toDoubleOrNull()
                val ipkValue = ipk.trim().replace(',', '.').toDoubleOrNull()
                if (ipkValue == null || ipkValue < 0.0 || ipkValue > 4.0) {
                    ipkError = "IPK harus berupa angka antara 0.00 - 4.00!"
                    isValid = false
                }

                if (isValid && ipkValue != null) {
                    val ipkTeks = String.format(Locale.US, "%.2f", ipkValue)
                    statusMessage = "Pendaftaran Berhasil Disimpan!\n\n" +
                            "Nama: ${nama.trim()}\n" +
                            "NIM: $nim\n" +
                            "Email: ${email.trim()}\n" +
                            "IPK: $ipkTeks"
                } else {
                    statusMessage = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Pendaftaran")
        }

        // Output hasil
        if (statusMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = statusMessage,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Preview Formulir Beasiswa")
@Composable
fun FormulirBeasiswaScreenPreview() {
    TrilogiTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            FormulirBeasiswaScreen()
        }
    }
}