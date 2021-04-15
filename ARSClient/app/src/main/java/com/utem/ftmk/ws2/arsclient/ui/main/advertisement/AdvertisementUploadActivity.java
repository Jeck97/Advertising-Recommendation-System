package com.utem.ftmk.ws2.arsclient.ui.main.advertisement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;

import com.google.android.material.chip.Chip;
import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.assistant.AdvertisementViewGroup;
import com.utem.ftmk.ws2.arsclient.assistant.DialogAssistant;
import com.utem.ftmk.ws2.arsclient.assistant.ImageAssistant;
import com.utem.ftmk.ws2.arsclient.assistant.InputValidator;
import com.utem.ftmk.ws2.arsclient.model.advertisement.Advertisement;
import com.utem.ftmk.ws2.arsclient.model.advertisement.AdvertisementManager;
import com.utem.ftmk.ws2.arsclient.model.client.ClientManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdvertisementUploadActivity extends AppCompatActivity {

    private AdvertisementViewGroup.TextGroup mTextGroup;
    private AdvertisementViewGroup.CategoryGroup mCategoryGroup;
    private AdvertisementViewGroup.ImageGroup mImageGroup;
    private AdvertisementViewGroup.CoverImageGroup mCoverImageGroup;
    private AdvertisementViewGroup.DateGroup mDateGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement_upload);

        mTextGroup = new AdvertisementViewGroup.TextGroup();
        mTextGroup.editTextTitle = findViewById(R.id.editText_title);
        mTextGroup.editTextDescription = findViewById(R.id.editText_description);

        mCategoryGroup = new AdvertisementViewGroup.CategoryGroup();
        mCategoryGroup.textViewHintAddCategory = findViewById(R.id.textView_prompt_add_categories);
        mCategoryGroup.chipAddCategory = findViewById(R.id.chip_add_category);
        mCategoryGroup.containerCategories = findViewById(R.id.chipGroup_categories);
        mCategoryGroup.allCategories = getResources().getStringArray(R.array.advertisement_categories);
        mCategoryGroup.allCategoriesLength = mCategoryGroup.allCategories.length;
        mCategoryGroup.addedChips = new Chip[mCategoryGroup.allCategoriesLength];
        mCategoryGroup.selectionCategories = new boolean[mCategoryGroup.allCategoriesLength];

        mImageGroup = new AdvertisementViewGroup.ImageGroup();
        mImageGroup.textViewHintAddImage = findViewById(R.id.textView_prompt_add_images);
        mImageGroup.imageViewAddImage = findViewById(R.id.imageView_add_image);
        mImageGroup.containerImages = findViewById(R.id.flexboxLayout_added_images);
        mImageGroup.addedImagesUri = new ArrayList<>();
        mImageGroup.layoutMain = findViewById(R.id.scrollView_upload);
        mImageGroup.layoutFullScreen = findViewById(R.id.imageView_upload_full_screen);
        mImageGroup.layoutFullScreen.setOnClickListener(v -> {
            Objects.requireNonNull(getSupportActionBar()).show();
            mImageGroup.layoutMain.setVisibility(View.VISIBLE);
            mImageGroup.layoutFullScreen.setVisibility(View.GONE);
        });

        mCoverImageGroup = new AdvertisementViewGroup.CoverImageGroup();
        mCoverImageGroup.textViewHintAddCover = findViewById(R.id.textView_prompt_select_images_cover);

        mDateGroup = new AdvertisementViewGroup.DateGroup();
        mDateGroup.textViewDateToPost = findViewById(R.id.textView_date_to_post);

    }

    public void onAddCategoryClicked(View view) {
        for (int index = 0; index < mCategoryGroup.allCategoriesLength; index++) {
            mCategoryGroup.selectionCategories[index] = mCategoryGroup.addedChips[index] != null;
        }
        DialogAssistant.showCategoriesDialog(this, mCategoryGroup);
    }

    public void onAddImageClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, ImageAssistant.REQUEST_PICK_IMAGE);
    }

    public void onSelectDateClicked(View view) {
        DialogAssistant.showDateToPostPickerDialog(this, mDateGroup);
    }

    public void onUploadClicked(View view) {

        boolean validInput = InputValidator.validateAdvertisement(
                mTextGroup, mCategoryGroup, mImageGroup, mCoverImageGroup, mDateGroup);

        if (!validInput) {
            return;
        }

        DialogAssistant.showProgressDialog(this, R.string.progress_upload_advertisement);
        List<Integer> categories = new ArrayList<>();
        for (int index = 0; index < mCategoryGroup.allCategoriesLength; index++) {
            if (mCategoryGroup.selectionCategories[index]) {
                categories.add(index);
            }
        }

        Advertisement advertisement = new Advertisement(
                mTextGroup.editTextTitle.getText().toString().trim(),
                mTextGroup.editTextDescription.getText().toString().trim(),
                categories,
                mDateGroup.dateToPost,
                mCoverImageGroup.coverImage,
                mImageGroup.addedImagesUri,
                ClientManager.getClientUid());


        new AdvertisementManager(AdvertisementUploadActivity.this)
                .addAdvertisement(advertisement, updatedAdvertisement ->
                        DialogAssistant.showUploadAdvertisementSuccessfulDialog(
                                AdvertisementUploadActivity.this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageAssistant.REQUEST_PICK_IMAGE) {
            if (resultCode == RESULT_OK && data != null && data.getClipData() != null) {
                Bundle bundleImages = new Bundle();
                bundleImages.putParcelable(ImageAssistant.BUNDLE_IMAGES, data.getClipData());

                ImageAssistant.AddedImagesLoaderCallback loaderCallback
                        = new ImageAssistant.AddedImagesLoaderCallback(
                        this, mImageGroup, mCoverImageGroup);
                LoaderManager.getInstance(this).restartLoader(
                        ImageAssistant.LOADER_ID_LOADING_IMAGE, bundleImages, loaderCallback);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mImageGroup.layoutFullScreen.getVisibility() == View.VISIBLE) {
            Objects.requireNonNull(getSupportActionBar()).show();
            mImageGroup.layoutMain.setVisibility(View.VISIBLE);
            mImageGroup.layoutFullScreen.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

}