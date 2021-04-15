package com.utem.ftmk.ws2.arsconsumer.ui.history;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utem.ftmk.ws2.arsconsumer.model.Advertisement;
import com.utem.ftmk.ws2.arsconsumer.model.Client;
import com.utem.ftmk.ws2.arsconsumer.model.Consumer;
import com.utem.ftmk.ws2.arsconsumer.ui.home.HomeViewModel;
import com.utem.ftmk.ws2.arsconsumer.utils.LocationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryViewModel extends ViewModel {

    private static final String TAG = HomeViewModel.class.getSimpleName();

    private List<Advertisement> allAdvertisements;

    private final MutableLiveData<Consumer> consumer;
    private final MutableLiveData<List<Advertisement>> advertisements;
    private final MutableLiveData<Client.Location> location;

    private final MutableLiveData<Void> initiator;

    public HistoryViewModel() {
        consumer = new MutableLiveData<>();
        advertisements = new MutableLiveData<>();
        location = new MutableLiveData<>();
        initiator = new MutableLiveData<>();
    }

    public LiveData<Void> getInitiator() {
        return initiator;
    }

    public LiveData<Consumer> getLiveConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer.setValue(consumer);
    }

    public LiveData<Client.Location> getLiveLocation() {
        return location;
    }

    public void setLocation(Client.Location location) {
        this.location.setValue(location);
    }

    public LiveData<List<Advertisement>> getLiveAdvertisements() {
        return advertisements;
    }

    public void setAdvertisements(List<Advertisement> advertisement) {
        this.advertisements.setValue(advertisement);
    }

    public void updateAdvertisements() {
        this.advertisements.setValue(allAdvertisements);
    }

    public void updateAdvertisements(Client.Location location) {
        for (Advertisement advertisement : allAdvertisements) {
            advertisement.setDistance(LocationUtil.getDistanceBetween(location,
                    advertisement.getMerchantLocation()));
        }
        setAdvertisements(allAdvertisements);
    }

    public void updateAdvertisements(Consumer consumer) {
        init(location.getValue(), consumer);
    }

    public void init(Client.Location initialLocation) {
        FirebaseDatabase.getInstance().getReference().child(Consumer.FIREBASE_IDENTIFIER)
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        init(initialLocation, snapshot.getValue(Consumer.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, error.getMessage());
                    }
                });
    }

    private void init(Client.Location initialLocation, Consumer initialConsumer) {
        FirebaseDatabase.getInstance()
                .getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot advertisementsSnapshot
                        = snapshot.child(Advertisement.FIREBASE_IDENTIFIER);
                DataSnapshot clientsSnapshot
                        = snapshot.child(Client.FIREBASE_IDENTIFIER);

                allAdvertisements = new ArrayList<>();
                Consumer consumerTemp = consumer.getValue() != null ?
                        consumer.getValue() : initialConsumer;
                List<String> ids = consumerTemp.getLikedAdvertisements();

                for (String id : ids) {
                    for (DataSnapshot advertisementSnapshot :
                            advertisementsSnapshot.getChildren()) {
                        Advertisement advertisement = advertisementSnapshot
                                .getValue(Advertisement.class);
                        if (id.equals(advertisement.getId())) {
                            Client client = clientsSnapshot.child(advertisement
                                    .getOwnerId()).getValue(Client.class);
                            advertisement.setMerchantName(client.getStoreName());
                            advertisement.setMerchantLocation(client.getLocation());
                            allAdvertisements.add(advertisement);
                            break;
                        }
                    }
                }
                Collections.reverse(allAdvertisements);
                updateAdvertisements(location.getValue() != null ?
                        location.getValue() : initialLocation);
                initiator.setValue(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });
    }


}