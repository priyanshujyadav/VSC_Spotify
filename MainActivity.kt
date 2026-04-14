package com.example.spotify

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SpotifyApp() }
    }
}

@Composable
fun SpotifyApp() {

    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2000)
        showSplash = false
    }

    if (showSplash) {
        SplashScreen()
    } else {
        MainScreen()
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text("Spotify", color = Color.Green, style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
fun MainScreen() {

    val navController = rememberNavController()

    Scaffold(
        containerColor = Color.Black
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("player/{name}/{resId}") {
                val name = it.arguments?.getString("name") ?: ""
                val resId = it.arguments?.getString("resId")?.toInt() ?: 0
                PlayerScreen(name, resId)
            }
        }
    }
}

data class Song(val name: String, val resId: Int)

@Composable
fun HomeScreen(navController: NavHostController) {

    val hindiSongs = listOf(
        Song("Kesariya", R.raw.song1),
        Song("Chaiya Chaiya", R.raw.song2)
    )

    val popSongs = listOf(
        Song("Mic Drop", R.raw.song3),
        Song("Love Me Like You Do", R.raw.song4)
    )

    val tamilSongs = listOf(
        Song("Vachindamma", R.raw.song5),
        Song("Instrument", R.raw.song6)
    )

    LazyColumn(modifier = Modifier.padding(16.dp)) {

        item { Section("Hindi Songs", hindiSongs, navController) }
        item { Section("Pop Songs", popSongs, navController) }
        item { Section("Tamil Songs", tamilSongs, navController) }
    }
}

@Composable
fun Section(title: String, songs: List<Song>, navController: NavHostController) {

    Column {
        Text(title, color = Color.Green)

        songs.forEach { song ->
            Text(
                text = song.name,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clickable {

                        // ✅ PUT IT HERE
                        navController.navigate("player/${song.name}/${song.resId}") {
                            launchSingleTop = true
                        }

                    }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun PlayerScreen(songName: String, songResId: Int) {

    val context = androidx.compose.ui.platform.LocalContext.current

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    var currentPosition by remember { mutableStateOf(0f) }
    var duration by remember { mutableStateOf(1f) }

    val handler = Handler(Looper.getMainLooper())

    LaunchedEffect(songResId) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, songResId)
        mediaPlayer?.setVolume(1f, 1f)

        duration = mediaPlayer?.duration?.toFloat() ?: 1f

        handler.post(object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    currentPosition = it.currentPosition.toFloat()
                }
                handler.postDelayed(this, 500)
            }
        })
    }

    DisposableEffect(Unit) {
        onDispose { mediaPlayer?.release() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 🎵 Song Name in Center
        Text(songName, color = Color.White)

        Spacer(modifier = Modifier.height(30.dp))

        // 🎚️ Seekbar
        Slider(
            value = currentPosition,
            onValueChange = {
                currentPosition = it
                mediaPlayer?.seekTo(it.toInt())
            },
            valueRange = 0f..duration
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ▶️ ⏸️ Button (Icon only)
        IconButton(
            onClick = {
                mediaPlayer?.let {
                    if (isPlaying) it.pause() else it.start()
                    isPlaying = !isPlaying
                }
            }
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(60.dp)
            )
        }
    }
}