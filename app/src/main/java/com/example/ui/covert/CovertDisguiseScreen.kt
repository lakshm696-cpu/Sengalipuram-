package com.example.ui.covert

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CovertDisguiseScreen(
    prefs: CovertPrefs,
    onUnlock: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (prefs.disguiseType) {
            "calculator" -> CalculatorDisguise(code = prefs.covertCode, onUnlock = onUnlock)
            "weather" -> WeatherDisguise(onUnlock = onUnlock)
            "notes" -> NotesDisguise(onUnlock = onUnlock)
            else -> CalculatorDisguise(code = prefs.covertCode, onUnlock = onUnlock)
        }
    }
}

@Composable
fun CalculatorDisguise(
    code: String,
    onUnlock: () -> Unit
) {
    var display by remember { mutableStateOf("0") }
    var expression by remember { mutableStateOf("") }
    var isOperatorClicked by remember { mutableStateOf(false) }
    var lastValue by remember { mutableStateOf(0.0) }
    var activeOperator by remember { mutableStateOf("") }

    // Simple calculation logic
    fun handleNumber(num: String) {
        if (display == "0" || isOperatorClicked) {
            display = num
            isOperatorClicked = false
        } else {
            display += num
        }
        expression += num
    }

    fun handleOperator(op: String) {
        lastValue = display.toDoubleOrNull() ?: 0.0
        activeOperator = op
        isOperatorClicked = true
        expression += " $op "
    }

    fun evaluate() {
        if (expression == code || display == code) {
            onUnlock()
            return
        }
        val currentValue = display.toDoubleOrNull() ?: 0.0
        val result = when (activeOperator) {
            "+" -> lastValue + currentValue
            "-" -> lastValue - currentValue
            "*" -> lastValue * currentValue
            "/" -> if (currentValue != 0.0) lastValue / currentValue else 0.0
            else -> currentValue
        }
        val resultStr = if (result % 1.0 == 0.0) result.toInt().toString() else result.toString()
        display = resultStr
        expression = resultStr
        activeOperator = ""
    }

    fun clear() {
        display = "0"
        expression = ""
        lastValue = 0.0
        activeOperator = ""
        isOperatorClicked = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E)) // Sized to match a standard dark calculator theme
            .padding(24.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Display Area
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = expression.ifEmpty { " " },
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = display,
            color = Color.White,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Grid layout for calculator keys
        val buttons = listOf(
            listOf("C", "±", "%", "/"),
            listOf("7", "8", "9", "*"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "=", "")
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { char ->
                    if (char.isNotEmpty()) {
                        val isOp = char in listOf("/", "*", "-", "+", "=")
                        val isSpecial = char in listOf("C", "±", "%")
                        val containerColor = when {
                            isOp -> MaterialTheme.colorScheme.primary
                            isSpecial -> Color.Gray.copy(alpha = 0.5f)
                            else -> Color(0xFF333333)
                        }
                        val contentColor = when {
                            isOp || isSpecial -> Color.White
                            else -> Color.White
                        }

                        Button(
                            onClick = {
                                when {
                                    char == "C" -> clear()
                                    char in listOf("+", "-", "*", "/") -> handleOperator(char)
                                    char == "=" -> evaluate()
                                    char == "%" -> {
                                        val valD = display.toDoubleOrNull() ?: 0.0
                                        display = (valD / 100).toString()
                                        expression = display
                                    }
                                    char == "±" -> {
                                        if (display.startsWith("-")) {
                                            display = display.substring(1)
                                        } else if (display != "0") {
                                            display = "-$display"
                                        }
                                        expression = display
                                    }
                                    else -> handleNumber(char)
                                }
                            },
                            modifier = Modifier
                                .weight(if (char == "0") 2.1f else 1f)
                                .height(72.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = containerColor,
                                contentColor = contentColor
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = char,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherDisguise(
    onUnlock: () -> Unit
) {
    var tapCount by remember { mutableStateOf(0) }
    var locationName by remember { mutableStateOf("Greenfield Village") }

    LaunchedEffect(tapCount) {
        if (tapCount >= 5) {
            onUnlock()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF29B6F6), Color(0xFF0288D1))))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Search bar lookalike
        OutlinedTextField(
            value = locationName,
            onValueChange = { locationName = it },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White) },
            placeholder = { Text("Search city...", color = Color.White.copy(alpha = 0.6f)) },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Color.White.copy(alpha = 0.2f)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Main Weather Display (The sun icon acts as secret hit box)
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .clickable { tapCount++ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = "Weather Icon",
                tint = Color(0xFFFFD54F),
                modifier = Modifier.size(120.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = locationName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Text(
            text = "Sunny & Clear",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "24°C",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Light,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Additional stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.15f))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherStatItem(Icons.Default.WaterDrop, "Humidity", "62%")
            WeatherStatItem(Icons.Default.Air, "Wind", "14 km/h")
            WeatherStatItem(Icons.Default.DeviceThermostat, "UV Index", "Low")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Proportional design - Hourly forecast
        Text(
            text = "HOURLY FORECAST",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.align(Alignment.Start).padding(start = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HourlyForecastItem("09:00", Icons.Default.WbSunny, "22°")
            HourlyForecastItem("12:00", Icons.Default.WbSunny, "24°")
            HourlyForecastItem("15:00", Icons.Default.WbCloudy, "23°")
            HourlyForecastItem("18:00", Icons.Default.WbCloudy, "21°")
            HourlyForecastItem("21:00", Icons.Default.NightsStay, "18°")
        }
    }
}

@Composable
fun WeatherStatItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun HourlyForecastItem(time: String, icon: androidx.compose.ui.graphics.vector.ImageVector, temp: String) {
    Column(
        modifier = Modifier
            .width(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(time, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
        Spacer(modifier = Modifier.height(4.dp))
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(temp, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun NotesDisguise(
    onUnlock: () -> Unit
) {
    var tapCount by remember { mutableStateOf(0) }
    var noteTitle by remember { mutableStateOf("") }
    var noteBody by remember { mutableStateOf("") }
    
    val savedNotes = remember {
        mutableStateListOf(
            "Shopping list (eggs, milk, bread)",
            "Call mechanic this afternoon",
            "Mow the front yard on Saturday",
            "Remember to water regional plants"
        )
    }

    LaunchedEffect(tapCount) {
        if (tapCount >= 5) {
            onUnlock()
        }
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { tapCount++ }
                    ) {
                        Icon(
                            Icons.Default.EditNote,
                            contentDescription = "Notes",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Personal Notebook", fontWeight = FontWeight.SemiBold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // New Note Creator
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Quick Memo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteBody,
                        onValueChange = { noteBody = it },
                        placeholder = { Text("Write something...") },
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (noteBody.isNotBlank()) {
                                savedNotes.add(0, noteBody)
                                noteBody = ""
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Save note")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("SAVED MEMOS", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(savedNotes) { note ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.StickyNote2, contentDescription = null, tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = note,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { savedNotes.remove(note) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete note", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
            }
        }
    }
}
