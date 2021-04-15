package com.utem.ftmk.ws2.arsclient.assistant;

import android.content.ClipData;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.bumptech.glide.Glide;
import com.utem.ftmk.ws2.arsclient.R;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class ImageAssistant {

    public static final int REQUEST_PICK_IMAGE = 101;
    public static final int LOADER_ID_LOADING_IMAGE = 201;
    public static final int LOADER_ID_LOADING_EXISTED_IMAGE = 202;
    public static final String BUNDLE_IMAGES = "bundle_images";

    public static Bitmap getCompressedBitmap(Uri imageUri) {
        Bitmap pickedImage = null;
        try {
            pickedImage = Glide.with(App.getContext()).asBitmap().load(imageUri).submit().get();
            float bitmapRatio = (float) pickedImage.getWidth() / (float) pickedImage.getHeight();
            int width = bitmapRatio > 0 ? 480 : (int) (480 * bitmapRatio);
            int height = bitmapRatio > 0 ? (int) (480 / bitmapRatio) : 480;
            pickedImage = Bitmap.createScaledBitmap(pickedImage, width, height, true);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return pickedImage;
    }

    public static class AddedImagesLoaderCallback implements LoaderManager.LoaderCallbacks<Void> {

        private final AppCompatActivity mActivity;
        private final AdvertisementViewGroup.ImageGroup mImageGroup;
        private final AdvertisementViewGroup.CoverImageGroup mCoverImageGroup;

        public AddedImagesLoaderCallback(AppCompatActivity activity,
                                         AdvertisementViewGroup.ImageGroup imageGroup,
                                         AdvertisementViewGroup.CoverImageGroup coverImageGroup) {
            this.mActivity = activity;
            this.mImageGroup = imageGroup;
            this.mCoverImageGroup = coverImageGroup;

        }

        @NonNull
        @Override
        public Loader<Void> onCreateLoader(int id, @Nullable Bundle args) {

            mImageGroup.textViewHintAddImage.setTextColor(mActivity.getColor(R.color.hint));
            assert args != null;
            ClipData imagesData = args.getParcelable(BUNDLE_IMAGES);
            DialogAssistant.showProgressDialog(mActivity, R.string.progress_loading_images);
            mImageGroup.containerImages.removeView(mImageGroup.imageViewAddImage);

            return new AsyncTaskLoader<Void>(mActivity) {

                @Nullable
                @Override
                public Void loadInBackground() {
                    for (int index = 0; index < imagesData.getItemCount(); index++) {

                        Uri pickedImageUri = imagesData.getItemAt(index).getUri();
                        Bitmap resizedImage = ImageAssistant.getCompressedBitmap(pickedImageUri);

                        mActivity.runOnUiThread(() -> {
                            View view = LayoutInflater.from(mActivity).inflate(
                                    R.layout.layout_image_closable,
                                    mImageGroup.containerImages, false);

                            RelativeLayout layoutSelectedImage =
                                    view.findViewById(R.id.layout_selected_effect);

                            ImageView imageView = view.findViewById(R.id.imageVieW_image_selected);
                            Glide.with(mActivity).load(resizedImage).thumbnail(0.05f).into(imageView);
                            imageView.setOnClickListener(v -> {
                                if (mCoverImageGroup.coverImageBorder != null) {
                                    mCoverImageGroup.coverImageBorder.setBackgroundColor(0);
                                }
                                layoutSelectedImage.setBackgroundColor(
                                        mActivity.getColor(R.color.purple_500));
                                mCoverImageGroup.coverImageBorder = layoutSelectedImage;
                                mCoverImageGroup.coverImage = pickedImageUri.toString();
                                mCoverImageGroup.textViewHintAddCover.setTextColor(
                                        mActivity.getColor(R.color.hint));
                            });

                            ImageButton closeButton = view.findViewById(R.id.imageButton_remove);
                            closeButton.setOnClickListener(v -> {
                                mImageGroup.addedImagesUri.remove(pickedImageUri.toString());
                                mImageGroup.containerImages.removeView(view);
                                if (mCoverImageGroup.coverImage != null &&
                                        mCoverImageGroup.coverImage.equals(pickedImageUri.toString())) {
                                    mCoverImageGroup.coverImage = null;
                                }
                            });

                            ImageView fullScreen = view.findViewById(R.id.imageView_button_full_screen);
                            fullScreen.setOnClickListener(v -> {
                                Objects.requireNonNull(mActivity.getSupportActionBar()).hide();
                                mImageGroup.layoutFullScreen.setImageURI(pickedImageUri);
                                mImageGroup.layoutFullScreen.setVisibility(View.VISIBLE);
                                mImageGroup.layoutMain.setVisibility(View.GONE);
                            });

                            mImageGroup.addedImagesUri.add(pickedImageUri.toString());
                            mImageGroup.containerImages.addView(view);
                        });
                    }
                    return null;
                }

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    forceLoad();
                }
            };
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Void> loader, Void data) {
            mImageGroup.containerImages.addView(mImageGroup.imageViewAddImage);
            DialogAssistant.dismissProgressDialog();
            LoaderManager.getInstance(mActivity).destroyLoader(LOADER_ID_LOADING_IMAGE);
        }

        @Override
        public void onLoaderReset(@NonNull Loader loader) {
        }
    }

    public static class ExistedImagesLoaderCallback implements LoaderManager.LoaderCallbacks<Void> {

        private final AppCompatActivity mActivity;
        private final AdvertisementViewGroup.ImageGroup mImageGroup;
        private final AdvertisementViewGroup.CoverImageGroup mCoverImageGroup;

        public ExistedImagesLoaderCallback(AppCompatActivity activity,
                                           AdvertisementViewGroup.ImageGroup imageGroup,
                                           AdvertisementViewGroup.CoverImageGroup coverImageGroup) {
            this.mActivity = activity;
            this.mImageGroup = imageGroup;
            this.mCoverImageGroup = coverImageGroup;
        }

        @NonNull
        @Override
        public Loader<Void> onCreateLoader(int id, @Nullable Bundle args) {

            mImageGroup.containerImages.removeView(mImageGroup.imageViewAddImage);

            return new AsyncTaskLoader<Void>(mActivity) {

                @Nullable
                @Override
                public Void loadInBackground() {
                    for (int index = 0; index < mImageGroup.existedImagesUri.size(); index++) {

                        Uri pickedImageUri = Uri.parse(mImageGroup.existedImagesUri.get(index));
                        Bitmap resizedImage = ImageAssistant.getCompressedBitmap(pickedImageUri);

                        mActivity.runOnUiThread(() -> {
                            View view = LayoutInflater.from(mActivity).inflate(
                                    R.layout.layout_image_closable,
                                    mImageGroup.containerImages, false);

                            RelativeLayout layoutSelectedImage
                                    = view.findViewById(R.id.layout_selected_effect);

                            ImageView imageView = view.findViewById(R.id.imageVieW_image_selected);
                            Glide.with(mActivity).load(resizedImage).thumbnail(0.05f).into(imageView);
                            imageView.setOnClickListener(v -> {
                                if (mCoverImageGroup.coverImageBorder != null) {
                                    mCoverImageGroup.coverImageBorder.setBackgroundColor(0);
                                }
                                layoutSelectedImage.setBackgroundColor(
                                        mActivity.getColor(R.color.purple_500));
                                mCoverImageGroup.coverImageBorder = layoutSelectedImage;
                                mCoverImageGroup.coverImage = pickedImageUri.toString();
                                mCoverImageGroup.textViewHintAddCover.setTextColor(
                                        mActivity.getColor(R.color.hint));
                            });

                            ImageButton closeButton = view.findViewById(R.id.imageButton_remove);
                            closeButton.setOnClickListener(v -> {
                                mImageGroup.existedImagesUri.remove(pickedImageUri.toString());
                                mImageGroup.existedRemovedImagesUri.add(pickedImageUri.toString());
                                mImageGroup.containerImages.removeView(view);
                                if (mCoverImageGroup.coverImage != null &&
                                        mCoverImageGroup.coverImage.equals(pickedImageUri.toString())) {
                                    mCoverImageGroup.coverImage = null;
                                }
                            });

                            ImageView fullScreen = view.findViewById(R.id.imageView_button_full_screen);
                            fullScreen.setOnClickListener(v -> {
                                Objects.requireNonNull(mActivity.getSupportActionBar()).hide();
                                mImageGroup.layoutFullScreen.setImageURI(pickedImageUri);
                                mImageGroup.layoutFullScreen.setVisibility(View.VISIBLE);
                                mImageGroup.layoutMain.setVisibility(View.GONE);
                            });

                            if (mCoverImageGroup.coverImage != null && mCoverImageGroup.coverImage
                                    .equals(pickedImageUri.toString())) {
                                layoutSelectedImage.setBackgroundColor(
                                        mActivity.getColor(R.color.purple_500));
                                mCoverImageGroup.coverImageBorder = layoutSelectedImage;
                            }
                            mImageGroup.containerImages.addView(view);
                        });
                    }
                    return null;
                }

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    forceLoad();
                }
            };
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Void> loader, Void data) {
            mImageGroup.containerImages.addView(mImageGroup.imageViewAddImage);
            LoaderManager.getInstance(mActivity).destroyLoader(LOADER_ID_LOADING_EXISTED_IMAGE);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Void> loader) {

        }
    }

}
