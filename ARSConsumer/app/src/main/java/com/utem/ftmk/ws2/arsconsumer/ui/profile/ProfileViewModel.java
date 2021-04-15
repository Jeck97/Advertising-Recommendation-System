package com.utem.ftmk.ws2.arsconsumer.ui.profile;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.utem.ftmk.ws2.arsconsumer.model.Consumer;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<Consumer> consumer;

    public ProfileViewModel() {
        consumer = new MutableLiveData<>();
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

}