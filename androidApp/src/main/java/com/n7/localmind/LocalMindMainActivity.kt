package com.n7.localmind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.n7.localmind.design.system.theme.LocalMindTheme
import com.n7.localmind.navigation.AppNavigationComposable

class LocalMindMainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocalMindTheme(
                content = { AppNavigationComposable() }
            )
        }
    }
}
