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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.davidread.habittracker.R
import com.davidread.habittracker.common.ui.composable.AlertDialog
import com.davidread.habittracker.common.ui.theme.Color
import com.davidread.habittracker.common.ui.theme.HabitTrackerTheme
import com.davidread.habittracker.login.model.LoginTextFieldViewState
import com.davidread.habittracker.login.model.LoginViewEffect
import com.davidread.habittracker.login.model.LoginViewIntent
import com.davidread.habittracker.login.model.LoginViewState
import com.davidread.habittracker.login.viewmodel.LoginViewModel

private const val SIGN_UP_LINK_ANNOTATION_TAG = "sign_up"

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToHabitListScreen: () -> Unit = {},
    onNavigateToSignUpScreen: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        viewModel.viewEffect.collect { viewEffect ->
            when (viewEffect) {
                is LoginViewEffect.NavigateToHabitListScreen -> onNavigateToHabitListScreen()
                is LoginViewEffect.NavigateToSignUpScreen -> onNavigateToSignUpScreen()
            }
        }
    }

    val viewState by viewModel.viewState.collectAsState()
    LoginScreenContent(
        modifier = modifier,
        viewState = viewState,
        onEmailValueChange = {
            viewModel.processIntent(
                intent = LoginViewIntent.ChangeEmailValue(
                    newValue = it
                )
            )
        },
        onPasswordValueChange = {
            viewModel.processIntent(
                intent = LoginViewIntent.ChangePasswordValue(
                    newValue = it
                )
            )
        },
        onLoginButtonClick = {
            viewModel.processIntent(intent = LoginViewIntent.ClickLoginButton)
        },
        onSignUpLinkClick = {
            viewModel.processIntent(intent = LoginViewIntent.ClickSignUpLink)
        },
        onAlertDialogButtonClick = {
            viewModel.processIntent(intent = LoginViewIntent.ClickAlertDialogButton)
        }
    )
}

@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    viewState: LoginViewState = LoginViewState(),
    onEmailValueChange: (String) -> Unit = {},
    onPasswordValueChange: (String) -> Unit = {},
    onLoginButtonClick: () -> Unit = {},
    onSignUpLinkClick: () -> Unit = {},
    onAlertDialogButtonClick: () -> Unit = {}
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

    if (viewState.alertDialogViewState.showDialog) {
        AlertDialog(
            message = viewState.alertDialogViewState.message,
            onButtonClick = onAlertDialogButtonClick
        )
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
            LoginTextField(
                viewState = viewState.emailTextFieldViewState,
                onValueChange = onEmailValueChange,
                labelText = stringResource(R.string.email),
                keyboardType = KeyboardType.Email,
            )
            Spacer(modifier = Modifier.height(16.dp))
            LoginTextField(
                viewState = viewState.passwordTextFieldViewState,
                onValueChange = onPasswordValueChange,
                labelText = stringResource(R.string.password),
                visualTransformation = PasswordVisualTransformation(),
                keyboardType = KeyboardType.Password,
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
fun LoginTextField(
    modifier: Modifier = Modifier,
    viewState: LoginTextFieldViewState,
    onValueChange: (String) -> Unit = {},
    labelText: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Unspecified
) {
    TextField(
        value = viewState.value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(labelText) },
        isError = viewState.isError,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done
        ),
        singleLine = true
    )
    if (viewState.isError && viewState.errorMessage.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = viewState.errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
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
private fun LoginScreenContentPreview_Default() {
    HabitTrackerTheme {
        LoginScreenContent()
    }
}

@Preview
@Composable
private fun LoginCredentialsCardPreview_Default() {
    HabitTrackerTheme {
        LoginCredentialsCard()
    }
}

@Preview
@Composable
private fun LoginCredentialsCardPreview_FieldsFilled() {
    HabitTrackerTheme {
        LoginCredentialsCard(
            viewState = LoginViewState(
                emailTextFieldViewState = LoginTextFieldViewState(
                    value = "david.read@gmail.com"
                ),
                passwordTextFieldViewState = LoginTextFieldViewState(
                    value = "password"
                )
            )
        )
    }
}

@Preview
@Composable
private fun LoginCredentialsCardPreview_FieldsInvalid() {
    HabitTrackerTheme {
        LoginCredentialsCard(
            viewState = LoginViewState(
                emailTextFieldViewState = LoginTextFieldViewState(
                    value = "invalid email",
                    isError = true,
                    errorMessage = stringResource(R.string.email_validation_error_message)
                ),
                passwordTextFieldViewState = LoginTextFieldViewState(
                    value = "1234",
                    isError = true,
                    errorMessage = stringResource(R.string.password_validation_error_message)
                )
            )
        )
    }
}
