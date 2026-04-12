package com.example.spotify
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SpotifyApp()
        }
    }
}

@Composable
fun SpotifyApp() {

    val navController = rememberNavController()

    Scaffold(
        containerColor = Color.Black,
        bottomBar = { BottomBar(navController) }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("search") { SearchScreen() }
            composable("library") { LibraryScreen() }
            composable("player/{song}") {
                val song = it.arguments?.getString("song") ?: ""
                PlayerScreen(song)
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {

    NavigationBar(containerColor = Color.Black) {

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("home") },
            icon = { Text("🏠") },
            label = { Text("Home", color = Color.White) }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("search") },
            icon = { Text("🔍") },
            label = { Text("Search", color = Color.White) }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("library") },
            icon = { Text("🎵") },
            label = { Text("Library", color = Color.White) }
        )
    }
}

@Composable
fun HomeScreen(navController: NavHostController) {

    val songs = listOf(
        "Shape of You",
        "Believer",
        "Perfect",
        "Faded",
        "Closer"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Spotify Clone", color = Color.White)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(songs) { song ->

                Text(
                    text = song,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clickable {
                            navController.navigate("player/$song")
                        }
                )

                Divider(color = Color.Gray)
            }
        }
    }
}

@Composable
fun SearchScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Search Screen", color = Color.White)
    }
}

@Composable
fun LibraryScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Your Library", color = Color.White)
    }
}

@Composable
fun PlayerScreen(song: String) {

    var isPlaying by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Now Playing", color = Color.Gray)

        Spacer(modifier = Modifier.height(10.dp))

        Text(song, color = Color.White)

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                isPlaying = !isPlaying
            }
        ) {
            Text(if (isPlaying) "Pause ⏸️" else "Play ▶️")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isPlaying) "Playing..." else "Stopped",
            color = Color.Green
        )
    }
}