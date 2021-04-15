package com.utem.ftmk.ws2.arsconsumer.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.utem.ftmk.ws2.arsconsumer.R;
import com.utem.ftmk.ws2.arsconsumer.model.Consumer;
import com.utem.ftmk.ws2.arsconsumer.utils.DateTimeUtil;

import java.util.Calendar;

import static com.utem.ftmk.ws2.arsconsumer.utils.DateTimeUtil.format;
import static com.utem.ftmk.ws2.arsconsumer.utils.DialogUtil.dismissProgressDialog;
import static com.utem.ftmk.ws2.arsconsumer.utils.DialogUtil.showProgressDialog;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private EditText etEmail;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etPasswordConfirmed;
    private TextView tvDob;
    private RadioGroup rgGender;

    private String email;
    private String username;
    private String password;
    private String gender;
    private long dob;

    private final OnFailureListener onFailureListener = e -> {
        String error = e.getMessage();
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        Log.e(TAG, error, e);
        dismissProgressDialog();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.et_register_email);
        etUsername = findViewById(R.id.et_register_username);
        etPassword = findViewById(R.id.et_register_password);
        etPasswordConfirmed = findViewById(R.id.et_register_password_confirm);
        tvDob = findViewById(R.id.tv_register_dob);
        rgGender = findViewById(R.id.rg_register_gender);

    }

    public void onDobClick(View view) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dob != 0 ? dob : System.currentTimeMillis());
        new DatePickerDialog(this,
                (datePicker, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    dob = calendar.getTimeInMillis();
                    if ((System.currentTimeMillis() - dob) <= DateTimeUtil.TWELVE_YEAR) {
                        dob = 0;
                        Toast.makeText(this, R.string.error_invalid_age,
                                Toast.LENGTH_LONG).show();
                    } else {
                        tvDob.setText(format(dob));
                        tvDob.setTextColor(getColor(R.color.black));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    public void onRegisterClick(View view) {
        if (validateForm()) {
            registerConsumer();
        }
    }

    public void onSignInClick(View view) {
        finish();
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

        username = etUsername.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            etUsername.setError(getString(R.string.error_field_required));
            valid = false;
        }

        password = etPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_field_required));
            valid = false;
        } else if (password.length() < 8) {
            etPassword.setError(getString(R.string.error_minimum_password));
            valid = false;
        }

        String confirmedPassword = etPasswordConfirmed.getText().toString();
        if (TextUtils.isEmpty(confirmedPassword)) {
            etPasswordConfirmed.setError(getString(R.string.error_field_required));
            valid = false;
        } else if (!password.equals(confirmedPassword)) {
            etPasswordConfirmed.setError(getString(R.string.error_not_match_confirm_password));
            valid = false;
        }

        if (dob == 0) {
            tvDob.setTextColor(getColor(R.color.red));
            valid = false;
        }

        gender = rgGender.getCheckedRadioButtonId() == R.id.rb_register_male ?
                Consumer.GENDER_MALE : Consumer.GENDER_FEMALE;

        return valid;
    }

    private void registerConsumer() {
        showProgressDialog(this, R.string.progress_registration);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> addConsumer(auth.getUid()))
                .addOnFailureListener(onFailureListener);
    }

    private void addConsumer(String uid) {
        FirebaseDatabase.getInstance().getReference().child(Consumer.FIREBASE_IDENTIFIER).child(uid)
                .setValue(new Consumer(uid, email, username, gender, dob))
                .addOnSuccessListener(aVoid -> showSuccessfulDialog())
                .addOnFailureListener(onFailureListener);
    }

    private void showSuccessfulDialog() {
        dismissProgressDialog();
        FirebaseAuth.getInstance().signOut();
        new AlertDialog.Builder(this)
                .setMessage(R.string.dialog_message_register_successful)
                .setPositiveButton(R.string.button_ok, (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

}