package com.n7.localmind.design.system.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BottomCenteredButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .padding(bottom = 80.dp) // push it up a bit from the bottom
        ) {
            Text(text)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewBottomCenteredButton() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter

    ) {
        Button(
            onClick = {  },
            modifier = Modifier
                .padding(bottom = 80.dp) // push it up a bit from the bottom
        ) {
            Text("Upload Document")
        }
    }
}