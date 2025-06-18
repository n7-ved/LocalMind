package com.n7.localmind.design.system.composables

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.n7.localmind.design.system.theme.Progress

@Preview(showSystemUi = true)
@Composable
fun LabelButton(text: String = "Upload", onClick: () -> Unit = {}) {

    Button(
        onClick = { onClick() },
        content = { Text(text = text) }
    )
}

@Composable
fun LoadingButton(text: String, isLoading: Boolean, onClick: () -> Unit) {

    Button(
        enabled = !isLoading,
        content = {
            if (isLoading) {
                CircularProgressIndicator(color = Progress)
            } else {
                MediumLabel(Modifier, text = text)
            }
        },
        onClick = onClick
    )
}

