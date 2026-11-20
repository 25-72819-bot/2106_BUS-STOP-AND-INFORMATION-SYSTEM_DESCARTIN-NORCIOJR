package com.example.myfirstapp.passenger

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfirstapp.TransitGreen
import com.example.myfirstapp.user
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user" or "bot"
    val text: String,
    val timestamp: String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
)

private const val GEMINI_API_KEY = ""

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommuterChatbot(
    user: user? = null,
    onBack: () -> Unit = {}
) {
    BackHandler(enabled = true) {
        onBack()
    }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val userName = user?.fullname?.ifBlank { "Commuter" } ?: "Commuter"

    var inputMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                sender = "bot",
                text = "Hello $userName! 👋 Welcome to TransitGo AI Support.\n\nI can assist you with real-time bus tracking, schedules, seat bookings, GCash/PayMaya payment, driver ratings, and lost items.\n\nHow can I help you today?"
            )
        )
    }

    val suggestedQuestions = listOf(
        "🚌 Track my bus live",
        "💳 GCash payment guide",
        "⏰ Bus schedules & routes",
        "🎒 Report a lost item",
        "⭐️ How to rate a driver"
    )

    fun sendMessage(userText: String) {
        if (userText.isBlank() || isLoading) return

        val trimmedText = userText.trim()
        inputMessage = ""
        messages.add(ChatMessage(sender = "user", text = trimmedText))

        isLoading = true

        coroutineScope.launch {
            listState.animateScrollToItem(messages.size - 1)

            val botResponseText = fetchGeminiResponse(trimmedText, messages)

            isLoading = false
            messages.add(ChatMessage(sender = "bot", text = botResponseText))
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = TransitGreen,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.2f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.SmartToy,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "TransitGo AI Support",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(Color(0xFF4CAF50), CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Online 24/7",
                                            color = Color.White.copy(alpha = 0.85f),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                        windowInsets = WindowInsets(0, 0, 0, 0)
                    )
                }
            }
        },
        containerColor = Color(0xFFF4F6F8)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Chat Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    ChatMessageBubble(msg = msg)
                }

                if (isLoading) {
                    item {
                        BotTypingIndicator()
                    }
                }
            }

            // Quick Prompt Suggestions Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestedQuestions) { question ->
                    SuggestionChip(
                        onClick = { sendMessage(question) },
                        label = { Text(question, fontSize = 12.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = Color.White,
                            labelColor = TransitGreen
                        ),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            enabled = true,
                            borderColor = TransitGreen.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))

            // Message Input Row
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputMessage,
                        onValueChange = { inputMessage = it },
                        placeholder = { Text("Ask TransitGo AI...", fontSize = 14.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = false,
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TransitGreen,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    IconButton(
                        onClick = { sendMessage(inputMessage) },
                        enabled = inputMessage.isNotBlank() && !isLoading,
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                color = if (inputMessage.isNotBlank() && !isLoading) TransitGreen else Color.LightGray,
                                shape = CircleShape
                            )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private suspend fun fetchGeminiResponse(userQuery: String, history: List<ChatMessage>): String {
    return withContext(Dispatchers.IO) {
        if (GEMINI_API_KEY.isBlank()) {
            kotlinx.coroutines.delay(400) // Brief natural thinking delay for UI feedback
            return@withContext getLocalFallbackResponse(userQuery)
        }
        val modelsToTry = listOf(
            "gemini-2.0-flash",
            "gemini-1.5-flash",
            "gemini-1.5-pro"
        )

        val systemPrompt = "You are TransitGo AI Assistant, an expert virtual customer support agent for TransitGo (a smart bus transit app in the Philippines). " +
                "Help commuters with live bus tracking, ticket booking, GCash/PayMaya payments, bus schedules, driver ratings, and lost items. " +
                "Keep answers friendly, clear, concise, and helpful. Use bullet points or emojis when helpful. " +
                "TransitGo Support Hotline: (043) 980-0385, Email: support@transitgo.ph."

        for (modelName in modelsToTry) {
            try {
                val urlString = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$GEMINI_API_KEY"
                val url = URL(urlString)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                conn.connectTimeout = 10000
                conn.readTimeout = 10000
                conn.doOutput = true

                val requestJson = JSONObject()
                val contentsArray = JSONArray()

                val historySlice = history.takeLast(6)
                val conversationText = StringBuilder()
                conversationText.append(systemPrompt).append("\n\n")
                for (msg in historySlice) {
                    if (msg.sender == "user") {
                        conversationText.append("Commuter: ").append(msg.text).append("\n")
                    } else if (msg.sender == "bot") {
                        conversationText.append("Assistant: ").append(msg.text).append("\n")
                    }
                }
                conversationText.append("Commuter: ").append(userQuery).append("\n")
                conversationText.append("Assistant:")

                val userContentObj = JSONObject()
                userContentObj.put("role", "user")
                val partsArray = JSONArray()
                val textObj = JSONObject()
                textObj.put("text", conversationText.toString())
                partsArray.put(textObj)
                userContentObj.put("parts", partsArray)
                contentsArray.put(userContentObj)

                requestJson.put("contents", contentsArray)

                val bodyBytes = requestJson.toString().toByteArray(Charsets.UTF_8)
                conn.outputStream.use { os ->
                    os.write(bodyBytes)
                }

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val responseStr = conn.inputStream.bufferedReader().use { it.readText() }
                    val jsonResp = JSONObject(responseStr)
                    val candidates = jsonResp.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val content = candidates.getJSONObject(0).optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val replyText = parts.getJSONObject(0).optString("text")
                            if (replyText.isNotBlank()) {
                                return@withContext replyText.trim()
                            }
                        }
                    }
                }
            } catch (_: Exception) {
                // Try next model or fallback
            }
        }

        // Fallback local rule-based response generator
        return@withContext getLocalFallbackResponse(userQuery)
    }
}

