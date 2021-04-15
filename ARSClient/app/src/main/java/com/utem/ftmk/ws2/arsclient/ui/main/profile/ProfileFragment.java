package com.utem.ftmk.ws2.arsclient.ui.main.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.assistant.DialogAssistant;
import com.utem.ftmk.ws2.arsclient.model.client.Client;
import com.utem.ftmk.ws2.arsclient.model.client.ClientManager;
import com.utem.ftmk.ws2.arsclient.ui.authentication.LoginActivity;

public class ProfileFragment extends Fragment {

    public static final String EXTRA_CLIENT = "extra_client";

    private ProfileViewModel mProfileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mProfileViewModel =
                new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        ImageView imageViewLogo = root.findViewById(R.id.imageView_profile_logo);
        TextView textViewStoreName = root.findViewById(R.id.textView_profile_store_name);
        textViewStoreName.setOnClickListener(mOnStoreNameClickListener);
        TextView textViewStorePhoto = root.findViewById(R.id.textView_profile_store_photo);
        textViewStorePhoto.setOnClickListener(mOnStorePhotoClickListener);
        TextView textViewChangeEmail = root.findViewById(R.id.textView_profile_change_email);
        textViewChangeEmail.setOnClickListener(mOnChangeEmailClickListener);
        TextView textViewResetPassword = root.findViewById(R.id.textView_profile_reset_password);
        textViewResetPassword.setOnClickListener(mOnResetPasswordClickListener);
        ConstraintLayout layoutAccountVerification
                = root.findViewById(R.id.layout_profile_account_verification);
        layoutAccountVerification.setOnClickListener(mOnAccountVerificationClickListener);
        TextView textViewAccountVerificationStatus
                = root.findViewById(R.id.textView_profile_account_verification_status);
        TextView textViewLogout = root.findViewById(R.id.textView_profile_logout);
        textViewLogout.setOnClickListener(mOnLogoutClickListener);

        mProfileViewModel.setIsVerified(ClientManager.isClientVerified());
        mProfileViewModel.getLiveClient().observe(getViewLifecycleOwner(), client -> {
            if (client.getLogo() != null) {
                Glide.with(requireActivity()).load(Uri.parse(client.getLogo())).into(imageViewLogo);
            }
            textViewStoreName.setText(client.getStoreName());
        });
        mProfileViewModel.getLiveIsVerified().observe(getViewLifecycleOwner(),
                isVerified -> textViewAccountVerificationStatus.setText(isVerified ?
                        R.string.text_preview_profile_status_completed :
                        R.string.text_preview_profile_status_unverified));

        return root;
    }

    @Override
    public void onResume() {
        if (!ClientManager.isClientVerified()) {
            ClientManager.reloadClient(task -> {
                mProfileViewModel.setIsVerified(ClientManager.isClientVerified());
            });
        }
        super.onResume();
    }

    private final View.OnClickListener mOnStoreNameClickListener = v -> {
        Intent intent = new Intent(requireActivity(), ProfileEditActivity.class);
        intent.putExtra(EXTRA_CLIENT, mProfileViewModel.getClient());
        startActivity(intent);
    };

    private final View.OnClickListener mOnStorePhotoClickListener = v -> {
        Intent intent = new Intent(requireActivity(), ProfilePhotoActivity.class);
        intent.putExtra(EXTRA_CLIENT, mProfileViewModel.getClient());
        requireActivity().startActivity(intent);
    };

    private final View.OnClickListener mOnChangeEmailClickListener = v ->
            DialogAssistant.showChangeEmailDialog(requireActivity(), mProfileViewModel.getClient());

    private final View.OnClickListener mOnResetPasswordClickListener = v ->
            DialogAssistant.showResetPasswordDialog(requireActivity());

    private final View.OnClickListener mOnAccountVerificationClickListener = v -> {
        if (ClientManager.isClientVerified()) {
            DialogAssistant.showMessageDialog(requireActivity(),
                    R.string.dialog_message_account_verified);
        } else {
            DialogAssistant.showProgressDialog(requireActivity(),
                    R.string.progress_send_validation_email);
            ClientManager.sendClientEmailVerification(task -> {
                DialogAssistant.dismissProgressDialog();
                DialogAssistant.showEmailValidationDialog(requireActivity());
            });
        }
    };

    private final View.OnClickListener mOnLogoutClickListener = v ->
            DialogAssistant.showConfirmLogoutDialog(requireActivity());
}