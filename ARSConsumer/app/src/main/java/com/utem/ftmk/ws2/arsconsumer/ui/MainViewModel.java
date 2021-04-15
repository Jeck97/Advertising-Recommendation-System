package com.utem.ftmk.ws2.arsconsumer.ui;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utem.ftmk.ws2.arsconsumer.model.Client;
import com.utem.ftmk.ws2.arsconsumer.model.Consumer;
import com.utem.ftmk.ws2.arsconsumer.utils.LocationUtil;

import java.util.Objects;

public class MainViewModel extends ViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private final MutableLiveData<Consumer> consumer;
    private final MutableLiveData<Client.Location> location;

    public MainViewModel() {
        consumer = new MutableLiveData<>();
        location = new MutableLiveData<>();
        init();
    }

    public LiveData<Consumer> getLiveConsumer() {
        return consumer;
    }

    public Consumer getConsumer() {
        return consumer.getValue();
    }

    public void setConsumer(Consumer consumer) {
        this.consumer.setValue(consumer);
    }

    public LiveData<Client.Location> getLiveLocation() {
        return location;
    }

    public Client.Location getLocation() {
        return location.getValue();
    }

    public void setLocation(Client.Location location) {
        this.location.setValue(location);
    }

    private void init() {
        String consumerId = Objects.requireNonNull(FirebaseAuth.getInstance().getUid());
        FirebaseDatabase.getInstance().getReference().child(Consumer.FIREBASE_IDENTIFIER)
                .child(consumerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setConsumer(snapshot.getValue(Consumer.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });
    }

}
