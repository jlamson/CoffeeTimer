package com.darkmoose117.coffee.android.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.darkmoose117.coffee.android.MyApplicationTheme

@Composable
fun ThemedPreview(content: @Composable () -> Unit) = MyApplicationTheme {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        content()
    }
}