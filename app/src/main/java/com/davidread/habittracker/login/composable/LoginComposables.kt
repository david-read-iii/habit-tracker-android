package com.davidread.habittracker.login.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidread.habittracker.R
import com.davidread.habittracker.common.ui.theme.HabitTrackerTheme

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(WindowInsets.systemBars.asPaddingValues()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.undraw_login_weas),
            contentDescription = null,
            modifier = Modifier.size(192.dp)
        )
        Spacer(modifier = Modifier.height(64.dp))
        LoginCredentialsCard(
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(64.dp))
        SignUpText()
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun LoginCredentialsCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = TextFieldValue(),
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.email)) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = TextFieldValue(),
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.password)) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = {}) {
                    Text(
                        stringResource(R.string.login),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun SignUpText() {
    val annotatedText = buildAnnotatedString {
        append(stringResource(R.string.sign_up_text_prefix) + " ")

        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append(stringResource(R.string.sign_up_text_postfix))
        }
    }
    Text(annotatedText)
}

@Preview(showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    HabitTrackerTheme {
        LoginScreen()
    }
}
