package com.utem.ftmk.ws2.arsclient.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.utem.ftmk.ws2.arsclient.model.advertisement.Advertisement;
import com.utem.ftmk.ws2.arsclient.model.client.Client;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<Client> mClient;
    private final MutableLiveData<List<Advertisement>> mAdvertisements;

    public HomeViewModel() {
        mClient = new MutableLiveData<>();
        mAdvertisements = new MutableLiveData<>();
    }

    public LiveData<Client> getLiveClient() {
        return mClient;
    }

    public void setClient(Client client) {
        mClient.setValue(client);
    }

    public LiveData<List<Advertisement>> getLiveAdvertisements() {
        return mAdvertisements;
    }

    public void setAdvertisements(List<Advertisement> advertisements) {
        mAdvertisements.setValue(advertisements);
    }

}
