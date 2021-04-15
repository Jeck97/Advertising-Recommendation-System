package com.utem.ftmk.ws2.arsconsumer.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.utem.ftmk.ws2.arsconsumer.ui.MainActivity;
import com.utem.ftmk.ws2.arsconsumer.R;

import static com.utem.ftmk.ws2.arsconsumer.utils.DialogUtil.dismissProgressDialog;
import static com.utem.ftmk.ws2.arsconsumer.utils.DialogUtil.showProgressDialog;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText etEmail;
    private EditText etPassword;

    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
    }

    public void onLoginClick(View view) {
        if (validateForm()) {
            loginConsumer();
        }
    }

    public void onSignUpClick(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private boolean validateForm() {
        boolean valid = true;
        email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_field_required));
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.error_format_email));
            valid = false;
        }

        password = etPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_field_required));
            valid = false;
        }
        return valid;
    }

    private void loginConsumer() {
        showProgressDialog(this, R.string.progress_login);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    dismissProgressDialog();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    dismissProgressDialog();
                    String error = e.getMessage();
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                    Log.e(TAG, error, e);
                });
    }
}