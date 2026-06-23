package com.example.filemanager.core

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.regex.Pattern

class FileBackupManager {

    companion object {
        private const val TARGET_FILE_NAME = "com.nasa.zip"
        // النمط يطابق: com.nasa.zip أو com.nasa(1).zip أو com.nasa-2.zip إلخ بشكل غير حساس لحالة الأحرف
        private val FILE_PATTERN: Pattern = Pattern.compile("^com\\.nasa.*\\.zip$", Pattern.CASE_INSENSITIVE)
    }

    /**
     * ينفذ عملية البحث عن أحدث نسخة، تنظيف الاسم، والنسخ الاحتياطي مع الاستبدال.
     * @param downloadsDir مسار مجلد التنزيلات العام للمصدر.
     * @param targetParentDir مسار جذر وحدة التخزين لإنشاء مسار الوجهة.
     * @return Result<Unit> يمثل كبسولة النجاح أو الفشل مع الاستثناء.
     */
    suspend fun executeBackup(downloadsDir: File, targetParentDir: File): Result<Unit> {
        return runCatching {
            // التحقق من صلاحية مجلد المصدر
            if (!downloadsDir.exists() || !downloadsDir.isDirectory) {
                throw IOException("مجلد التنزيلات غير موجود أو تعذر الوصول إليه.")
            }

            // 1. تصفية الملفات بناءً على الـ Regex وجلب الملف الأحدث تعديلاً (Last Modified)
            val latestSourceFile = downloadsDir.listFiles()
                ?.filter { it.isFile && FILE_PATTERN.matcher(it.name).matches() }
                ?.maxByOrNull { it.lastModified() }
                ?: throw CompanionException("لم يتم العثور على ملف com.nasa.zip في مجلد التنزيلات.")

            // 2. إعداد مسار الوجهة المستهدف (WhatsApp/GBBackups)
            val destFolder = File(targetParentDir, "WhatsApp/GBBackups")
            if (!destFolder.exists()) {
                if (!destFolder.mkdirs()) {
                    throw IOException("فشل إنشاء شجرة مجلدات الوجهة WhatsApp/GBBackups")
                }
            }

            // تحديد الملف النهائي بالاسم القياسي الصحيح دون زيادات
            val targetFile = File(destFolder, TARGET_FILE_NAME)

            // 3. حذف النسخة السابقة من المجلد المستهدف إن وجدت لضمان الاستبدال النظيف
            if (targetFile.exists()) {
                if (!targetFile.delete()) {
                    throw IOException("فشل حذف النسخة القديمة المتواجدة في مجلد الوجهة.")
                }
            }

            // 4. دفق البيانات والنسخ (Stream Copy)
            copyFile(latestSourceFile, targetFile)
        }
    }

    /**
     * دالة منخفضة المستوى للتعامل مع الإدخال والإخراج عبر الـ Streams بحجم بافر مستقر.
     */
    private fun copyFile(source: File, destination: File) {
        FileInputStream(source).use { input ->
            FileOutputStream(destination).use { output ->
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                }
                output.flush()
            }
        }
    }

    /**
     * استثناء مخصص لتعريف أخطاء البحث داخل التطبيق.
     */
    class CompanionException(message: String) : IOException(message)
}
