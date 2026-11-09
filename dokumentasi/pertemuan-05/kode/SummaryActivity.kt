package com.example.trilogi

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

class SummaryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = intent.getStringExtra("EXTRA_NAME")?.ifBlank { "Guest" } ?: "Guest"
        val semester = intent.getIntExtra("EXTRA_SEMESTER", 0)
        val isActive = intent.getBooleanExtra("EXTRA_IS_ACTIVE", false)

        setContent {
            TrilogiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SummaryScreen(
                        name = name,
                        semester = semester,
                        isActive = isActive,
                        onBackClicked = { finish() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryScreen(
    name: String,
    semester: Int,
    isActive: Boolean,
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
            text = "Ringkasan (Layar 3)",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Data diterima dari DetailActivity lewat Explicit Intent.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Nama (String): $name", fontSize = 16.sp)
                Text("Semester (Int): $semester", fontSize = 16.sp)
                Text("Status Aktif (Boolean): $isActive", fontSize = 16.sp)
            }
        }

        Button(
            onClick = onBackClicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kembali (finish)")
        }
    }
}

@Preview(showBackground = true, name = "Summary Screen Preview")
@Composable
fun SummaryScreenPreview() {
    TrilogiTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SummaryScreen(name = "Amelia Putri Rahayu", semester = 5, isActive = true, onBackClicked = {})
        }
    }
}
