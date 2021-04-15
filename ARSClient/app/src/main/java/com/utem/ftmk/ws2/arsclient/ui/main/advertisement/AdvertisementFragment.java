package com.utem.ftmk.ws2.arsclient.ui.main.advertisement;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.assistant.DialogAssistant;
import com.utem.ftmk.ws2.arsclient.model.client.Client;
import com.utem.ftmk.ws2.arsclient.model.client.ClientManager;

public class AdvertisementFragment extends Fragment implements View.OnClickListener {

    private AdvertisementViewModel mAdvertisementViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mAdvertisementViewModel = new ViewModelProvider(requireActivity())
                .get(AdvertisementViewModel.class);
        View root = inflater.inflate(R.layout.fragment_advertisement, container, false);

        ViewPager viewPager = root.findViewById(R.id.view_pager);
        viewPager.setAdapter(new AdvertisementPagerAdapter(
                getChildFragmentManager(), mAdvertisementViewModel.getTabFragments()));

        TabLayout tabLayout = root.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fabUpload = root.findViewById(R.id.fab_upload);
        fabUpload.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {

        Client.Subscription subscription = mAdvertisementViewModel.getClient().getSubscription();
        if (subscription == null) {
            DialogAssistant.showMessageDialog(requireActivity(),
                    R.string.text_preview_subscription_warning_no_subscription);
        } else if (!subscription.getStatus()) {
            DialogAssistant.showMessageDialog(requireActivity(),
                    R.string.text_preview_subscription_warning_expired);
        }else {
            requireActivity().startActivity(new Intent(getActivity(),
                    AdvertisementUploadActivity.class));
        }
    }
}