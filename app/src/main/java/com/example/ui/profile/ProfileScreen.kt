package com.example.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import com.example.data.Report
import com.example.data.User
import com.example.ui.ReportCard

@Composable
fun ProfileScreen(
    user: User?,
    reports: List<Report>,
    onLogout: () -> Unit,
    onDeleteReport: (Int) -> Unit
) {
    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Log in to view your profile")
        }
        return
    }

    val userReports = reports.filter { it.userId == user.id }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        // Top Bar Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Picture
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Icon",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Metrics
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${userReports.size}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    Text("Posts", style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("132", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    Text("Followers", style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("34", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    Text("Following", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Bio section
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = user.username.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() },
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Community Member | Sharing updates",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { /* TODO Edit profile */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface),
                contentPadding = PaddingValues(0.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            ) {
                Text("Edit Profile")
            }
            var isInstagramLinked by remember { mutableStateOf(false) }
            Button(
                onClick = { isInstagramLinked = !isInstagramLinked },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface),
                contentPadding = PaddingValues(0.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            ) {
                Text(if (isInstagramLinked) "Unlink Instagram" else "Link Instagram")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

        // Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = androidx.compose.material.icons.Icons.Default.Menu, contentDescription = "Posts")
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = androidx.compose.material.icons.Icons.Default.Person, contentDescription = "Tags")
            }
        }
        
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

        if (userReports.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "You haven't posted any reports yet.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(userReports, key = { it.id }) { report ->
                    ReportCard(
                        report = report,
                        currentUserId = user.id,
                        isFollowed = false, // Not applicable for own reports really
                        onToggleFollow = { },
                        onDelete = { onDeleteReport(report.id) }
                    )
                }
            }
        }
    }
}
