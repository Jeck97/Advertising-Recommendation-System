package com.utem.ftmk.ws2.arsclient.ui.main.advertisement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.model.advertisement.Advertisement;
import com.utem.ftmk.ws2.arsclient.model.advertisement.AdvertisementManager;
import com.utem.ftmk.ws2.arsclient.model.client.Client;
import com.utem.ftmk.ws2.arsclient.model.client.ClientManager;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * A fragment representing a list of Items.
 */
public class AdvertisementTabFragment extends Fragment {

    private final String mStatus;
    private AdvertisementRecyclerViewAdapter mAdapter;

    public AdvertisementTabFragment(String status) {
        this.mStatus = status;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_advertisement_tab, container, false);

        mAdapter = new AdvertisementRecyclerViewAdapter();
        RecyclerView mRecyclerView = view.findViewById(R.id.recyclerView_advertisements);
        mRecyclerView.setAdapter(mAdapter);

        AdvertisementViewModel advertisementViewModel = new ViewModelProvider(requireActivity())
                .get(AdvertisementViewModel.class);
        advertisementViewModel.getLiveAdvertisements().observe(getViewLifecycleOwner(), advertisements -> {
            List<Advertisement> filteredAdvertisement = mStatus == null ? advertisements :
                    advertisements.stream().filter(advertisement ->
                            advertisement.getStatus().equals(mStatus)).collect(toList());
            mAdapter.updateAdvertisement(filteredAdvertisement);
            mAdapter.notifyDataSetChanged();
        });

        return view;
    }

}