package com.utem.ftmk.ws2.arsclient.ui.main.advertisement;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.assistant.DateAssistant;
import com.utem.ftmk.ws2.arsclient.assistant.DialogAssistant;
import com.utem.ftmk.ws2.arsclient.assistant.FirebaseIdentifier;
import com.utem.ftmk.ws2.arsclient.model.advertisement.Advertisement;
import com.utem.ftmk.ws2.arsclient.model.advertisement.AdvertisementManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import me.relex.circleindicator.CircleIndicator;

public class AdvertisementDetailActivity extends AppCompatActivity {

    public static final String EXTRA_DETAIL_ADVERTISEMENT = "extra_detail_advertisement";

    private static final int REQUEST_EDIT_ADVERTISEMENT = 101;

    private Advertisement mAdvertisement;
    private ViewPager mViewPager;
    private TextView mTextViewTitle;
    private TextView mTextViewCategories;
    private TextView mTextViewDescription;
    private CircleIndicator mIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement_detail);

        mAdvertisement = (Advertisement) getIntent().getSerializableExtra(
                AdvertisementRecyclerViewAdapter.ViewHolder.EXTRA_HOLDER_ADVERTISEMENT);

        Toolbar toolbar = findViewById(R.id.toolbar_advertisement_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mAdvertisement.getTitle());
        }

        ExtendedFloatingActionButton eFab = findViewById(R.id.e_fab_liked);
        eFab.setText(String.valueOf(mAdvertisement.getLikes().size()));
        AppBarLayout appBar = findViewById(R.id.app_bar_advertisement_detail);
        appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (eFab.isExtended()) eFab.show();
            else eFab.hide();
        });

        mViewPager = findViewById(R.id.viewPager_advertisement_detail);
        mIndicator = findViewById(R.id.indicator_advertisement_detail);
        mTextViewTitle = findViewById(R.id.textView_detail_title);
        mTextViewCategories = findViewById(R.id.textView_detail_categories);
        mTextViewDescription = findViewById(R.id.textView_detail_description);
        TextView textViewStatus = findViewById(R.id.textView_detail_status);
        TextView textViewUploadedDate = findViewById(R.id.textView_detail_date_uploaded);
        TextView textViewPostedDate = findViewById(R.id.textView_detail_date_posted);
        TextView textViewPostedDateLabel = findViewById(R.id.textView_detail_date_posted_label);
        init();
        textViewStatus.setText(mAdvertisement.getStatusToString());
        textViewUploadedDate.setText(DateAssistant.formatDefault(mAdvertisement.getUploadedDate()));
        textViewPostedDate.setText(DateAssistant.formatDefault(mAdvertisement.getPostedDate()));

        switch (mAdvertisement.getStatus()) {
            case Advertisement.STATUS_ACTIVATED:
                textViewStatus.setTextColor(getColor(R.color.status_activated));
                break;
            case Advertisement.STATUS_EXPIRED:
                textViewStatus.setTextColor(getColor(R.color.status_expired));
                break;
            case Advertisement.STATUS_PENDING:
                textViewStatus.setTextColor(getColor(R.color.status_pending));
                textViewPostedDateLabel.setText(R.string.text_preview_advertisement_date_to_post);
                break;
            case Advertisement.STATUS_REJECTED:
                textViewStatus.setTextColor(getColor(R.color.status_rejected));
                textViewPostedDateLabel.setText(R.string.text_preview_advertisement_date_to_post);
                break;
        }

        // [START] TODO: Data modifier for demonstration purpose, need to be delete afterward
        eFab.setOnClickListener(v -> {
            String id = mAdvertisement.getId();
            long postedDate = mAdvertisement.getPostedDate();
            String[] status = new String[]{Advertisement.STATUS_ACTIVATED, Advertisement.STATUS_EXPIRED, Advertisement.STATUS_PENDING, Advertisement.STATUS_REJECTED};
            new AlertDialog.Builder(AdvertisementDetailActivity.this).setTitle("Modify Data").setSingleChoiceItems(new String[]{"Change status", "Change posted date", "Generate likes until today"}, -1, (dialog, which) -> {
                switch (which) {
                    case 0:
                        dialog.dismiss();
                        new AlertDialog.Builder(AdvertisementDetailActivity.this).setSingleChoiceItems(status, -1, (dialog1, which1) -> {
                            DialogAssistant.showProgressDialog(AdvertisementDetailActivity.this, "Changing to " + status[which1]);
                            mAdvertisement.setStatus(status[which1]);
                            switch (mAdvertisement.getStatus()) {
                                case Advertisement.STATUS_ACTIVATED:
                                    textViewStatus.setTextColor(getColor(R.color.status_activated));
                                    break;
                                case Advertisement.STATUS_EXPIRED:
                                    textViewStatus.setTextColor(getColor(R.color.status_expired));
                                    break;
                                case Advertisement.STATUS_PENDING:
                                    textViewStatus.setTextColor(getColor(R.color.status_pending));
                                    textViewPostedDateLabel.setText(R.string.text_preview_advertisement_date_to_post);
                                    mAdvertisement.setLikes(new ArrayList<>());
                                    eFab.setText(String.valueOf(mAdvertisement.getLikes().size()));
                                    break;
                                case Advertisement.STATUS_REJECTED:
                                    textViewStatus.setTextColor(getColor(R.color.status_rejected));
                                    textViewPostedDateLabel.setText(R.string.text_preview_advertisement_date_to_post);
                                    mAdvertisement.setLikes(new ArrayList<>());
                                    eFab.setText(String.valueOf(mAdvertisement.getLikes().size()));
                                    break;
                            }
                            FirebaseDatabase.getInstance().getReference().child(FirebaseIdentifier.ADVERTISEMENT).child(id).setValue(mAdvertisement).addOnCompleteListener(task -> {
                                textViewStatus.setText(mAdvertisement.getStatusToString());
                                dialog1.dismiss();
                                DialogAssistant.dismissProgressDialog();
                            });
                        }).setNegativeButton("cancel", null).show();
                        break;
                    case 1:
                        dialog.dismiss();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(postedDate);
                        new DatePickerDialog(AdvertisementDetailActivity.this, (view, year, month, dayOfMonth) -> {
                            calendar.set(year, month, dayOfMonth);
                            long newPostedDate = calendar.getTimeInMillis();
                            calendar.add(Calendar.DAY_OF_MONTH, -3);
                            long newUploadedDate = calendar.getTimeInMillis();
                            mAdvertisement.setPostedDate(newPostedDate);
                            mAdvertisement.setUploadedDate(newUploadedDate);
                            DialogAssistant.showProgressDialog(AdvertisementDetailActivity.this, "Changing to " + DateAssistant.formatDefault(newPostedDate));
                            FirebaseDatabase.getInstance().getReference().child(FirebaseIdentifier.ADVERTISEMENT).child(id).setValue(mAdvertisement).addOnCompleteListener(task -> {
                                textViewUploadedDate.setText(DateAssistant.formatDefault(mAdvertisement.getUploadedDate()));
                                textViewPostedDate.setText(DateAssistant.formatDefault(mAdvertisement.getPostedDate()));
                                DialogAssistant.dismissProgressDialog();
                            });
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                        break;
                    case 2:
                        if (mAdvertisement.getStatus().equals(Advertisement.STATUS_PENDING) || mAdvertisement.getStatus().equals(Advertisement.STATUS_REJECTED)) {
                            Toast.makeText(AdvertisementDetailActivity.this, "Invalid status", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String[] genders = new String[]{"male", "female"};
                        Random random = new Random();
                        int randomLike = random.nextInt(100) + 1;
                        List<Advertisement.Like> likes = mAdvertisement.getLikes();
                        int dayRange = (int) TimeUnit.DAYS.convert(System.currentTimeMillis() - postedDate, TimeUnit.MILLISECONDS);
                        System.out.println(dayRange);//todo
                        if (dayRange < 0) {
                            Toast.makeText(AdvertisementDetailActivity.this, "Invalid posted date", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        DialogAssistant.showProgressDialog(AdvertisementDetailActivity.this, "Generating " + randomLike + " likes");
                        Calendar calendarPosted = Calendar.getInstance();
                        for (int i = 0; i < randomLike; i++) {
                            calendarPosted.setTimeInMillis(postedDate);
                            calendarPosted.add(Calendar.DAY_OF_MONTH, random.nextInt(dayRange));
                            long likedDate = calendarPosted.getTimeInMillis();
                            String gender = genders[random.nextInt(2)];
                            int age = random.nextInt(66 - 12) + 12; //12 to 65 years old
                            Advertisement.Like like = new Advertisement.Like(likedDate, gender, age, "consumer" + i);
                            likes.add(like);
                        }
                        mAdvertisement.setLikes(likes);
                        FirebaseDatabase.getInstance().getReference().child(FirebaseIdentifier.ADVERTISEMENT).child(id).setValue(mAdvertisement).addOnCompleteListener(task -> {
                            eFab.setText(String.valueOf(mAdvertisement.getLikes().size()));
                            dialog.dismiss();
                            DialogAssistant.dismissProgressDialog();
                        });
                        break;
                }
            }).setNegativeButton("cancel", null).show();
        });
        // [END]

    }

    private void init() {
        mViewPager.setAdapter(new ImagePagerAdapter(this, mAdvertisement.getImages()));
        mIndicator.setViewPager(mViewPager);
        mTextViewTitle.setText(mAdvertisement.getTitle());
        mTextViewCategories.setText(mAdvertisement.getCategoriesToString());
        mTextViewDescription.setText(mAdvertisement.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_advertisement_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_edit) {
            Intent intent = new Intent(this, AdvertisementEditActivity.class);
            intent.putExtra(EXTRA_DETAIL_ADVERTISEMENT, mAdvertisement);
            startActivityForResult(intent, REQUEST_EDIT_ADVERTISEMENT);
        } else if (item.getItemId() == R.id.menu_delete) {
            DialogAssistant.showConfirmDeleteAdvertisementDialog(this, () ->
                    AdvertisementManager.deleteAdvertisement(mAdvertisement, advertisement ->
                            DialogAssistant.showMessageDialog(this,
                                    R.string.dialog_message_advertisement_deleted, this::finish)));
        } else {
            finish();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_ADVERTISEMENT) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                mAdvertisement = (Advertisement) data.getSerializableExtra(
                        AdvertisementEditActivity.EXTRA_EDITED_ADVERTISEMENT);
                init();
            }
        }
    }

    private static class ImagePagerAdapter extends PagerAdapter {

        private final Context mContext;
        private final List<String> mImages;

        public ImagePagerAdapter(Context context, List<String> images) {
            mContext = context;
            mImages = images;
        }

        @Override
        public int getCount() {
            return mImages.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(mContext)
                    .load(Uri.parse(mImages.get(position)))
                    .placeholder(DialogAssistant.getCircularProgress(mContext))
                    .into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position,
                                @NonNull Object object) {
            container.removeView((ImageView) object);
        }
    }
}