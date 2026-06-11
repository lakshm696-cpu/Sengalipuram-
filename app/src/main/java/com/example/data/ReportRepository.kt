package com.example.data

import kotlinx.coroutines.flow.Flow

class ReportRepository(
    private val reportDao: ReportDao,
    private val progressUpdateDao: ProgressUpdateDao
) {
    val allReports: Flow<List<Report>> = reportDao.getAllReports()

    suspend fun insert(report: Report) = reportDao.insertReport(report)

    suspend fun deleteById(id: Int) = reportDao.deleteReportById(id)

    fun getUpdatesForReport(reportId: Int): Flow<List<ProgressUpdate>> =
        progressUpdateDao.getUpdatesForReport(reportId)

    suspend fun insertUpdate(update: ProgressUpdate) =
        progressUpdateDao.insertUpdate(update)

    suspend fun deleteUpdateById(id: Int) =
        progressUpdateDao.deleteUpdateById(id)
}