private fun getLocalFallbackResponse(query: String): String {
    val q = query.lowercase()
    return when {
        q.contains("track") || q.contains("location") || q.contains("gps") || q.contains("where") -> {
            "📍 **Live Bus Tracking Instructions**\n\n" +
            "1. Go to the **Tracking** tab on the bottom menu or select your active booking.\n" +
            "2. View real-time GPS locations of buses on the interactive map.\n" +
            "3. You can also view estimated time of arrival (ETA) for each bus stop!"
        }
        q.contains("pay") || q.contains("gcash") || q.contains("paymaya") || q.contains("book") || q.contains("ticket") -> {
            "💳 **Booking & Payment Steps**\n\n" +
            "1. Tap **Search Routes** on the Home tab.\n" +
            "2. Select your origin and destination stops.\n" +
            "3. Choose your preferred departure time and select your seats.\n" +
            "4. Pay securely using GCash or PayMaya by scanning the QR code!"
        }
        q.contains("lost") || q.contains("item") || q.contains("missing") || q.contains("found") -> {
            "🎒 **Lost & Found Support**\n\n" +
            "If you left something on a TransitGo bus:\n" +
            "• **Hotline:** (043) 980-0385\n" +
            "• **Email:** support@transitgo.ph\n\n" +
            "Please provide your **Bus Number** and **Trip Date/Time** when contacting support!"
        }
        q.contains("rate") || q.contains("driver") || q.contains("star") || q.contains("review") -> {
            "⭐️ **Rating Drivers & Trips**\n\n" +
            "1. Open **Profile & Settings**.\n" +
            "2. Tap **Trip & Driver Rating**.\n" +
            "3. Choose your completed trip and rate your driver from 1 to 5 stars!"
        }
        q.contains("schedule") || q.contains("time") || q.contains("route") -> {
            "⏰ **Bus Schedules & Routes**\n\n" +
            "TransitGo buses operate daily from **6:00 AM to 10:00 PM**.\n" +
            "You can view complete routes and timetables directly from the **Home** tab under available trips."
        }
        q.contains("hello") || q.contains("hi") || q.contains("hey") -> {
            "Hello! 😊 How can I help you with TransitGo today? Feel free to ask about bus schedules, ticket booking, live tracking, or lost items."
        }
        else -> {
            "Thank you for contacting TransitGo Support! 🚌\n\n" +
            "I can assist you with:\n" +
            "• Live Bus GPS Tracking\n" +
            "• GCash / PayMaya Payment & Bookings\n" +
            "• Driver & Trip Ratings\n" +
            "• Lost & Found Inquiries\n\n" +
            "For urgent assistance, call our Hotline: **(043) 980-0385** or email **support@transitgo.ph**."
        }
    }
}

@Composable
fun ChatMessageBubble(msg: ChatMessage) {
    val isUser = msg.sender == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Surface(
                shape = CircleShape,
                color = TransitGreen.copy(alpha = 0.15f),
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.Top)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = TransitGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                color = if (isUser) TransitGreen else Color.White,
                shadowElevation = 1.dp
            ) {
                Text(
                    text = msg.text,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    color = if (isUser) Color.White else Color.Black,
                    fontSize = 14.sp
                )
            }
            Text(
                text = msg.timestamp,
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
            )
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = CircleShape,
                color = TransitGreen,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.Top)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BotTypingIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 40.dp, top = 4.dp, bottom = 4.dp)
    ) {
        CircularProgressIndicator(
            color = TransitGreen,
            modifier = Modifier.size(14.dp),
            strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "TransitGo AI is thinking...",
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}
