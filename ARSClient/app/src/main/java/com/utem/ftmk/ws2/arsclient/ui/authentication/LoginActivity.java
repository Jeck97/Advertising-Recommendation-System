package com.utem.ftmk.ws2.arsclient.ui.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.assistant.DialogAssistant;
import com.utem.ftmk.ws2.arsclient.assistant.InputValidator;
import com.utem.ftmk.ws2.arsclient.model.client.Client;
import com.utem.ftmk.ws2.arsclient.model.client.ClientManager;
import com.utem.ftmk.ws2.arsclient.ui.main.HomeActivity;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_REGISTRATION = 101;

    private EditText mEditTextEmail;
    private EditText mEditTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEditTextEmail = findViewById(R.id.editText_email_address);
        mEditTextPassword = findViewById(R.id.editText_password);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REGISTRATION) {
            if (resultCode == RESULT_OK) {
                DialogAssistant.showProgressDialog(LoginActivity.this,
                        R.string.progress_send_validation_email);
                ClientManager.sendClientEmailVerification(task -> {
                    DialogAssistant.dismissProgressDialog();
                    DialogAssistant.showEmailValidationDialog(LoginActivity.this);
                    ClientManager.signOutClient();
                });
            }
        }
    }

    public void onRegisterAccountClicked(View view) {
        startActivityForResult(
                new Intent(LoginActivity.this, RegistrationActivity.class),
                REQUEST_REGISTRATION);
    }

    public void onForgotPasswordClicked(View view) {
        DialogAssistant.showForgotPasswordDialog(this);
    }

    public void onLoginClicked(View view) {
        boolean validInput = InputValidator.validateLogin(mEditTextEmail, mEditTextPassword);
        if (!validInput) {
            return;
        }

        DialogAssistant.showProgressDialog(this, R.string.progress_login);
        ClientManager.loginClient(new Client(
                mEditTextEmail.getText().toString().trim(),
                mEditTextPassword.getText().toString()), task -> {
            DialogAssistant.dismissProgressDialog();
            if (task.isSuccessful()) {
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this,
                        Objects.requireNonNull(task.getException()).getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}