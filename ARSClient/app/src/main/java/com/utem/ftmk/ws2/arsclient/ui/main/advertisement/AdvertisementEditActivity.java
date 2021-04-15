package com.utem.ftmk.ws2.arsclient.ui.main.advertisement;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdvertisementEditActivity extends AppCompatActivity {

    public static final String EXTRA_EDITED_ADVERTISEMENT = "extra_edited_advertisement";

    private Advertisement mAdvertisement;
    private AdvertisementViewGroup.TextGroup mTextGroup;
    private AdvertisementViewGroup.CategoryGroup mCategoryGroup;
    private AdvertisementViewGroup.ImageGroup mImageGroup;
    private AdvertisementViewGroup.CoverImageGroup mCoverImageGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement_edit);

        mAdvertisement = (Advertisement) getIntent().getSerializableExtra
                (AdvertisementDetailActivity.EXTRA_DETAIL_ADVERTISEMENT);

        mTextGroup = new AdvertisementViewGroup.TextGroup();
        mTextGroup.editTextTitle = findViewById(R.id.editText_title);
        mTextGroup.editTextTitle.setText(mAdvertisement.getTitle());
        mTextGroup.editTextDescription = findViewById(R.id.editText_description);
        mTextGroup.editTextDescription.setText(mAdvertisement.getDescription());

        mCategoryGroup = new AdvertisementViewGroup.CategoryGroup();
        mCategoryGroup.textViewHintAddCategory = findViewById(R.id.textView_prompt_add_categories);
        mCategoryGroup.chipAddCategory = findViewById(R.id.chip_add_category);
        mCategoryGroup.containerCategories = findViewById(R.id.chipGroup_categories);
        mCategoryGroup.allCategories = getResources().getStringArray(R.array.advertisement_categories);
        mCategoryGroup.allCategoriesLength = mCategoryGroup.allCategories.length;
        mCategoryGroup.addedChips = new Chip[mCategoryGroup.allCategoriesLength];
        mCategoryGroup.selectionCategories = new boolean[mCategoryGroup.allCategoriesLength];
        initCategories();

        mImageGroup = new AdvertisementViewGroup.ImageGroup();
        mImageGroup.textViewHintAddImage = findViewById(R.id.textView_prompt_add_images);
        mImageGroup.imageViewAddImage = findViewById(R.id.imageView_add_image);
        mImageGroup.containerImages = findViewById(R.id.flexboxLayout_added_images);
        mImageGroup.addedImagesUri = new ArrayList<>();
        mImageGroup.existedImagesUri = new ArrayList<>(mAdvertisement.getImages());
        mImageGroup.existedRemovedImagesUri = new ArrayList<>();
        mImageGroup.layoutMain = findViewById(R.id.scrollView_edit);
        mImageGroup.layoutFullScreen = findViewById(R.id.imageView_edit_full_screen);
        mImageGroup.layoutFullScreen.setOnClickListener(v -> {
            Objects.requireNonNull(getSupportActionBar()).show();
            mImageGroup.layoutMain.setVisibility(View.VISIBLE);
            mImageGroup.layoutFullScreen.setVisibility(View.GONE);
        });

        mCoverImageGroup = new AdvertisementViewGroup.CoverImageGroup();
        mCoverImageGroup.textViewHintAddCover = findViewById(R.id.textView_prompt_select_images_cover);
        mCoverImageGroup.coverImage = mAdvertisement.getCoverImage();

        ImageAssistant.ExistedImagesLoaderCallback loaderCallback
                = new ImageAssistant.ExistedImagesLoaderCallback(
                this, mImageGroup, mCoverImageGroup);
        LoaderManager.getInstance(this).restartLoader(
                ImageAssistant.LOADER_ID_LOADING_EXISTED_IMAGE, null, loaderCallback);
    }

    private void initCategories() {
        for (int index = 0; index < mCategoryGroup.allCategoriesLength; index++) {
            mCategoryGroup.selectionCategories[index] = false;
        }
        for (int index : mAdvertisement.getCategories()) {
            mCategoryGroup.selectionCategories[index] = true;
        }
        mCategoryGroup.containerCategories.removeView(mCategoryGroup.chipAddCategory);
        for (int index = 0; index < mAdvertisement.getCategories().size(); index++) {
            final int INDEX = index;
            final int CATEGORY = mAdvertisement.getCategories().get(INDEX);
            Chip chip = new Chip(this);
            chip.setText(mCategoryGroup.allCategories[CATEGORY]);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                mCategoryGroup.selectionCategories[CATEGORY] = false;
                mCategoryGroup.addedChips[INDEX] = null;
                mCategoryGroup.containerCategories.removeView(chip);
            });
            mCategoryGroup.addedChips[INDEX] = chip;
            mCategoryGroup.containerCategories.addView(chip);
        }
        mCategoryGroup.containerCategories.addView(mCategoryGroup.chipAddCategory);
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

    public void onSaveClicked(View view) {
        boolean validInput = InputValidator.validateAdvertisement(
                mTextGroup, mCategoryGroup, mImageGroup, mCoverImageGroup);

        if (!validInput) {
            return;
        }

        DialogAssistant.showProgressDialog(this, R.string.progress_update_advertisement);
        List<Integer> categories = new ArrayList<>();
        for (int index = 0; index < mCategoryGroup.allCategoriesLength; index++) {
            if (mCategoryGroup.selectionCategories[index]) {
                categories.add(index);
            }
        }

        mAdvertisement.setTitle(mTextGroup.editTextTitle.getText().toString().trim());
        mAdvertisement.setDescription(mTextGroup.editTextDescription.getText().toString().trim());
        mAdvertisement.setCategories(categories);
        mAdvertisement.setCoverImage(mCoverImageGroup.coverImage);

        new AdvertisementManager(this).updateAdvertisement(
                mAdvertisement, mImageGroup, updatedAdvertisement ->
                        DialogAssistant.showUpdateAdvertisementSuccessfulDialog(
                                AdvertisementEditActivity.this, updatedAdvertisement));
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
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

}