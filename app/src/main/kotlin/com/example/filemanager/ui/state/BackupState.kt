package com.example.filemanager.ui.state

/**
 * تمثيل الحالات المتاحة لعملية النسخ الاحتياطي (Backup Process States).
 */
sealed class BackupState {
    
    /**
     * الحالة الابتدائية للتطبيق قبل بدء العملية (الانتظار).
     */
    object Idle : BackupState()

    /**
     * حالة جاري التنفيذ (تُستخدم لإظهار مؤشر التحميل CircularProgressIndicator).
     */
    object Loading : BackupState()

    /**
     * حالة النجاح، تحمل رسالة التأكيد لتمريرها وتنبيه المستخدم بها.
     */
    data class Success(val message: String) : BackupState()

    /**
     * حالة الفشل، تحمل نص الخطأ المسترجع من كلاس الـ Core لمعالجته وعرضه.
     */
    data class Error(val error: String) : BackupState()
}
