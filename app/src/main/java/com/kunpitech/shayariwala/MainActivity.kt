package com.kunpitech.shayariwala

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import com.kunpitech.shayariwala.data.repository.LikeRepository
import com.kunpitech.shayariwala.ui.navigation.ShayariNavGraph
import com.kunpitech.shayariwala.ui.theme.ShayariWalaTheme

class MainActivity : ComponentActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        signInAnonymouslyIfNeeded()

        setContent {
            ShayariWalaTheme(darkTheme = true) {
                val systemUiController = rememberSystemUiController()
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color     = Color.Transparent,
                        darkIcons = false,
                    )
                }
                ShayariNavGraph()
            }
        }
    }

    private fun signInAnonymouslyIfNeeded() {
        if (auth.currentUser == null) {
            auth.signInAnonymously()
                .addOnSuccessListener {
                    // Init like state after auth is ready
                    LikeRepository.init()
                }
                .addOnFailureListener {
                    LikeRepository.init()
                }
        } else {
            // Already signed in — init immediately
            LikeRepository.init()
        }
    }
}