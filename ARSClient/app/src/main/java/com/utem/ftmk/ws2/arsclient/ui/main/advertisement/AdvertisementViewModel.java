package com.utem.ftmk.ws2.arsclient.ui.main.advertisement;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.utem.ftmk.ws2.arsclient.model.advertisement.Advertisement;
import com.utem.ftmk.ws2.arsclient.model.advertisement.AdvertisementManager;
import com.utem.ftmk.ws2.arsclient.model.client.Client;
import com.utem.ftmk.ws2.arsclient.model.client.ClientManager;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class AdvertisementViewModel extends ViewModel {

    private final MutableLiveData<Client> mClient;
    private final MutableLiveData<List<Advertisement>> mAdvertisements;

    private List<AdvertisementTabFragment> mTabFragments;

    public AdvertisementViewModel() {
        mClient = new MutableLiveData<>();
        mAdvertisements = new MutableLiveData<>();
    }

    public LiveData<List<Advertisement>> getLiveAdvertisements() {
        return mAdvertisements;
    }

    public void setAdvertisements(List<Advertisement> advertisements) {
        mAdvertisements.setValue(advertisements);
    }

    public Client getClient() {
        return mClient.getValue();
    }

    public void setClient(Client client) {
        mClient.setValue(client);
    }

    public List<Fragment> getTabFragments() {
        if (mTabFragments == null) {
            mTabFragments = new ArrayList<>();
            mTabFragments.add(new AdvertisementTabFragment(null));
            mTabFragments.add(new AdvertisementTabFragment(Advertisement.STATUS_ACTIVATED));
            mTabFragments.add(new AdvertisementTabFragment(Advertisement.STATUS_EXPIRED));
            mTabFragments.add(new AdvertisementTabFragment(Advertisement.STATUS_PENDING));
            mTabFragments.add(new AdvertisementTabFragment(Advertisement.STATUS_REJECTED));
        }
        return new ArrayList<>(mTabFragments);
    }

}