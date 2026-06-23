package com.example.filemanager.ui.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.core.FileBackupManager
import com.example.filemanager.ui.state.BackupState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class BackupViewModel : ViewModel() {

    private val backupManager = FileBackupManager()

    // تصحيح الاستدعاء وإدارة الحالة داخلياً وخارجياً
    private val _backupState = MutableStateFlow<BackupState>(BackupState.Idle)
    val backupState: StateFlow<BackupState> = _backupState.asStateFlow()

    fun startBackupProcess() {
        viewModelScope.launch(Dispatchers.IO) {
            // استخدام .value لتحديث الحالة برمجياً بشكل صحيح
            _backupState.value = BackupState.Loading
            
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val rootStorage = Environment.getExternalStorageDirectory()

            val result = backupManager.executeBackup(downloadsDir, rootStorage)
            
            result.onSuccess {
                _backupState.value = BackupState.Success("تم النسخ والاستبدال بنجاح إلى WhatsApp/GBBackups")
            }.onFailure { exception ->
                _backupState.value = BackupState.Error(exception.localizedMessage ?: "حدث خطأ غير متوقع أثناء العملية.")
            }
        }
    }
}
