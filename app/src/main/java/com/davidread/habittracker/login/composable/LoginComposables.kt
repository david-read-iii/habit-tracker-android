package com.davidread.habittracker.login.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
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
import com.davidread.habittracker.common.ui.composable.HabitTrackerAlertDialog
import com.davidread.habittracker.common.ui.composable.HabitTrackerButton
import com.davidread.habittracker.common.ui.composable.HabitTrackerCard
import com.davidread.habittracker.common.ui.composable.HabitTrackerTextField
import com.davidread.habittracker.common.ui.composable.HabitTrackerTopAppBar
import com.davidread.habittracker.common.ui.theme.HabitTrackerTheme
import com.davidread.habittracker.login.model.LoginTextFieldViewState
import com.davidread.habittracker.login.model.LoginViewEffect
import com.davidread.habittracker.login.model.LoginViewIntent
import com.davidread.habittracker.login.model.LoginViewState
import com.davidread.habittracker.login.viewmodel.LoginViewModel

internal const val LOGIN_BUTTON_TEST_TAG = "login_button"
internal const val SIGN_UP_LINK_TEST_TAG = "sign_up_link"
internal const val CLEAR_EMAIL_BUTTON_TEST_TAG = "clear_email_button"
internal const val TOGGLE_PASSWORD_VISIBILITY_BUTTON_TEST_TAG = "toggle_password_visibility_button"
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
        onClearEmailButtonClick = {
            viewModel.processIntent(intent = LoginViewIntent.ClickClearEmailButton)
        },
        onTogglePasswordVisibilityButtonClick = {
            viewModel.processIntent(intent = LoginViewIntent.ClickTogglePasswordVisibilityButton)
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
    onClearEmailButtonClick: () -> Unit = {},
    onTogglePasswordVisibilityButtonClick: () -> Unit = {},
    onLoginButtonClick: () -> Unit = {},
    onSignUpLinkClick: () -> Unit = {},
    onAlertDialogButtonClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    Scaffold(
        modifier = modifier,
        topBar = {
            HabitTrackerTopAppBar(title = stringResource(R.string.login))
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                viewState = viewState,
                onEmailValueChange = onEmailValueChange,
                onPasswordValueChange = onPasswordValueChange,
                onClearEmailButtonClick = onClearEmailButtonClick,
                onTogglePasswordVisibilityButtonClick = onTogglePasswordVisibilityButtonClick,
                onLoginButtonClick = onLoginButtonClick
            )
            Spacer(modifier = Modifier.height(64.dp))
            SignUpText(onSignUpLinkClick = onSignUpLinkClick)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (viewState.alertDialogViewState.showDialog) {
        HabitTrackerAlertDialog(
            message = viewState.alertDialogViewState.message,
            onPrimaryButtonClick = onAlertDialogButtonClick
        )
    }
}

@Composable
fun LoginCredentialsCard(
    modifier: Modifier = Modifier,
    viewState: LoginViewState = LoginViewState(),
    onEmailValueChange: (String) -> Unit = {},
    onPasswordValueChange: (String) -> Unit = {},
    onClearEmailButtonClick: () -> Unit = {},
    onTogglePasswordVisibilityButtonClick: () -> Unit = {},
    onLoginButtonClick: () -> Unit = {}
) {
    HabitTrackerCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            HabitTrackerTextField(
                value = viewState.emailTextFieldViewState.value,
                onValueChange = onEmailValueChange,
                labelText = stringResource(R.string.email),
                isError = viewState.emailTextFieldViewState.isError,
                errorMessage = viewState.emailTextFieldViewState.errorMessage,
                enabled = !viewState.showLoading,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false
                ),
                trailingIcon = {
                    if (viewState.emailTextFieldViewState.value.isNotBlank() && !viewState.showLoading) {
                        IconButton(
                            modifier = Modifier.testTag(CLEAR_EMAIL_BUTTON_TEST_TAG),
                            onClick = onClearEmailButtonClick
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = stringResource(R.string.clear_email)
                            )
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            HabitTrackerTextField(
                value = viewState.passwordTextFieldViewState.value,
                onValueChange = onPasswordValueChange,
                labelText = stringResource(R.string.password),
                isError = viewState.passwordTextFieldViewState.isError,
                errorMessage = viewState.passwordTextFieldViewState.errorMessage,
                enabled = !viewState.showLoading,
                visualTransformation = if (viewState.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (!viewState.showLoading) {
                            onLoginButtonClick()
                        }
                    }
                ),
                trailingIcon = {
                    IconButton(
                        modifier = Modifier.testTag(TOGGLE_PASSWORD_VISIBILITY_BUTTON_TEST_TAG),
                        onClick = onTogglePasswordVisibilityButtonClick,
                        enabled = !viewState.showLoading
                    ) {
                        Icon(
                            imageVector = if (viewState.isPasswordVisible) {
                                Icons.Filled.VisibilityOff
                            } else {
                                Icons.Filled.Visibility
                            },
                            contentDescription = stringResource(
                                if (viewState.isPasswordVisible) {
                                    R.string.hide_password
                                } else {
                                    R.string.show_password
                                }
                            )
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (viewState.showLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(40.dp))
                } else {
                    HabitTrackerButton(
                        modifier = Modifier.testTag(LOGIN_BUTTON_TEST_TAG),
                        label = stringResource(R.string.login),
                        onClick = onLoginButtonClick
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
    Text(
        text = annotatedText,
        modifier = modifier
            .testTag(SIGN_UP_LINK_TEST_TAG)
            .semantics {
                onClick {
                    onSignUpLinkClick()
                    true
                }
            }
    )
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
