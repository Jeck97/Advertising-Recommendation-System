package com.utem.ftmk.ws2.arsclient.ui.main.subscription;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.utem.ftmk.ws2.arsclient.model.client.Client;
import com.utem.ftmk.ws2.arsclient.model.client.ClientManager;

public class SubscriptionViewModel extends ViewModel {

    private final MutableLiveData<Client> mClient;

    public SubscriptionViewModel() {
        mClient = new MutableLiveData<>();
    }

    public LiveData<Client> getLiveClient() {
        return mClient;
    }

    public void setClient(Client client) {
        mClient.setValue(client);
    }

    public void refreshSubscription() {
        mClient.setValue(mClient.getValue());
    }

}