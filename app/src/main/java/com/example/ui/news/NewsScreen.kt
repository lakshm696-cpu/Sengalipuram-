package com.example.ui.news

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class NewsItem(val title: String, val summary: String, val author: String, val date: String)

val sampleNews = listOf(
    NewsItem("Local Park Renovation Starts", "The city has begun renovations on the central park, expecting to finish by next spring.", "City Council", "2h ago"),
    NewsItem("New Library Branch Opening", "A new public library branch will open its doors next week in the downtown area.", "Library Board", "5h ago"),
    NewsItem("Road Closure on Main St.", "Main St. will be closed from 3rd Ave to 5th Ave this weekend for repaving.", "Dept. of Transportation", "1d ago"),
    NewsItem("Community Garden Volunteer Day", "Join your neighbors this Saturday to help plant spring flowers at the community garden.", "Garden Club", "2d ago")
)

@Composable
fun NewsScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Latest News",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleNews) { news ->
                NewsCard(news)
            }
        }
    }
}

@Composable
fun NewsCard(news: NewsItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = news.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = news.author,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = news.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
