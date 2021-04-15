package com.utem.ftmk.ws2.arsconsumer.ui.profile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.utem.ftmk.ws2.arsconsumer.R;
import com.utem.ftmk.ws2.arsconsumer.model.Consumer;
import com.utem.ftmk.ws2.arsconsumer.utils.DateTimeUtil;
import com.utem.ftmk.ws2.arsconsumer.utils.DialogUtil;

import java.util.Calendar;

import static android.app.Activity.RESULT_OK;
import static com.utem.ftmk.ws2.arsconsumer.utils.DateTimeUtil.format;

public class ProfileFragment extends Fragment {

    private static final int REQUEST_PICK_IMAGE = 301;

    private ProfileViewModel profileViewModel;

    private com.google.android.material.floatingactionbutton.FloatingActionButton fabProfileEdit;
    private ImageView ivProfile;
    private TextView tvEmail;
    private EditText etUsername;
    private EditText etDob;
    private RadioGroup rgGender;
    private RadioButton rbMail;
    private RadioButton rbFemale;
    private LinearLayout layoutButtons;
    private FloatingActionMenu fam;

    private Uri imageUri = null;
    private long dob = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        fabProfileEdit = root.findViewById(R.id.fab_consumer_profile_edit);
        fabProfileEdit.setOnClickListener(editImageListener);

        ivProfile = root.findViewById(R.id.iv_consumer_profile);
        tvEmail = root.findViewById(R.id.tv_profile_email);

