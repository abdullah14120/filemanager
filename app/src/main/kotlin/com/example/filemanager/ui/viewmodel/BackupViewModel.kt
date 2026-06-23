package com.example.filemanager.ui.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.core.FileBackupManager
import com.example.filemanager.ui.state.BackupState
import kotlinx.flow.MutableStateFlow
import kotlinx.flow.StateFlow
import kotlinx.flow.asStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class BackupViewModel : ViewModel() {

    // استدعاء طبقة المنطق (Core Logic)
    private val backupManager = FileBackupManager()

    // إدارة الحالة الداخلية والخارجية للتطبيق
    private val _backupState = MutableStateFlow<BackupState>(BackupState.Idle)
    val backupState: StateFlow<BackupState> = _backupState.asStateFlow()

    /**
     * بدء عملية البحث والنسخ الاحتياطي في الخلفية.
     */
    fun startBackupProcess() {
        viewModelScope.launch(Dispatchers.IO) {
            // تحويل الحالة إلى جاري التحميل لتحديث الواجهة
            _backupState.value = BackupState.Loading
            
            // تحديد مسارات التخزين العامة
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val rootStorage = Environment.getExternalStorageDirectory()

            // تنفيذ العملية من كلاس الـ Core
            val result = backupManager.executeBackup(downloadsDir, rootStorage)
            
            // معالجة النتيجة وتحديث الحالة بناءً على النجاح أو الفشل
            result.onSuccess {
                _backupState.value = BackupState.Success("تم النسخ والاستبدال بنجاح إلى WhatsApp/GBBackups")
            }.onFailure { exception ->
                _backupState.value = BackupState.Error(exception.localizedMessage ?: "حدث خطأ غير متوقع أثناء العملية.")
            }
        }
    }
}
