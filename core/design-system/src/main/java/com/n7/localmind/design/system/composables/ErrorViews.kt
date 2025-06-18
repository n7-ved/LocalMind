package com.n7.localmind.design.system.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.n7.localmind.design.system.R
import com.n7.localmind.design.system.theme.defaultMargin

@Composable
inline fun RetryErrorView(
    errorMessage: String = stringResource(id = R.string.generic_error_message),
    crossinline onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = {
            MediumLabel(modifier = Modifier.padding(defaultMargin), text = errorMessage)
            LabelButton(text = stringResource(R.string.retry)) { onRetry() }
        }
    )
}

@Preview(showSystemUi = true)
@Composable
fun ErrorView(
    errorMessage: String = stringResource(id = R.string.generic_error_message)
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = {
            MediumLabel(modifier = Modifier.padding(defaultMargin), text = errorMessage)
        }
    )
}
