package com.example.filemanager.ui.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.filemanager.ui.state.BackupState
import com.example.filemanager.ui.viewmodel.BackupViewModel

@Composable
fun BackupScreen(viewModel: BackupViewModel = viewModel()) {
    val context = LocalContext.current
    // مراقبة دفق الحالة القادم من الـ ViewModel تحضيراً لتحديث الواجهة
    val state by viewModel.backupState.collectAsState()

    // الاستماع للحالات الجانبية (Side Effects) لإظهار التنبيهات المنبثقة فوراً
    LaunchedEffect(state) {
        when (state) {
            is BackupState.Success -> {
                Toast.makeText(context, (state as BackupState.Success).message, Toast.LENGTH_LONG).show()
            }
            is BackupState.Error -> {
                Toast.makeText(context, (state as BackupState.Error).error, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "مدير النسخ الاحتياطي لـ NASA",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // معالجة الرسم (Rendering) بناءً على الحالة الحالية
            when (state) {
                is BackupState.Loading -> {
                    CircularProgressIndicator(
                        strokeWidth = 4.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                else -> {
                    Button(
                        onClick = { viewModel.startBackupProcess() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = "ابدأ الفحص والنسخ الآن",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
