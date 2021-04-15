package com.utem.ftmk.ws2.arsclient.assistant;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class AdvertisementViewGroup {

    public static class TextGroup {
        public EditText editTextTitle;
        public EditText editTextDescription;
    }

    public static class CategoryGroup {
        public TextView textViewHintAddCategory;
        public Chip chipAddCategory;
        public ChipGroup containerCategories;
        public Chip[] addedChips;
        public boolean[] selectionCategories;
        public String[] allCategories;
        public int allCategoriesLength;
    }

    public static class ImageGroup {
        public TextView textViewHintAddImage;
        public ImageView imageViewAddImage;
        public List<String> addedImagesUri;
        public List<String> existedImagesUri;
        public List<String> existedRemovedImagesUri;
        public FlexboxLayout containerImages;
        public ScrollView layoutMain;
        public ImageView layoutFullScreen;
    }

    public static class CoverImageGroup {
        public TextView textViewHintAddCover;
        public String coverImage;
        public RelativeLayout coverImageBorder;
    }

    public static class DateGroup {
        public TextView textViewDateToPost;
        public long dateToPost;
    }

}
