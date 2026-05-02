package com.davidread.habittracker.signup.composable

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.davidread.habittracker.R
import com.davidread.habittracker.common.ui.composable.HabitTrackerAlertDialog
import com.davidread.habittracker.common.ui.composable.HabitTrackerButton
import com.davidread.habittracker.common.ui.composable.HabitTrackerCard
import com.davidread.habittracker.common.ui.composable.HabitTrackerTopAppBar
import com.davidread.habittracker.common.ui.composable.HabitTrackerTextField
import com.davidread.habittracker.common.ui.theme.HabitTrackerTheme
import com.davidread.habittracker.signup.model.SignUpTextFieldViewState
import com.davidread.habittracker.signup.model.SignUpViewEffect
import com.davidread.habittracker.signup.model.SignUpViewIntent
import com.davidread.habittracker.signup.model.SignUpViewState
import com.davidread.habittracker.signup.viewmodel.SignUpViewModel

internal const val SIGN_UP_BUTTON_TEST_TAG = "sign_up_button"

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
    onNavigateToHabitListScreen: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        viewModel.viewEffect.collect { viewEffect ->
            when (viewEffect) {
                is SignUpViewEffect.NavigateToHabitListScreen -> onNavigateToHabitListScreen()
            }
        }
    }

    val viewState by viewModel.viewState.collectAsState()
    SignUpScreenContent(
        modifier = modifier,
        viewState = viewState,
        onNavigateBack = onNavigateBack,
        onEmailValueChange = {
            viewModel.processIntent(SignUpViewIntent.ChangeEmailValue(newValue = it))
        },
        onPasswordValueChange = {
            viewModel.processIntent(SignUpViewIntent.ChangePasswordValue(newValue = it))
        },
        onConfirmPasswordValueChange = {
            viewModel.processIntent(SignUpViewIntent.ChangeConfirmPasswordValue(newValue = it))
        },
        onSignUpButtonClick = {
            viewModel.processIntent(SignUpViewIntent.ClickSignUpButton)
        },
        onAlertDialogButtonClick = {
            viewModel.processIntent(SignUpViewIntent.ClickAlertDialogButton)
        }
    )
}

@Composable
fun SignUpScreenContent(
    modifier: Modifier = Modifier,
    viewState: SignUpViewState = SignUpViewState(),
    onNavigateBack: () -> Unit = {},
    onEmailValueChange: (String) -> Unit = {},
    onPasswordValueChange: (String) -> Unit = {},
    onConfirmPasswordValueChange: (String) -> Unit = {},
    onSignUpButtonClick: () -> Unit = {},
    onAlertDialogButtonClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    Scaffold(
        modifier = modifier,
        topBar = {
            HabitTrackerTopAppBar(
                title = stringResource(R.string.sign_up),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                }
            )
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
                painter = painterResource(id = R.drawable.undraw_sign_up_qamz),
                contentDescription = null,
                modifier = Modifier.size(192.dp)
            )
            Spacer(modifier = Modifier.height(64.dp))
            SignUpCredentialsCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                viewState = viewState,
                onEmailValueChange = onEmailValueChange,
                onPasswordValueChange = onPasswordValueChange,
                onConfirmPasswordValueChange = onConfirmPasswordValueChange,
                onSignUpButtonClick = onSignUpButtonClick
            )
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
fun SignUpCredentialsCard(
    modifier: Modifier = Modifier,
    viewState: SignUpViewState = SignUpViewState(),
    onEmailValueChange: (String) -> Unit = {},
    onPasswordValueChange: (String) -> Unit = {},
    onConfirmPasswordValueChange: (String) -> Unit = {},
    onSignUpButtonClick: () -> Unit = {}
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
                keyboardType = KeyboardType.Email,
            )
            Spacer(modifier = Modifier.height(16.dp))
            HabitTrackerTextField(
                value = viewState.passwordTextFieldViewState.value,
                onValueChange = onPasswordValueChange,
                labelText = stringResource(R.string.password),
                isError = viewState.passwordTextFieldViewState.isError,
                errorMessage = viewState.passwordTextFieldViewState.errorMessage,
                enabled = !viewState.showLoading,
                keyboardType = KeyboardType.Password,
            )
            Spacer(modifier = Modifier.height(16.dp))
            HabitTrackerTextField(
                value = viewState.confirmPasswordTextFieldViewState.value,
                onValueChange = onConfirmPasswordValueChange,
                labelText = stringResource(R.string.confirm_password),
                isError = viewState.confirmPasswordTextFieldViewState.isError,
                errorMessage = viewState.confirmPasswordTextFieldViewState.errorMessage,
                enabled = !viewState.showLoading,
                keyboardType = KeyboardType.Password,
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
                        modifier = Modifier.testTag(SIGN_UP_BUTTON_TEST_TAG),
                        label = stringResource(R.string.sign_up),
                        onClick = onSignUpButtonClick
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SignUpScreenContentPreview_Default() {
    HabitTrackerTheme {
        SignUpScreenContent()
    }
}

@Preview
@Composable
private fun SignUpCredentialsCardPreview_Default() {
    HabitTrackerTheme {
        SignUpCredentialsCard()
    }
}

@Preview
@Composable
private fun SignUpCredentialsCardPreview_FieldsFilled() {
    HabitTrackerTheme {
        SignUpCredentialsCard(
            viewState = SignUpViewState(
                emailTextFieldViewState = SignUpTextFieldViewState(value = "david.read@gmail.com"),
                passwordTextFieldViewState = SignUpTextFieldViewState(value = "password"),
                confirmPasswordTextFieldViewState = SignUpTextFieldViewState(value = "password")
            )
        )
    }
}

@Preview
@Composable
private fun SignUpCredentialsCardPreview_FieldsInvalid() {
    HabitTrackerTheme {
        SignUpCredentialsCard(
            viewState = SignUpViewState(
                emailTextFieldViewState = SignUpTextFieldViewState(
                    value = "invalid email",
                    isError = true,
                    errorMessage = stringResource(R.string.email_validation_error_message)
                ),
                passwordTextFieldViewState = SignUpTextFieldViewState(
                    value = "1234",
                    isError = true,
                    errorMessage = stringResource(R.string.password_validation_error_message)
                ),
                confirmPasswordTextFieldViewState = SignUpTextFieldViewState(
                    value = "123",
                    isError = true,
                    errorMessage = stringResource(R.string.confirm_password_validation_error_message)
                )
            )
        )
    }
}
