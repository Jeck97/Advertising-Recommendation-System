package com.utem.ftmk.ws2.arsclient.ui.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.assistant.DialogAssistant;
import com.utem.ftmk.ws2.arsclient.assistant.InputValidator;
import com.utem.ftmk.ws2.arsclient.model.client.Client;
import com.utem.ftmk.ws2.arsclient.model.client.ClientManager;
import com.utem.ftmk.ws2.arsclient.ui.SelectLocationActivity;

import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    public static final int REQUEST_SELECT_LOCATION = 101;

    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private EditText mEditTextConfirmPassword;
    private EditText mEditTextStoreName;
    private TextView mTextViewLocation;
    private Client.Location mLocation;

    private boolean mIsLocationSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mEditTextEmail = findViewById(R.id.editText_email_address);
        mEditTextPassword = findViewById(R.id.editText_password);
        mEditTextConfirmPassword = findViewById(R.id.editText_password_confirm);
        mEditTextStoreName = findViewById(R.id.editText_store_name);
        mTextViewLocation = findViewById(R.id.textView_location);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_LOCATION) {
            switch (resultCode) {
                case RESULT_OK:
                    assert data != null;
                    mLocation = (Client.Location) data.getSerializableExtra
                            (SelectLocationActivity.SELECTED_LOCATION);
                    mTextViewLocation.setText(mLocation.getAddress());
                    mTextViewLocation.setTextColor(getColor(R.color.hint));
                    mIsLocationSelected = true;
                    break;
                case SelectLocationActivity.RESULT_PERMISSION_DINED:
                    Toast.makeText(this, getString(R.string.error_permission_dined),
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    public void onSelectLocationClicked(View view) {
        startActivityForResult(
                new Intent(RegistrationActivity.this, SelectLocationActivity.class),
                REQUEST_SELECT_LOCATION);
    }

    public void onRegisterClicked(View view) {
        boolean validInput = InputValidator.validateRegistration(
                mEditTextEmail, mEditTextPassword, mEditTextConfirmPassword,
                mEditTextStoreName, mTextViewLocation, mIsLocationSelected);
        if (!validInput) {
            return;
        }

        DialogAssistant.showProgressDialog(this, R.string.progress_registration);
        Client client = new Client(mEditTextEmail.getText().toString().trim(),
                mEditTextPassword.getText().toString(),
                mEditTextStoreName.getText().toString().trim(),
                mLocation);
        ClientManager.registerClient(client, registerTask -> {
            if (registerTask.isSuccessful()) {
                ClientManager.addClient(client, addClientTask -> {
                    DialogAssistant.dismissProgressDialog();
                    if (addClientTask.isSuccessful()) {
                        DialogAssistant.showRegisterSuccessfulDialog(
                                RegistrationActivity.this);
                    } else {
                        Toast.makeText(RegistrationActivity.this,
                                Objects.requireNonNull(addClientTask.getException()).getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                DialogAssistant.dismissProgressDialog();
                Toast.makeText(RegistrationActivity.this,
                        Objects.requireNonNull(registerTask.getException()).getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}