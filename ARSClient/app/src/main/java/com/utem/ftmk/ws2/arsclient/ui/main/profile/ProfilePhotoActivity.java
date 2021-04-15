package com.utem.ftmk.ws2.arsclient.ui.main.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.assistant.DialogAssistant;
import com.utem.ftmk.ws2.arsclient.assistant.ImageAssistant;
import com.utem.ftmk.ws2.arsclient.model.client.Client;
import com.utem.ftmk.ws2.arsclient.model.client.ClientManager;

import java.util.ArrayList;
import java.util.List;

public class ProfilePhotoActivity extends AppCompatActivity {

    private ProfilePhotoAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ImageView mImageViewFullScreen;
    private MenuItem mMenuItem;

    private Client mClient;
    private int mPosition;

    private static int sCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photo);

        mClient = (Client) getIntent().getSerializableExtra(ProfileFragment.EXTRA_CLIENT);

        mImageViewFullScreen = findViewById(R.id.imageView_profile_photo_full_screen);

        mAdapter = new ProfilePhotoAdapter(this, mClient.getImages());
        mAdapter.setOnViewHolderClickListener((photo, position) -> {
            mPosition = position;
            Glide.with(this).load(Uri.parse(photo))
                    .placeholder(DialogAssistant.getCircularProgress(this))
                    .into(mImageViewFullScreen);
            mRecyclerView.setVisibility(View.GONE);
            mImageViewFullScreen.setVisibility(View.VISIBLE);
            mImageViewFullScreen.bringToFront();
            mMenuItem.setIcon(R.drawable.ic_delete);
        });

        mRecyclerView = findViewById(R.id.recyclerView_profile_photo);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageAssistant.REQUEST_PICK_IMAGE) {
            if (resultCode == RESULT_OK && data != null && data.getClipData() != null) {
                DialogAssistant.showProgressDialog(this, R.string.progress_add_photo);
                ClipData imagesData = data.getClipData();
                List<String> images = new ArrayList<>();
                StringBuilder errorBuilder = new StringBuilder();
                int photosSize = imagesData.getItemCount();
                sCount = 0;

                for (int index = 0; index < photosSize; index++) {
                    Uri image = imagesData.getItemAt(index).getUri();
                    ClientManager.addStorageImage(image, task -> {
                        if (task.isSuccessful()) {
                            String downloadUri = task.getResult().toString();
                            System.out.println(downloadUri); //todo
                            images.add(downloadUri);
                            mClient.getImages().add(downloadUri);
                        } else {
                            errorBuilder.append(image.toString()).append(": ")
                                    .append(task.getException().toString()).append("\n");
                        }
                        if (++sCount == photosSize) {
                            sCount = 0;
                            ClientManager.updateClient(mClient, task1 -> {
                                DialogAssistant.dismissProgressDialog();
                                if (task1.isSuccessful()) {
                                    System.out.println(images.size());
                                    System.out.println(photosSize);//todo
                                    mAdapter.addPhotos(images, photosSize);
                                } else {
                                    errorBuilder.append(task1.getException().toString());
                                }
                                if (errorBuilder.length() != 0) {
                                    DialogAssistant.showMessageDialog(this,
                                            errorBuilder.toString());
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_edit_photo, menu);
        mMenuItem = menu.findItem(R.id.menu_item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int visibility = mImageViewFullScreen.getVisibility();
        int menuId = item.getItemId();
        switch (visibility) {
            case View.VISIBLE:
                if (menuId == R.id.menu_item) {
                    deletePhoto();
                } else if (menuId == android.R.id.home) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mImageViewFullScreen.setVisibility(View.GONE);
                    mMenuItem.setIcon(R.drawable.ic_add);
                }
                return true;
            case View.GONE:
                if (menuId == R.id.menu_item) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, ImageAssistant.REQUEST_PICK_IMAGE);
                } else if (menuId == android.R.id.home) {
                    finish();
                }
                return true;
            case View.INVISIBLE:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mImageViewFullScreen.getVisibility() == View.VISIBLE) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mImageViewFullScreen.setVisibility(View.GONE);
            mMenuItem.setIcon(R.drawable.ic_add);
        } else {
            super.onBackPressed();
        }
    }

    private void deletePhoto() {
        DialogAssistant.showConfirmDeletePhotoDialog(this, () -> {
            DialogAssistant.showProgressDialog(this,
                    R.string.progress_delete_photo);
            String image = mClient.getImages().get(mPosition);
            ClientManager.deleteStorageImage(image, task -> {
                mClient.getImages().remove(mPosition);
                if (task.isSuccessful()) {
                    ClientManager.updateClient(mClient, task1 -> {
                        DialogAssistant.dismissProgressDialog();
                        if (task1.isSuccessful()) {
                            mAdapter.removedPhoto(mPosition);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mImageViewFullScreen.setVisibility(View.GONE);
                            mMenuItem.setIcon(R.drawable.ic_add);
                        } else {
                            DialogAssistant.showMessageDialog(this,
                                    task.getException().getMessage());
                        }
                    });
                } else {
                    DialogAssistant.dismissProgressDialog();
                    DialogAssistant.showMessageDialog(this,
                            task.getException().getMessage());
                }
            });
        });
    }

}