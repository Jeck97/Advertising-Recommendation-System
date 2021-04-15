package com.utem.ftmk.ws2.arsclient.model.advertisement;

import android.app.Activity;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.utem.ftmk.ws2.arsclient.assistant.AdvertisementViewGroup;
import com.utem.ftmk.ws2.arsclient.assistant.FirebaseIdentifier;
import com.utem.ftmk.ws2.arsclient.model.client.Client;
import com.utem.ftmk.ws2.arsclient.model.client.ClientManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

public class AdvertisementManager {

    private final Activity mActivity;
    private static final DatabaseReference sAdvertisementRef = FirebaseDatabase.getInstance()
            .getReference().child(FirebaseIdentifier.ADVERTISEMENT);
    private static final StorageReference sAdvertisementStorageRef = FirebaseStorage.getInstance()
            .getReference().child(FirebaseIdentifier.ADVERTISEMENT);

    private static int COMPLETED_IMAGES_COUNT = 0;

    public AdvertisementManager(Activity activity) {
        mActivity = activity;
    }

    public void updateAdvertisement(Advertisement advertisement,
                                    AdvertisementViewGroup.ImageGroup imageGroup,
                                    AdvertisementCompleteListener listener) {

        for (String imageUri : imageGroup.existedRemovedImagesUri) {
            FirebaseStorage.getInstance().getReferenceFromUrl(imageUri).delete()
                    .addOnCompleteListener(deleteTask -> {
                        if (!deleteTask.isSuccessful()) {
                            Toast.makeText(mActivity,
                                    Objects.requireNonNull(deleteTask.getException()).getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }

        if (imageGroup.addedImagesUri.size() == 0) {
            advertisement.setImages(imageGroup.existedImagesUri);
            updateAdvertisement(advertisement, listener);
            return;
        }

        for (int index = 0; index < imageGroup.addedImagesUri.size(); index++) {
            int currentIndex = index;
            Uri uri = Uri.parse(imageGroup.addedImagesUri.get(index));
            String path = advertisement.getId() + "/" + uri.getLastPathSegment();

            StorageReference currentAdvertisementStorageRef = sAdvertisementStorageRef.child(path);
            currentAdvertisementStorageRef.putFile(uri).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return currentAdvertisementStorageRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String downloadUri = task.getResult().toString();
                    if (imageGroup.addedImagesUri.get(currentIndex)
                            .equals(advertisement.getCoverImage())) {
                        advertisement.setCoverImage(downloadUri);
                    }
                    imageGroup.addedImagesUri.set(currentIndex, downloadUri);
                    if (++COMPLETED_IMAGES_COUNT == imageGroup.addedImagesUri.size()) {
                        COMPLETED_IMAGES_COUNT = 0;
                        imageGroup.existedImagesUri.addAll(imageGroup.addedImagesUri);
                        advertisement.setImages(imageGroup.existedImagesUri);
                        updateAdvertisement(advertisement, listener);
                    }
                } else {
                    Toast.makeText(mActivity,
                            Objects.requireNonNull(task.getException()).getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void updateAdvertisement(Advertisement advertisement,
                                     AdvertisementCompleteListener listener) {
        sAdvertisementRef.child(advertisement.getId()).setValue(advertisement)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onComplete(advertisement);
                    } else {
                        Toast.makeText(mActivity,
                                Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void addAdvertisement(Advertisement advertisement,
                                 AdvertisementCompleteListener listener) {

        DatabaseReference currentAdvertisementRef = sAdvertisementRef.push();
        advertisement.setId(currentAdvertisementRef.getKey());

        List<String> images = advertisement.getImages();
        for (int index = 0; index < images.size(); index++) {
            int currentIndex = index;
            Uri uriImage = Uri.parse(images.get(index));
            String path = advertisement.getId() + "/" + uriImage.getLastPathSegment();

            StorageReference currentAdvertisementStorageRef = sAdvertisementStorageRef.child(path);
            currentAdvertisementStorageRef.putFile(uriImage).continueWithTask(imageTask -> {
                if (!imageTask.isSuccessful()) {
                    throw Objects.requireNonNull(imageTask.getException());
                }
                return currentAdvertisementStorageRef.getDownloadUrl();
            }).addOnCompleteListener(uriTask -> {
                if (uriTask.isSuccessful()) {
                    String uri = uriTask.getResult().toString();
                    if (images.get(currentIndex).equals(advertisement.getCoverImage())) {
                        advertisement.setCoverImage(uri);
                    }
                    images.set(currentIndex, uri);
                    if (++COMPLETED_IMAGES_COUNT == images.size()) {
                        COMPLETED_IMAGES_COUNT = 0;
                        currentAdvertisementRef.setValue(advertisement)
                                .addOnCompleteListener(uploadTask -> {
                                    if (uploadTask.isSuccessful()) {
                                        listener.onComplete(advertisement);
                                    } else {
                                        Toast.makeText(mActivity, Objects.requireNonNull(
                                                uploadTask.getException()).getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(mActivity,
                            Objects.requireNonNull(uriTask.getException()).getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public static void deleteAdvertisement(Advertisement advertisement,
                                           AdvertisementCompleteListener listener) {
        for (String image : advertisement.getImages()) {
            FirebaseStorage.getInstance().getReferenceFromUrl(image).delete();
        }
        sAdvertisementRef.child(advertisement.getId()).removeValue().addOnCompleteListener(task ->
                listener.onComplete(advertisement));
    }

    public static void getAdvertisements(AdvertisementsValueListener listener) {
        sAdvertisementRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String clientId = ClientManager.getClientUid();

                List<Advertisement> advertisements = new ArrayList<>();
                for (DataSnapshot advertisementSnapshot : snapshot.getChildren()) {
                    Advertisement advertisement = advertisementSnapshot.getValue(Advertisement.class);
                    if (advertisement != null && advertisement.getOwnerId().equals(clientId)) {
                        advertisements.add(advertisement);
                    }
                }
                listener.onDataChange(advertisements);

//                ClientManager.getClient(new ClientManager.ClientEventListener() {
//                    @Override
//                    public void onDataChange(Client client) {
//
//                        List<Advertisement> advertisements = new ArrayList<>();
//                        for (DataSnapshot advertisementSnapshot : snapshot.getChildren()) {
//                            Advertisement advertisement = advertisementSnapshot.getValue(Advertisement.class);
//                            if (advertisement != null && advertisement.getOwnerId().equals(clientId)) {
//                                if (client.getSubscription().getStatus()) {
//                                    advertisement.setStatus(Advertisement.STATUS_ACTIVATED);
//                                } else {
//                                    advertisement.setStatus(Advertisement.STATUS_EXPIRED);
//                                }
//                                advertisements.add(advertisement);
//                            }
//                        }
//                        listener.onDataChange(advertisements);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError error) {
//
//                    }
//                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onCancelled(error);
            }
        });
    }

    public interface AdvertisementCompleteListener {
        void onComplete(Advertisement advertisement);
    }

    public interface AdvertisementsValueListener {
        void onDataChange(List<Advertisement> advertisements);

        void onCancelled(DatabaseError error);
    }

}
