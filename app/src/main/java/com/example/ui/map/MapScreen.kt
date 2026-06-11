package com.example.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.data.Report
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(reports: List<Report>) {
    val defaultLocation = LatLng(20.5937, 78.9629) // Center of India, arbitrary default
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 4f)
    }

    LaunchedEffect(reports) {
        val firstReportWithLocation = reports.firstOrNull { it.latitude != null && it.longitude != null }
        if (firstReportWithLocation != null) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(firstReportWithLocation.latitude!!, firstReportWithLocation.longitude!!),
                10f
            )
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        reports.forEach { report ->
            if (report.latitude != null && report.longitude != null) {
                Marker(
                    state = MarkerState(position = LatLng(report.latitude, report.longitude)),
                    title = report.title,
                    snippet = report.description
                )
            }
        }
    }
}
