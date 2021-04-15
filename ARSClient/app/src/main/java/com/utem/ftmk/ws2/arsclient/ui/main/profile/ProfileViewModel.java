package com.utem.ftmk.ws2.arsclient.ui.main.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.utem.ftmk.ws2.arsclient.model.client.Client;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<Client> mClient;
    private final MutableLiveData<Boolean> mIsVerified;

    public ProfileViewModel() {
        mClient = new MutableLiveData<>();
        mIsVerified = new MutableLiveData<>();
    }

    public LiveData<Client> getLiveClient() {
        return mClient;
    }

    public Client getClient() {
        return mClient.getValue();
    }

    public void setClient(Client client) {
        mClient.setValue(client);
    }

    public LiveData<Boolean> getLiveIsVerified() {
        return mIsVerified;
    }

    public void setIsVerified(boolean isVerified) {
        mIsVerified.setValue(isVerified);
    }
}