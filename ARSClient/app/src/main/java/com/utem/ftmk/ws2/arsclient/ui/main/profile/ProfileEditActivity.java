package com.utem.ftmk.ws2.arsclient.ui.main.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.assistant.DialogAssistant;
import com.utem.ftmk.ws2.arsclient.assistant.InputValidator;
import com.utem.ftmk.ws2.arsclient.model.client.Client;
import com.utem.ftmk.ws2.arsclient.model.client.ClientManager;
import com.utem.ftmk.ws2.arsclient.ui.SelectLocationActivity;

public class ProfileEditActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_LOCATION = 101;
    private static final int REQUEST_PICK_IMAGE = 102;

    private ImageView mImageViewLogo;
    private TextInputEditText mEditTextStoreName;
    private TextInputEditText mEditTextDescription;
    private TextView mTextViewLocation;

    private Client mClient;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        mImageViewLogo = findViewById(R.id.imageView_profile_edit_logo);
        mEditTextStoreName = findViewById(R.id.editText_profile_edit_store_name);
        mEditTextDescription = findViewById(R.id.editText_profile_edit_store_description);
        mTextViewLocation = findViewById(R.id.textView_profile_edit_store_location);

        mClient = (Client) getIntent().getSerializableExtra(ProfileFragment.EXTRA_CLIENT);
        if (mClient.getLogo() != null) {
            Glide.with(this).load(Uri.parse(mClient.getLogo())).into(mImageViewLogo);
        }
        mEditTextStoreName.setText(mClient.getStoreName());
        mEditTextDescription.setText(mClient.getDescription());
        mTextViewLocation.setText(mClient.getLocation().getAddress());
    }

    public void onSaveClicked(View view) {
        if (InputValidator.validateProfile(mEditTextStoreName)) {
            mClient.setStoreName(mEditTextStoreName.getText().toString().trim());
            mClient.setDescription(mEditTextDescription.getText().toString().trim());

            DialogAssistant.showProgressDialog(this, R.string.progress_update_profile);
            if (mImageUri != null) {
                if (mClient.getLogo() == null) {
                    ClientManager.addStorageImage(mImageUri, task -> {
                        if (task.isSuccessful()) {
                            mClient.setLogo(task.getResult().toString());
                            ClientManager.updateClient(mClient, task1 -> {
                                DialogAssistant.dismissProgressDialog();
                                if (task1.isSuccessful()) {
                                    DialogAssistant.showMessageDialog(
                                            ProfileEditActivity.this,
                                            R.string.dialog_message_profile_updated,
                                            this::finish);
                                } else {
                                    Toast.makeText(ProfileEditActivity.this,
                                            task1.getException().getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(ProfileEditActivity.this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    ClientManager.deleteStorageImage(mClient.getLogo(), task -> {
                        if (task.isSuccessful()) {
                            ClientManager.addStorageImage(mImageUri, task1 -> {
                                if (task1.isSuccessful()) {
                                    mClient.setLogo(task1.getResult().toString());
                                    ClientManager.updateClient(mClient, task2 -> {
                                        DialogAssistant.dismissProgressDialog();
                                        if (task2.isSuccessful()) {
                                            DialogAssistant.showMessageDialog(
                                                    ProfileEditActivity.this,
                                                    R.string.dialog_message_profile_updated,
                                                    this::finish);
                                        } else {
                                            Toast.makeText(ProfileEditActivity.this,
                                                    task2.getException().getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    DialogAssistant.dismissProgressDialog();
                                    Toast.makeText(ProfileEditActivity.this,
                                            task1.getException().getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            DialogAssistant.dismissProgressDialog();
                            Toast.makeText(ProfileEditActivity.this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                ClientManager.updateClient(mClient, task -> {
                    DialogAssistant.dismissProgressDialog();
                    if (task.isSuccessful()) {
                        DialogAssistant.showMessageDialog(ProfileEditActivity.this,
                                R.string.dialog_message_profile_updated, this::finish);
                    } else {
                        Toast.makeText(ProfileEditActivity.this,
                                task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    public void onEditLogoClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    public void onSelectLocationClicked(View view) {
        Intent intent = new Intent(this, SelectLocationActivity.class);
        intent.putExtra(SelectLocationActivity.EXTRA_INIT_LOCATION, mClient.getLocation());
        startActivityForResult(intent, REQUEST_SELECT_LOCATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SELECT_LOCATION:
                if (resultCode == RESULT_OK && data != null) {
                    mClient.setLocation((Client.Location) data.getSerializableExtra
                            (SelectLocationActivity.SELECTED_LOCATION));
                    mTextViewLocation.setText(mClient.getLocation().getAddress());
                } else if (resultCode == SelectLocationActivity.RESULT_PERMISSION_DINED) {
                    Toast.makeText(this, getString(R.string.error_permission_dined),
                            Toast.LENGTH_LONG).show();
                }
                break;

            case REQUEST_PICK_IMAGE:
                if (resultCode == RESULT_OK && data != null) {
                    mImageUri = data.getData();
                    Glide.with(this).load(mImageUri).circleCrop().into(mImageViewLogo);
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}