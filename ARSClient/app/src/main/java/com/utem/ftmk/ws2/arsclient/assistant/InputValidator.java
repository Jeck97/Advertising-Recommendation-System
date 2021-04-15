package com.utem.ftmk.ws2.arsclient.assistant;

import android.opengl.ETC1;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.utem.ftmk.ws2.arsclient.R;

public class InputValidator {

    public static boolean validateRegistration(EditText editTextEmail,
                                               EditText editTextPassword,
                                               EditText editTextConfirmPassword,
                                               EditText editTextStoreName,
                                               TextView textViewLocation,
                                               boolean isLocationSelected) {
        boolean validEmail = false;
        String email = editTextEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError(App.getContext().getString(R.string.error_input_required));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(App.getContext().getString(R.string.error_format_email));
        } else {
            validEmail = true;
        }

        boolean validPassword = false;
        String password = editTextPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError(App.getContext().getString(R.string.error_input_required));
        } else if (password.length() < 8) {
            editTextPassword.setError(App.getContext().getString(R.string.error_minimum_password));
        } else {
            validPassword = true;
        }

        boolean validConfirmed = false;
        String confirmedPassword = editTextConfirmPassword.getText().toString();
        if (TextUtils.isEmpty(confirmedPassword)) {
            editTextConfirmPassword.setError(App.getContext().getString(R.string.error_input_required));
        } else if (!password.equals(confirmedPassword)) {
            editTextConfirmPassword.setError(App.getContext().getString(R.string.error_not_match_confirm_password));
        } else {
            validConfirmed = true;
        }

        boolean validStoreName = false;
        String storeName = editTextStoreName.getText().toString().trim();
        if (TextUtils.isEmpty(storeName)) {
            editTextStoreName.setError(App.getContext().getString(R.string.error_input_required));
        } else {
            validStoreName = true;
        }

        if (!isLocationSelected) {
            textViewLocation.setTextColor(App.getContext().getColor(R.color.red));
        }

        return validEmail && validPassword && validConfirmed && validStoreName && isLocationSelected;
    }

    public static boolean validateLogin(EditText editTextEmail, EditText editTextPassword) {
        boolean validEmail = false;
        String email = editTextEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError(App.getContext().getString(R.string.error_input_required));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(App.getContext().getString(R.string.error_format_email));
        } else {
            validEmail = true;
        }

        boolean validPassword = false;
        String password = editTextPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError(App.getContext().getString(R.string.error_input_required));
        } else {
            validPassword = true;
        }

        return validEmail && validPassword;
    }

    public static boolean validateForgotPassword(EditText editTextEmail) {
        boolean validEmail = false;
        String email = editTextEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError(App.getContext().getString(R.string.error_input_required));
            editTextEmail.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(App.getContext().getString(R.string.error_format_email));
            editTextEmail.requestFocus();
        } else {
            validEmail = true;
        }
        return validEmail;
    }

    public static boolean validateAdvertisement(AdvertisementViewGroup.TextGroup textGroup,
                                                AdvertisementViewGroup.CategoryGroup categoryGroup,
                                                AdvertisementViewGroup.ImageGroup imageGroup,
                                                AdvertisementViewGroup.CoverImageGroup coverImageGroup) {

        return validateAdvertisement(textGroup, categoryGroup, imageGroup, coverImageGroup, null);
    }

    public static boolean validateAdvertisement(AdvertisementViewGroup.TextGroup textGroup,
                                                AdvertisementViewGroup.CategoryGroup categoryGroup,
                                                AdvertisementViewGroup.ImageGroup imageGroup,
                                                AdvertisementViewGroup.CoverImageGroup coverImageGroup,
                                                AdvertisementViewGroup.DateGroup dateGroup) {

        boolean validTitle = false;
        String title = textGroup.editTextTitle.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            textGroup.editTextTitle.setError(App.getContext().getString(R.string.error_input_required));
        } else {
            validTitle = true;
        }

        boolean validDescription = false;
        String description = textGroup.editTextDescription.getText().toString().trim();
        if (TextUtils.isEmpty(description)) {
            textGroup.editTextDescription.setError(App.getContext().getString(R.string.error_input_required));
        } else {
            validDescription = true;
        }

        boolean validCategories = false;
        for (boolean isSelected : categoryGroup.selectionCategories) {
            if (isSelected) {
                validCategories = true;
                break;
            }
        }
        if (!validCategories) {
            categoryGroup.textViewHintAddCategory.setTextColor(App.getContext().getColor(R.color.red));
        }

        boolean validImages = false;
        if ((imageGroup.existedImagesUri != null && imageGroup.existedImagesUri.size() == 0 && imageGroup.addedImagesUri.size() == 0)
                || (imageGroup.existedImagesUri == null && imageGroup.addedImagesUri.size() == 0)) {
            imageGroup.textViewHintAddImage.setTextColor(App.getContext().getColor(R.color.red));
        } else if (coverImageGroup.coverImage == null) {
            coverImageGroup.textViewHintAddCover.setTextColor(App.getContext().getColor(R.color.red));
        } else {
            validImages = true;
        }

        boolean validDate = false;
        if (dateGroup != null) {
            String date = dateGroup.textViewDateToPost.getText().toString();
            if (date.equals(App.getContext().getString(R.string.hint_select_date))) {
                dateGroup.textViewDateToPost.setTextColor(App.getContext().getColor(R.color.red));
            } else {
                validDate = true;
            }
        } else {
            validDate = true;
        }

        return validTitle && validDescription && validCategories && validImages && validDate;
    }

    public static boolean validateChangeEmail(EditText editTextEmail, EditText editTextPassword) {

        boolean validEmail = false;
        String email = editTextEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError(App.getContext().getString(R.string.error_input_required));
            editTextEmail.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(App.getContext().getString(R.string.error_format_email));
            editTextEmail.requestFocus();
        } else {
            validEmail = true;
        }

        boolean validPassword = false;
        String password = editTextPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError(App.getContext().getString(R.string.error_input_required));
            editTextPassword.requestFocus();
        } else {
            validPassword = true;
        }

        return validEmail && validPassword;
    }

    public static boolean validateResetPassword(EditText editTextPassword,
                                                EditText editTextPasswordNew) {
        boolean validPassword = false;
        String password = editTextPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError(App.getContext().getString(R.string.error_input_required));
            editTextPassword.requestFocus();
        } else {
            validPassword = true;
        }

        boolean validPasswordNew = false;
        String passwordNew = editTextPasswordNew.getText().toString();
        if (TextUtils.isEmpty(passwordNew)) {
            editTextPasswordNew.setError(App.getContext()
                    .getString(R.string.error_input_required));
            editTextPasswordNew.requestFocus();
        } else if (passwordNew.length() < 8) {
            editTextPasswordNew.setError(App.getContext()
                    .getString(R.string.error_minimum_password));
            editTextPasswordNew.requestFocus();
        } else if (password.equals(passwordNew)) {
            editTextPasswordNew.setError(App.getContext()
                    .getString(R.string.error_duplicate_password));
            editTextPasswordNew.requestFocus();
        } else {
            validPasswordNew = true;
        }

        return validPassword && validPasswordNew;
    }

    public static boolean validateProfile(TextInputEditText editTextStoreName) {

        boolean validStoreName = false;
        String storeName = editTextStoreName.getText().toString().trim();
        if (TextUtils.isEmpty(storeName)) {
            editTextStoreName.setError(App.getContext().getString(R.string.error_input_required));
        } else {
            validStoreName = true;
        }

        return validStoreName;
    }
}