        etUsername = root.findViewById(R.id.et_profile_username);
        etDob = root.findViewById(R.id.et_profile_dob);
        etDob.setOnClickListener(datePickerListener);

        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_enabled}, //disabled
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[]{
                        getContext().getColor(R.color.hint), //disabled
                        getContext().getColor(R.color.teal_700) //enabled
                }
        );
        rgGender = root.findViewById(R.id.rg_profile_gender);
        rbMail = root.findViewById(R.id.rb_profile_male);
        rbMail.setButtonTintList(colorStateList);
        rbFemale = root.findViewById(R.id.rb_profile_female);
        rbFemale.setButtonTintList(colorStateList);

        fam = root.findViewById(R.id.fam_action);
        FloatingActionButton fabEditProfile = root.findViewById(R.id.fab_edit_profile);
        fabEditProfile.setOnClickListener(editProfileListener);
        FloatingActionButton fabChangeEmail = root.findViewById(R.id.fab_change_email);
        fabChangeEmail.setOnClickListener(changeEmailListener);
        FloatingActionButton fabChangePassword = root.findViewById(R.id.fab_change_password);
        fabChangePassword.setOnClickListener(changePasswordListener);
        Button btnSave = root.findViewById(R.id.btn_profile_save);
        btnSave.setOnClickListener(saveListener);
        Button btnCancel = root.findViewById(R.id.btn_profile_cancel);
        btnCancel.setOnClickListener(cancelListener);
        layoutButtons = root.findViewById(R.id.layout_buttons);

        profileViewModel.getLiveConsumer().observe(getViewLifecycleOwner(), this::init);

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Glide.with(requireActivity()).load(imageUri).circleCrop().into(ivProfile);
        }
    }

    private void init(Consumer consumer) {
        if (consumer.getProfileImage() != null) {
            Glide.with(requireActivity()).load(Uri.parse(consumer.getProfileImage()))
                    .centerCrop().into(ivProfile);
        }
        tvEmail.setText(consumer.getEmail());
        etUsername.setText(consumer.getUsername());
        etDob.setText(DateTimeUtil.format(consumer.getDob()));
        rgGender.check(consumer.getGender().equals(Consumer.GENDER_MALE) ?
                R.id.rb_profile_male : R.id.rb_profile_female);

        imageUri = null;
        dob = consumer.getDob();
    }

    private void updateUI(boolean edit) {
        int color = edit ?
                getContext().getColor(R.color.teal_700) :
                getContext().getColor(R.color.hint);
        if (edit) {
            fam.hideMenu(true);
        } else {
            fam.showMenu(true);
        }
        fabProfileEdit.setVisibility(edit ? View.VISIBLE : View.GONE);
        etUsername.setEnabled(edit);
        etUsername.setTextColor(color);
        etUsername.setError(null);
        etDob.setEnabled(edit);
        etDob.setTextColor(color);
        etDob.setError(null);
        etDob.getCompoundDrawables()[2].setColorFilter(new PorterDuffColorFilter(
                color, PorterDuff.Mode.SRC_IN));
        rbMail.setEnabled(edit);
        rbFemale.setEnabled(edit);
        layoutButtons.setVisibility(edit ? View.VISIBLE : View.GONE);
    }

    private void reAuthenticateUser(String email, String password,
                                    ReAuthenticateSuccessfulListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication.
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password);

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(onFailureListener);
    }

    private void updateEmail(String email) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updateEmail(email).addOnSuccessListener(aVoid -> {
            Consumer consumer = profileViewModel.getConsumer();
            consumer.setEmail(email);
            FirebaseDatabase.getInstance().getReference()
                    .child(Consumer.FIREBASE_IDENTIFIER)
                    .child(user.getUid()).setValue(consumer)
                    .addOnSuccessListener(aVoid1 -> {
                        DialogUtil.dismissProgressDialog();
                        displayToast(R.string.message_email_updated);
                    })
                    .addOnFailureListener(onFailureListener);
        }).addOnFailureListener(onFailureListener);
    }

    private void updatePassword(String password) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updatePassword(password)
                .addOnSuccessListener(aVoid -> {
                    DialogUtil.dismissProgressDialog();
                    displayToast(R.string.message_password_updated);
                })
                .addOnFailureListener(onFailureListener);
    }

    private void updateConsumer(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Consumer consumer = profileViewModel.getConsumer();
        consumer.setUsername(etUsername.getText().toString().trim());
        consumer.setDob(dob);
        consumer.setGender(rgGender.getCheckedRadioButtonId() == R.id.rb_profile_male ?
                Consumer.GENDER_MALE : Consumer.GENDER_FEMALE);
        consumer.setProfileImage(uri != null ? uri.toString() : null);

        FirebaseDatabase.getInstance().getReference()
                .child(Consumer.FIREBASE_IDENTIFIER)
                .child(user.getUid()).setValue(consumer)
                .addOnSuccessListener(aVoid -> {
                    DialogUtil.dismissProgressDialog();
                    displayToast(R.string.message_profile_updated);
                })
                .addOnFailureListener(onFailureListener);
    }

    private void updateConsumerWithImage() {
        StorageReference currentRef = FirebaseStorage.getInstance().getReference()
                .child(Consumer.FIREBASE_IDENTIFIER)
                .child(FirebaseAuth.getInstance().getUid())
                .child(imageUri.getLastPathSegment());

        String currentUri = profileViewModel.getConsumer().getProfileImage();
        if (currentUri != null) {
            StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(currentUri);
            ref.delete().addOnCompleteListener(aVoid ->
                    currentRef.putFile(imageUri).continueWithTask(task ->
                            currentRef.getDownloadUrl())
                            .addOnSuccessListener(this::updateConsumer)
                            .addOnFailureListener(onFailureListener));
        } else {
            currentRef.putFile(imageUri).continueWithTask(task -> currentRef.getDownloadUrl())
                    .addOnSuccessListener(this::updateConsumer)
                    .addOnFailureListener(onFailureListener);
        }
    }

    private boolean validateForm(EditText etEmail, EditText etPassword, EditText etPasswordNew) {

        boolean valid = true;
        if (etEmail != null) {
            String email = etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                etEmail.setError(getString(R.string.error_field_required));
                valid = false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError(getString(R.string.error_format_email));
                valid = false;
            }
        }

        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_field_required));
            valid = false;
        }

        if (etPasswordNew != null) {
            String newPassword = etPasswordNew.getText().toString();
            if (TextUtils.isEmpty(newPassword)) {
                etPasswordNew.setError(getString(R.string.error_field_required));
                valid = false;
            } else if (newPassword.length() < 8) {
                etPasswordNew.setError(getString(R.string.error_minimum_password));
                valid = false;
            } else if (newPassword.equals(password)) {
                etPasswordNew.setError(getString(R.string.error_duplicate_password));
                valid = false;
            }
        }

        return valid;
    }

    private boolean validateForm() {

        boolean valid = true;
        String username = etUsername.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            etUsername.setError(getString(R.string.error_field_required));
            valid = false;
        }

        return valid;
    }

    private void displayToast(@StringRes int message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show();
    }

    private void displayToast(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show();
    }

    private final View.OnClickListener saveListener = v -> {
        if (validateForm()) {
            updateUI(false);
            DialogUtil.showProgressDialog(requireActivity(), R.string.progress_update);
            if (imageUri != null) {
                updateConsumerWithImage();
            } else {
                updateConsumer(null);
            }
        }
    };

    private final View.OnClickListener cancelListener = v -> {
        updateUI(false);
        init(profileViewModel.getConsumer());
    };

    private final View.OnClickListener datePickerListener = v -> {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dob);
        new DatePickerDialog(requireActivity(), (datePicker, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            if ((System.currentTimeMillis() - calendar.getTimeInMillis())
                    <= DateTimeUtil.TWELVE_YEAR) {
                displayToast(R.string.error_invalid_age);
            } else {
                dob = calendar.getTimeInMillis();
                etDob.setText(format(dob));
            }
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    };

    private final View.OnClickListener editImageListener = v -> {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    };

    private final View.OnClickListener editProfileListener = v -> updateUI(true);

    private final View.OnClickListener changeEmailListener = v -> {
        fam.close(false);
        EditText etEmail = new EditText(requireActivity());
        etEmail.setPadding(50, 50, 50, 50);
        etEmail.setHint(R.string.hint_email_new);
        etEmail.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        EditText etPassword = new EditText(requireActivity());
        etPassword.setPadding(50, 50, 50, 50);
        etPassword.setHint(R.string.hint_password_current);
        etPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout layout = new LinearLayout(requireActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(params);
        layout.setPadding(50, 50, 50, 100);
        layout.addView(etEmail, params);
        layout.addView(etPassword, params);

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.dialog_title_change_email)
                .setView(layout)
                .setPositiveButton(R.string.button_submit, null)
                .setNegativeButton(R.string.button_cancel, null)
                .show();

        Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btnPositive.setOnClickListener(v1 -> {
            if (validateForm(etEmail, etPassword, null)) {
                DialogUtil.showProgressDialog(requireActivity(), R.string.progress_update);
                String oldEmail = profileViewModel.getConsumer().getEmail();
                String newEmail = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString();
                reAuthenticateUser(oldEmail, password, () -> updateEmail(newEmail));
                dialog.dismiss();
            }
        });
    };

    private final View.OnClickListener changePasswordListener = v -> {
        fam.close(false);
        EditText etPassword = new EditText(requireActivity());
        etPassword.setPadding(50, 50, 50, 50);
        etPassword.setHint(R.string.hint_password_current);
        etPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);

        EditText etPasswordNew = new EditText(requireActivity());
        etPasswordNew.setPadding(50, 50, 50, 50);
        etPasswordNew.setHint(R.string.hint_password_new);
        etPasswordNew.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout layout = new LinearLayout(requireActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(params);
        layout.setPadding(50, 50, 50, 100);
        layout.addView(etPassword, params);
        layout.addView(etPasswordNew, params);

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.dialog_title_change_password)
                .setView(layout)
                .setPositiveButton(R.string.button_submit, null)
                .setNegativeButton(R.string.button_cancel, null)
                .show();

        Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btnPositive.setOnClickListener(v1 -> {
            if (validateForm(null, etPassword, etPasswordNew)) {
                DialogUtil.showProgressDialog(requireActivity(), R.string.progress_update);
                String email = profileViewModel.getConsumer().getEmail();
                String oldPassword = etPassword.getText().toString();
                String newPassword = etPasswordNew.getText().toString();
                reAuthenticateUser(email, oldPassword, () -> updatePassword(newPassword));
                dialog.dismiss();
            }
        });
    };

    private final OnFailureListener onFailureListener = e -> {
        DialogUtil.dismissProgressDialog();
        displayToast(e.getMessage());
    };

private interface ReAuthenticateSuccessfulListener {
    void onSuccess();
}
}