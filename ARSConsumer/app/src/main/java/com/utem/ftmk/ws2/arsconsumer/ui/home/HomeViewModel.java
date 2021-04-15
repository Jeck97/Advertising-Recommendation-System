package com.utem.ftmk.ws2.arsconsumer.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utem.ftmk.ws2.arsconsumer.model.Advertisement;
import com.utem.ftmk.ws2.arsconsumer.model.Client;
import com.utem.ftmk.ws2.arsconsumer.utils.LocationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HomeViewModel extends ViewModel {

    private static final String TAG = HomeViewModel.class.getSimpleName();
    private static final int SORT_DATE = 0;
    private static final int SORT_DISTANCE = 1;
    private static final int SORT_LIKE = 2;

    private final MutableLiveData<Client.Location> location;
    private final MutableLiveData<List<Advertisement>> advertisements;
    private final MutableLiveData<Integer> selectedSort;
    private final MutableLiveData<boolean[]> selectedFilter;
    private final MutableLiveData<String> searchKey;

    private final MutableLiveData<Void> initiator;

    private List<Advertisement> allAdvertisements;


    public HomeViewModel() {
        location = new MutableLiveData<>();
        advertisements = new MutableLiveData<>();
        selectedSort = new MutableLiveData<>();
        selectedFilter = new MutableLiveData<>();
        searchKey = new MutableLiveData<>();
        initiator = new MutableLiveData<>();
        initSelection();
    }

    public LiveData<Void> getInitiator() {
        return initiator;
    }

    public LiveData<Client.Location> getLiveLocation() {
        return location;
    }

    public void setLocation(Client.Location location) {
        this.location.setValue(location);
    }

    public LiveData<List<Advertisement>> getLiveAdvertisement() {
        return advertisements;
    }

    public void updateAdvertisements() {
        advertisements.setValue(allAdvertisements);
    }

    public void updateAdvertisements(Client.Location location) {
        for (Advertisement advertisement : allAdvertisements) {
            advertisement.setDistance(LocationUtil.getDistanceBetween(location,
                    advertisement.getMerchantLocation()));
        }
        advertisements.setValue(allAdvertisements);
    }

    public void updateAdvertisements(String searchKey) {
        if (searchKey.isEmpty()) {
            advertisements.setValue(allAdvertisements);
        } else {
            String key = searchKey.toLowerCase();
            advertisements.setValue(
                    allAdvertisements.stream().filter(advertisement ->
                            advertisement.getTitle().toLowerCase().contains(key) ||
                                    advertisement.getMerchantName().toLowerCase().contains(key))
                            .collect(Collectors.toList()));
        }
    }

    public void updateAdvertisements(Integer selectedSort) {
        switch (selectedSort) {
            case SORT_DISTANCE:
                advertisements.setValue(allAdvertisements.stream()
                        .sorted(Comparator.comparingDouble(Advertisement::getDistance))
                        .collect(Collectors.toList()));
                break;
            case SORT_DATE:
                advertisements.setValue(allAdvertisements.stream()
                        .sorted(Comparator.comparingLong(Advertisement::getPostedDate).reversed())
                        .collect(Collectors.toList()));
                break;
            case SORT_LIKE:
                List<Advertisement> sortedAdvertisements = allAdvertisements.stream()
                        .sorted(Comparator.comparingInt(ads -> ads.getLikes().size()))
                        .collect(Collectors.toList());
                Collections.reverse(sortedAdvertisements);
                advertisements.setValue(sortedAdvertisements);
                break;
        }
    }

    public void updateAdvertisements(boolean[] selectedFilter) {
        List<Integer> selectedCategories = new ArrayList<>();
        for (int index = 0; index < selectedFilter.length; index++) {
            if (selectedFilter[index]) {
                selectedCategories.add(index);
            }
        }

        if (selectedCategories.size() == 0) {
            advertisements.setValue(allAdvertisements);
        } else {
            advertisements.setValue(allAdvertisements.stream().filter(advertisement ->
                    new ArrayList<>(advertisement.getCategories()).removeAll(selectedCategories))
                    .collect(Collectors.toList()));
        }
    }

    public LiveData<Integer> getLiveSelectedSort() {
        return selectedSort;
    }

    public int getSelectedSort() {
        return selectedSort.getValue();
    }

    public void setSelectedSort(int selectedSort) {
        this.selectedSort.setValue(selectedSort);
    }

    public LiveData<boolean[]> getLiveSelectedFilter() {
        return selectedFilter;
    }

    public boolean[] getSelectedFilter() {
        return selectedFilter.getValue();
    }

    public void setSelectedFilter(boolean[] selectedFilter) {
        this.selectedFilter.setValue(selectedFilter);
    }

    public LiveData<String> getLiveSearchKey() {
        return searchKey;
    }

    public String getSearchKey() {
        return searchKey.getValue();
    }

    public void setSearchKey(String searchKey) {
        this.searchKey.setValue(searchKey);
    }

    public void init(Client.Location initialLocation) {
        FirebaseDatabase.getInstance()
                .getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot advertisementsSnapshot
                        = snapshot.child(Advertisement.FIREBASE_IDENTIFIER);
                DataSnapshot clientsSnapshot
                        = snapshot.child(Client.FIREBASE_IDENTIFIER);
                allAdvertisements = new ArrayList<>();
                for (DataSnapshot advertisementSnapshot : advertisementsSnapshot.getChildren()) {
                    Advertisement advertisement = advertisementSnapshot
                            .getValue(Advertisement.class);
                    if (advertisement.getStatus().equals(Advertisement.STATUS_ACTIVATED)) {
                        Client client = clientsSnapshot.child(advertisement.getOwnerId())
                                .getValue(Client.class);
                        advertisement.setMerchantName(client.getStoreName());
                        advertisement.setMerchantLocation(client.getLocation());
                        allAdvertisements.add(advertisement);
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

    private void initSelection() {
        selectedSort.setValue(0);
        selectedFilter.setValue(new boolean[20]);
        searchKey.setValue("");
    }

}