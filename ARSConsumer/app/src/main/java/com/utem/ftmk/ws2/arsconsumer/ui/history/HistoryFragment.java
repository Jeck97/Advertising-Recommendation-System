package com.utem.ftmk.ws2.arsconsumer.ui.history;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utem.ftmk.ws2.arsconsumer.AdvertisementRecyclerViewAdapter;
import com.utem.ftmk.ws2.arsconsumer.R;
import com.utem.ftmk.ws2.arsconsumer.model.Advertisement;
import com.utem.ftmk.ws2.arsconsumer.model.Client;
import com.utem.ftmk.ws2.arsconsumer.model.Consumer;

import java.util.List;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        historyViewModel = new ViewModelProvider(requireActivity())
                .get(HistoryViewModel.class);

        View root = inflater.inflate(R.layout.fragment_history, container, false);

        ProgressBar pb = root.findViewById(R.id.pb_history_loading);
        AdvertisementRecyclerViewAdapter adapter = new AdvertisementRecyclerViewAdapter(
                (AppCompatActivity) requireActivity());
        RecyclerView rv = root.findViewById(R.id.rv_history);
        rv.setAdapter(adapter);

        historyViewModel.getInitiator().observe(getViewLifecycleOwner(), new Observer<Void>() {
            @Override
            public void onChanged(Void aVoid) {
                historyViewModel.getLiveAdvertisements().observe(getViewLifecycleOwner(),
                        adapter::updateList);
                historyViewModel.getLiveLocation().observe(getViewLifecycleOwner(),
                        location -> historyViewModel.updateAdvertisements(location));
                historyViewModel.getLiveConsumer().observe(getViewLifecycleOwner(),
                        consumer -> historyViewModel.updateAdvertisements(consumer));
                pb.setVisibility(View.GONE);
                historyViewModel.getInitiator().removeObserver(this);
            }
        });

        return root;
    }

}