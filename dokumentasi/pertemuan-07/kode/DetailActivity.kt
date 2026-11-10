package com.example.trilogi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trilogi.ui.theme.TrilogiTheme

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ambil data berdasarkan Key yang dikirim; key harus SAMA dengan pengirim.
        // Elvis (?:) = nilai default bila data null
        val passedName = intent.getStringExtra("EXTRA_NAME")?.ifBlank { "Guest" } ?: "Guest"
        val passedSemester = intent.getIntExtra("EXTRA_SEMESTER", 0)
        val isActive = intent.getBooleanExtra("EXTRA_IS_ACTIVE", false)

        setContent {
            TrilogiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DetailScreen(
                        name = passedName,
                        semester = passedSemester,
                        isActive = isActive,
                        onNextClicked = {
                            // Explicit Intent ke layar ke-3 + teruskan 3 tipe data
                            val next = Intent(this@DetailActivity, SummaryActivity::class.java).apply {
                                putExtra("EXTRA_NAME", passedName)
                                putExtra("EXTRA_SEMESTER", passedSemester)
                                putExtra("EXTRA_IS_ACTIVE", isActive)
                            }
                            startActivity(next)
                        },
                        onBackClicked = {
                            finish() // hapus activity ini dari back stack
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailScreen(
    name: String,
    semester: Int,
    isActive: Boolean,
    onNextClicked: () -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Halaman Detail Mahasiswa",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Nama: $name", fontSize = 16.sp)
                Text("Semester: $semester", fontSize = 16.sp)
                Text("Status Aktif: ${if (isActive) "Ya" else "Tidak"}", fontSize = 16.sp)
            }
        }

        Button(
            onClick = onNextClicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lihat Ringkasan (ke Layar 3)")
        }

        Button(
            onClick = onBackClicked,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Kembali & Hancurkan Activity (finish)")
        }
    }
}

@Preview(showBackground = true, name = "Detail Screen Preview")
@Composable
fun DetailScreenPreview() {
    TrilogiTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DetailScreen(
                name = "Amelia Putri Rahayu",
                semester = 5,
                isActive = true,
                onNextClicked = {},
                onBackClicked = {}
            )
        }
    }
}