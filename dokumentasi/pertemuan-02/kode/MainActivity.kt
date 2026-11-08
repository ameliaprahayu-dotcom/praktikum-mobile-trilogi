package com.example.trilogi

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trilogi.ui.theme.TrilogiTheme

class MainActivity : ComponentActivity() {

    private val TAG = "LIFECYCLE_TAG"

    private var currentStatus by mutableStateOf("onCreate: Aplikasi Dimulai")
    private var resumeCount by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d(TAG, "onCreate: Activity dibuat dan resource diinisialisasi")
        currentStatus = "Status: onCreate (Inisialisasi Selesai)"
        setContent {
            TrilogiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LifecycleScreen(
                        status = currentStatus,
                        counter = resumeCount,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: Layar mulai terlihat oleh pengguna")
        currentStatus = "Status: onStart (Aplikasi Terlihat)"
    }

    override fun onResume() {
        super.onResume()
        resumeCount++
        Log.d(TAG, "onResume: Aplikasi aktif (Fase ke-$resumeCount)")
        currentStatus = "Status: onResume (Aplikasi Siap Digunakan)"
        Toast.makeText(this, "onResume ke-$resumeCount", Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: Aplikasi kehilangan fokus")
        currentStatus = "Status: onPause (Kehilangan Fokus)"
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: Aplikasi tidak terlihat (background)")
        currentStatus = "Status: onStop (Di Background)"
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart: Aplikasi dibuka kembali setelah onStop")
        currentStatus = "Status: onRestart (Dibuka Kembali)"
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Activity dihancurkan dan memori dibersihkan")
    }
}

@Composable
fun LifecycleScreen(status: String, counter: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Demo Activity Lifecycle",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = status)
                Text(text = "Jumlah onResume: $counter")
            }
        }
        Text(
            text = "Cara uji: tekan Home, buka lagi dari Recent Apps, putar layar, " +
                    "lalu amati Logcat dengan filter LIFECYCLE_TAG."
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LifecyclePreview() {
    TrilogiTheme {
        LifecycleScreen(status = "Status: onCreate (Inisialisasi Selesai)", counter = 1)
    }
}