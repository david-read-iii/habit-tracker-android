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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidread.habittracker.R
import com.davidread.habittracker.common.ui.theme.HabitTrackerTheme
import com.davidread.habittracker.login.model.LoginViewState

private const val SIGN_UP_LINK_ANNOTATION_TAG = "sign_up"

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewState: LoginViewState = LoginViewState(),
    onEmailValueChange: (String) -> Unit = {},
    onPasswordValueChange: (String) -> Unit = {},
    onLoginButtonClick: () -> Unit = {},
    onSignUpLinkClick: () -> Unit = {}
) {
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
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            viewState = viewState,
            onEmailValueChange = onEmailValueChange,
            onPasswordValueChange = onPasswordValueChange,
            onLoginButtonClick = onLoginButtonClick
        )
        Spacer(modifier = Modifier.height(64.dp))
        SignUpText(onSignUpLinkClick = onSignUpLinkClick)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun LoginCredentialsCard(
    modifier: Modifier = Modifier,
    viewState: LoginViewState = LoginViewState(),
    onEmailValueChange: (String) -> Unit = {},
    onPasswordValueChange: (String) -> Unit = {},
    onLoginButtonClick: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = viewState.emailTextFieldViewState.value,
                onValueChange = onEmailValueChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.email)) },
                isError = viewState.emailTextFieldViewState.isError
            )
            if (viewState.emailTextFieldViewState.isError
                && viewState.emailTextFieldViewState.errorMessage.isNotBlank()
            ) {
                Text(
                    text = viewState.emailTextFieldViewState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = viewState.passwordTextFieldViewState.value,
                onValueChange = onPasswordValueChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.password)) },
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onLoginButtonClick) {
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
fun SignUpText(modifier: Modifier = Modifier, onSignUpLinkClick: () -> Unit = {}) {
    val annotatedText = buildAnnotatedString {
        append(stringResource(R.string.sign_up_prompt))
        append(stringResource(R.string.sign_up_whitespace))
        withLink(
            LinkAnnotation.Clickable(
                tag = SIGN_UP_LINK_ANNOTATION_TAG,
                linkInteractionListener = { onSignUpLinkClick() })
        ) {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(stringResource(R.string.sign_up_link_text))
            }
        }
    }
    Text(text = annotatedText, modifier = modifier)
}

@Preview(showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    HabitTrackerTheme {
        LoginScreen()
    }
}
