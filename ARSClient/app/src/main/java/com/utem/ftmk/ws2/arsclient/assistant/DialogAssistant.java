package com.utem.ftmk.ws2.arsclient.assistant;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.google.android.material.chip.Chip;
import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.model.advertisement.Advertisement;
import com.utem.ftmk.ws2.arsclient.model.client.Client;
import com.utem.ftmk.ws2.arsclient.model.client.ClientManager;
import com.utem.ftmk.ws2.arsclient.ui.authentication.LoginActivity;
import com.utem.ftmk.ws2.arsclient.ui.main.advertisement.AdvertisementEditActivity;
import com.utem.ftmk.ws2.arsclient.ui.main.statistic.StatisticViewModel;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.Calendar;

public class DialogAssistant {

    private static ProgressDialog mProgressDialog;

    public static CircularProgressDrawable getCircularProgress(@NonNull Context context) {
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStyle(CircularProgressDrawable.LARGE);
        circularProgressDrawable.setColorSchemeColors(context.getColor(R.color.teal_200));
        circularProgressDrawable.start();

        return circularProgressDrawable;
    }

    public static void showProgressDialog(@NonNull Context context, @StringRes int messageId) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(context.getString(messageId));
        mProgressDialog.show();
    }

    public static void showProgressDialog(@NonNull Context context, String message) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    public static void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public static void showEmailValidationDialog(@NonNull Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_title_verification)
                .setMessage(R.string.dialog_message_verification)
                .setPositiveButton(R.string.dialog_button_verify, (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Intent chooser = Intent.createChooser(intent, null);
                    context.startActivity(chooser);
                })
                .setNegativeButton(R.string.dialog_button_not_now, (dialog, which) ->
                        dialog.dismiss())
                .show();
    }

    public static void showForgotPasswordDialog(@NonNull Context context) {
        EditText editTextEmail = new EditText(context);
        editTextEmail.setHint(R.string.hint_email_address);
        editTextEmail.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(params);
        layout.setPadding(50, 50, 50, 100);
        layout.addView(editTextEmail, params);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_title_reset_password)
                .setView(layout)
                .setPositiveButton(R.string.dialog_button_send, null)
                .setNegativeButton(R.string.dialog_button_cancel, null)
                .show();

        Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btnPositive.setOnClickListener(v -> {
            boolean validEmail = InputValidator.validateForgotPassword(editTextEmail);
            if (validEmail) {
                ClientManager.sendClientPasswordResetEmail(
                        editTextEmail.getText().toString().trim(), task -> {
                            dialog.dismiss();
                            Toast.makeText(context,
                                    context.getString(R.string.toast_message_success_reset_password),
                                    Toast.LENGTH_LONG).show();
                        });
            }
        });
    }

    public static void showRegisterSuccessfulDialog(@NonNull Context context) {
        new AlertDialog.Builder(context)
                .setMessage(R.string.dialog_message_register_successful)
                .setPositiveButton(R.string.dialog_button_ok, (dialog, which) -> {
                    Activity activity = (Activity) context;
                    activity.setResult(Activity.RESULT_OK);
                    activity.finish();
                })
                .setCancelable(false)
                .show();
    }

    public static void showCategoriesDialog(@NonNull Context context, AdvertisementViewGroup.CategoryGroup categoryGroup) {

        new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_title_category)
                .setMultiChoiceItems(categoryGroup.allCategories, categoryGroup.selectionCategories,
                        (dialog, which, isChecked) ->
                                categoryGroup.selectionCategories[which] = isChecked)
                .setPositiveButton(R.string.dialog_button_ok, (dialog, which) -> {
                    categoryGroup.containerCategories.removeView(categoryGroup.chipAddCategory);
                    for (int index = 0; index < categoryGroup.allCategoriesLength; index++) {
                        if (categoryGroup.selectionCategories[index]
                                && categoryGroup.addedChips[index] == null) {
                            final int INDEX = index;
                            Chip chip = new Chip(context);
                            chip.setText(categoryGroup.allCategories[index]);
                            chip.setCloseIconVisible(true);
                            chip.setOnCloseIconClickListener(v -> {
                                categoryGroup.selectionCategories[INDEX] = false;
                                categoryGroup.addedChips[INDEX] = null;
                                categoryGroup.containerCategories.removeView(chip);
                            });
                            categoryGroup.addedChips[index] = chip;
                            categoryGroup.containerCategories.addView(chip);
                            categoryGroup.textViewHintAddCategory.
                                    setTextColor(context.getColor(R.color.hint));
                        } else if (!categoryGroup.selectionCategories[index]
                                && categoryGroup.addedChips[index] != null) {
                            categoryGroup.containerCategories
                                    .removeView(categoryGroup.addedChips[index]);
                            categoryGroup.addedChips[index] = null;
                        }
                    }
                    categoryGroup.containerCategories.addView(categoryGroup.chipAddCategory);
                })
                .setNegativeButton(R.string.dialog_button_cancel, (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    public static void showDateToPostPickerDialog(@NonNull Context context, AdvertisementViewGroup.DateGroup dateGroup) {

        DatePickerDialog dialog = new DatePickerDialog(context,
                (view, year, month, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth);

                    long date = calendar.getTimeInMillis();
                    dateGroup.dateToPost = date;
                    dateGroup.textViewDateToPost.setText(DateAssistant.formatDefault(date));
                    dateGroup.textViewDateToPost.setTextColor(context.getColor(R.color.hint));
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 3)
        );
        long threeDays = 259200000;
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() + threeDays);
        dialog.show();
    }

    public static void showUploadAdvertisementSuccessfulDialog(@NonNull Context context) {
        new AlertDialog.Builder(context)
                .setMessage(R.string.dialog_message_upload_advertisement_successful)
                .setPositiveButton(R.string.dialog_button_ok, (dialog, which) -> {
                    Activity activity = (Activity) context;
                    activity.finish();
                })
                .setCancelable(false)
                .show();
    }

    public static void showUpdateAdvertisementSuccessfulDialog(@NonNull Context context,
                                                               Advertisement advertisement) {
        new AlertDialog.Builder(context)
                .setMessage(R.string.dialog_message_update_advertisement_successful)
                .setPositiveButton(R.string.dialog_button_ok, (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.putExtra(AdvertisementEditActivity.EXTRA_EDITED_ADVERTISEMENT,
                            advertisement);

                    Activity activity = (Activity) context;
                    activity.setResult(Activity.RESULT_OK, intent);
                    activity.finish();
                })
                .setCancelable(false)
                .show();
    }

    public static void showFilterDatePickerDialog(@NonNull Context context,
                                                  StatisticViewModel viewModel, int type) {
        Calendar calendar = DateAssistant.toCalendar(viewModel.getDate(type));
        DatePickerDialog dialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
            Calendar calendarResult = Calendar.getInstance();
            calendarResult.set(year, month, dayOfMonth);
            viewModel.setDate(calendarResult.getTimeInMillis(), type);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        switch (type) {
            case StatisticViewModel.DATE_DAY:
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                break;
            case StatisticViewModel.DATE_PERIOD_START:
                dialog.getDatePicker().setMaxDate(DateAssistant.addDays(
                        viewModel.getDate(StatisticViewModel.DATE_PERIOD_END), -1));
                break;
            case StatisticViewModel.DATE_PERIOD_END:
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.getDatePicker().setMinDate(DateAssistant.addDays(
                        viewModel.getDate(StatisticViewModel.DATE_PERIOD_START), 1));
                break;
        }
        dialog.show();
    }

    public static void showFilterMonthPickerDialog(@NonNull Context context,
                                                   StatisticViewModel viewModel, int type) {
        Calendar calendar = DateAssistant.toCalendar(viewModel.getDate(type));
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(context,
                (selectedMonth, selectedYear) -> {
                    Calendar calendarResult = Calendar.getInstance();
                    calendarResult.set(selectedYear, selectedMonth, 1);
                    viewModel.setDate(calendarResult.getTimeInMillis(), type);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
        builder.setMaxYear(Calendar.getInstance().get(Calendar.YEAR));
        if (type == StatisticViewModel.DATE_YEAR) {
            builder.showYearOnly();
        }
        builder.build().show();
    }

    public static void showMessageDialog(@NonNull Context context, @StringRes int message) {
        new AlertDialog.Builder(context).setMessage(message)
                .setPositiveButton(R.string.dialog_button_ok, null).show();
    }

    public static void showMessageDialog(@NonNull Context context, String message) {
        new AlertDialog.Builder(context).setMessage(message)
                .setPositiveButton(R.string.dialog_button_ok, null).show();
    }

    public static void showMessageDialog(@NonNull Context context, @StringRes int message,
                                         DialogCallback listener) {
        new AlertDialog.Builder(context).setMessage(message)
                .setPositiveButton(R.string.dialog_button_ok,
                        (dialog, which) -> listener.onComplete()).show();
    }

    public static void showMessageDialog(@NonNull Context context, String message,
                                         DialogCallback listener) {
        new AlertDialog.Builder(context).setMessage(message)
                .setPositiveButton(R.string.dialog_button_ok,
                        (dialog, which) -> listener.onComplete()).show();
    }


    public static void showChangeEmailDialog(@NonNull Context context, Client client) {

        EditText editTextEmail = new EditText(context);
        editTextEmail.setHint(R.string.hint_email_address_new);
        editTextEmail.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        EditText editTextPassword = new EditText(context);
        editTextPassword.setHint(R.string.hint_password);
        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(params);
        layout.setPadding(50, 50, 50, 100);
        layout.addView(editTextEmail, params);
        layout.addView(editTextPassword, params);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_title_reset_email)
                .setView(layout)
                .setPositiveButton(R.string.dialog_button_submit, null)
                .setNegativeButton(R.string.dialog_button_cancel, null)
                .show();

        Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btnPositive.setOnClickListener(v -> {
            boolean valid = InputValidator.validateChangeEmail(editTextEmail, editTextPassword);
            if (valid) {
                DialogAssistant.showProgressDialog(context, R.string.progress_update_email);
                String oldEmail = ClientManager.getClientEmail();
                String newEmail = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString();
                ClientManager.reAuthenticateClient(oldEmail, password, task -> {
                    if (task.isSuccessful()) {
                        ClientManager.updateClientEmail(newEmail, task1 -> {
                            if (task1.isSuccessful()) {
                                client.setEmail(newEmail);
                                ClientManager.updateClient(client, task2 -> {
                                    DialogAssistant.dismissProgressDialog();
                                    DialogAssistant.showMessageDialog(context,
                                            R.string.dialog_message_update_email_successful);
                                });
                            } else {
                                DialogAssistant.dismissProgressDialog();
                                DialogAssistant.showMessageDialog(context,
                                        task.getException().getMessage());
                            }
                        });
                    } else {
                        DialogAssistant.dismissProgressDialog();
                        DialogAssistant.showMessageDialog(context,
                                task.getException().getMessage());
                    }
                });
                dialog.dismiss();
            }
        });
    }

    public static void showResetPasswordDialog(@NonNull Context context) {
        EditText editTextPassword = new EditText(context);
        editTextPassword.setHint(R.string.hint_password_current);
        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);

        EditText editTextPasswordNew = new EditText(context);
        editTextPasswordNew.setHint(R.string.hint_password_new);
        editTextPasswordNew.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(params);
        layout.setPadding(50, 50, 50, 100);
        layout.addView(editTextPassword, params);
        layout.addView(editTextPasswordNew, params);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_title_reset_password)
                .setView(layout)
                .setPositiveButton(R.string.dialog_button_submit, null)
                .setNegativeButton(R.string.dialog_button_cancel, null)
                .show();

        Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btnPositive.setOnClickListener(v -> {
            boolean valid = InputValidator
                    .validateResetPassword(editTextPassword, editTextPasswordNew);
            if (valid) {
                DialogAssistant.showProgressDialog(context, R.string.progress_update_password);
                String email = ClientManager.getClientEmail();
                String password = editTextPassword.getText().toString();
                String passwordNew = editTextPasswordNew.getText().toString();

                ClientManager.reAuthenticateClient(email, password, task -> {
                    if (task.isSuccessful()) {
                        ClientManager.updateClientPassword(passwordNew, task1 -> {
                            if (task1.isSuccessful()) {
                                DialogAssistant.dismissProgressDialog();
                                DialogAssistant.showMessageDialog(context,
                                        R.string.dialog_message_update_password_successful);
                            } else {
                                DialogAssistant.dismissProgressDialog();
                                DialogAssistant.showMessageDialog(context,
                                        task.getException().getMessage());
                            }
                        });
                    } else {
                        DialogAssistant.dismissProgressDialog();
                        DialogAssistant.showMessageDialog(context,
                                task.getException().getMessage());
                    }
                });
                dialog.dismiss();
            }
        });
    }

    public static void showConfirmLogoutDialog(@NonNull Context context) {
        new android.app.AlertDialog.Builder(context)
                .setMessage(R.string.dialog_message_logout_confirmation)
                .setPositiveButton(R.string.dialog_button_yes, (dialog, which) -> {
                    ClientManager.signOutClient();
                    context.startActivity(new Intent(context, LoginActivity.class));
                    ((Activity) context).finish();
                })
                .setNegativeButton(R.string.dialog_button_no, null)
                .show();
    }

    public static void showConfirmDeletePhotoDialog(@NonNull Context context,
                                                    DialogCallback listener) {
        new android.app.AlertDialog.Builder(context)
                .setMessage(R.string.dialog_message_photo_delete_confirmation)
                .setPositiveButton(R.string.dialog_button_yes, (dialog, which) ->
                        listener.onComplete())
                .setNegativeButton(R.string.dialog_button_no, null)
                .show();
    }

    public static void showConfirmDeleteAdvertisementDialog(@NonNull Context context,
                                                            DialogCallback listener) {
        new android.app.AlertDialog.Builder(context)
                .setMessage(R.string.dialog_message_advertisement_delete_confirmation)
                .setPositiveButton(R.string.dialog_button_yes, (dialog, which) ->
                        listener.onComplete())
                .setNegativeButton(R.string.dialog_button_no, null)
                .show();
    }


    public interface DialogCallback {
        void onComplete();
    }

}
