package com.strengthtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.strengthtracker.ui.AppRoot
import com.strengthtracker.ui.theme.StrengthTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StrengthTrackerTheme {
                AppRoot()
            }
        }
    }
}
