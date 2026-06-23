package com.example.filemanager

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.filemanager.ui.theme.FileManagerTheme
import com.example.filemanager.ui.view.BackupScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // التحقق من الصلاحيات فور تشغيل التطبيق
        checkAndRequestStoragePermissions()

        setContent {
            FileManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BackupScreen()
                }
            }
        }
    }

    /**
     * التحقق من صلاحيات التخزين وطلبها برمجياً حسب إصدار الأندرويد لضمان الوصول للمجلدات العامة.
     */
    private fun checkAndRequestStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // لأنظمة Android 11 فما فوق، نتحقق من صلاحية إدارة الملفات بالكامل
            if (!Environment.isExternalStorageManager()) {
                runCatching {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                        data = Uri.parse("package:$packageName")
                    }
                    startActivity(intent)
                }.onFailure {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    startActivity(intent)
                }
                Toast.makeText(this, "الرجاء تفعيل صلاحية الوصول للملفات لضمان عمل التطبيق.", Toast.LENGTH_LONG).show()
            }
        } else {
            // للأنظمة الأقدم من Android 11 (المستوى 29 فما دون)
            val permissions = arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            requestPermissions(permissions, 101)
        }
    }
}
