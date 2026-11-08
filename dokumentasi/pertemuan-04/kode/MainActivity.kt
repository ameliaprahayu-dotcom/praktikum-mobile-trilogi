package com.example.trilogi

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trilogi.ui.theme.TrilogiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrilogiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    IntentDemoScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun IntentDemoScreen(modifier: Modifier = Modifier) {
    // LocalContext diperlukan untuk menjalankan Intent di dalam Compose
    val context = LocalContext.current
    var nameInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Pertemuan 4: Intent & Navigasi",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider()

        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Masukkan Nama Anda") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // --- EXPLICIT INTENT: pindah ke DetailActivity + kirim 3 tipe data ---
        Button(
            onClick = {
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra("EXTRA_NAME", nameInput)   // String
                    putExtra("EXTRA_SEMESTER", 5)       // Int
                    putExtra("EXTRA_IS_ACTIVE", true)   // Boolean
                }
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buka DetailScreen (Explicit Intent)")
        }

        // --- IMPLICIT INTENT 1: buka aplikasi telepon (dialer) ---
        OutlinedButton(
            onClick = {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:08123456789")
                }
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "Aplikasi telepon tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hubungi Dosen (Implicit Intent 1: Dial)")
        }

        // --- IMPLICIT INTENT 2: buka website di browser ---
        OutlinedButton(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.android.com"))
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "Browser tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buka Website (Implicit Intent 2: View)")
        }
    }
}

@Preview(showBackground = true, name = "Main Screen Preview")
@Composable
fun IntentDemoScreenPreview() {
    TrilogiTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            IntentDemoScreen()
        }
    }
}