package com.utem.ftmk.ws2.arsconsumer.ui.home;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utem.ftmk.ws2.arsconsumer.AdvertisementRecyclerViewAdapter;
import com.utem.ftmk.ws2.arsconsumer.R;
import com.utem.ftmk.ws2.arsconsumer.model.Advertisement;
import com.utem.ftmk.ws2.arsconsumer.model.Client;
import com.utem.ftmk.ws2.arsconsumer.model.Consumer;
import com.utem.ftmk.ws2.arsconsumer.utils.DateTimeUtil;

import java.util.List;
import java.util.stream.Collectors;

import me.relex.circleindicator.CircleIndicator;

public class HomeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CLIENT = "extra_client";
    public static final int RESULT_UPDATED = 101;

    private Advertisement advertisement;
    private Consumer consumer;

    private ExtendedFloatingActionButton eFabLike;
    private ImageView ivFullImage;

    private boolean liked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_detail);

        advertisement = (Advertisement) getIntent().getSerializableExtra(
                AdvertisementRecyclerViewAdapter.EXTRA_ADVERTISEMENT);
        consumer = (Consumer) getIntent().getSerializableExtra(
                AdvertisementRecyclerViewAdapter.EXTRA_CONSUMER);
        for (String id : consumer.getLikedAdvertisements()) {
            if (id.equals(advertisement.getId())) {
                liked = true;
                break;
            }
        }

        String[] allCategories = getResources().getStringArray(R.array.advertisement_categories);
        List<String> categoriesString = advertisement.getCategories().stream()
                .map(integer -> allCategories[integer]).collect(Collectors.toList());

        Toolbar toolbar = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(advertisement.getTitle());
        }

        eFabLike = findViewById(R.id.e_fab_liked);
        eFabLike.setText(String.valueOf(advertisement.getLikes().size()));
        eFabLike.setRippleColor(ColorStateList.valueOf(getColor(R.color.heart)));
        if (liked) {
            eFabLike.setIconTint(ColorStateList.valueOf(getColor(R.color.heart)));
        }
        AppBarLayout appBar = findViewById(R.id.app_bar_detail);
        appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (eFabLike.isExtended()) eFabLike.show();
            else eFabLike.hide();
        });

        ViewPager vp = findViewById(R.id.vp_detail);
        vp.setAdapter(new ImagePagerAdapter(advertisement.getImages()));

        CircleIndicator indicator = findViewById(R.id.indicator_detail);
        indicator.setViewPager(vp);

        TextView tv_title = findViewById(R.id.tv_detail_title);
        tv_title.setText(advertisement.getTitle());

        TextView tv_merchant = findViewById(R.id.tv_detail_merchant);
        tv_merchant.setText(advertisement.getMerchantName());

        TextView tv_category = findViewById(R.id.tv_detail_categories);
        tv_category.setText(TextUtils.join(", ", categoriesString));

        TextView tv_location = findViewById(R.id.tv_detail_location);
        tv_location.setText(advertisement.getMerchantLocation().getAddress());

        TextView tv_distance = findViewById(R.id.tv_detail_distance);
        tv_distance.setText(getString(R.string.placeholder_distance_far_away,
                advertisement.getDistance()));

        TextView tv_date = findViewById(R.id.tv_detail_date);
        tv_date.setText(DateTimeUtil.format(advertisement.getPostedDate()));

        TextView tv_description = findViewById(R.id.tv_detail_description);
        tv_description.setText(advertisement.getDescription());

        ivFullImage = findViewById(R.id.iv_detail_full_image);
        ivFullImage.setOnClickListener(v -> ivFullImage.setVisibility(View.GONE));
    }

    public void OnMerchantClick(View view) {
        FirebaseDatabase.getInstance().getReference().child(Client.FIREBASE_IDENTIFIER)
                .child(advertisement.getOwnerId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Intent intent = new Intent(HomeDetailActivity.this,
                                HomeMerchantActivity.class);
                        intent.putExtra(EXTRA_CLIENT, snapshot.getValue(Client.class));
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HomeDetailActivity.this, error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void OnLikeClick(View view) {
        eFabLike.setClickable(false);
        if (liked) {
            liked = false;
            eFabLike.setIconTint(ColorStateList.valueOf(getColor(R.color.white)));
            eFabLike.setText(String.valueOf(advertisement.getLikes().size() - 1));
            for (Advertisement.Like like : advertisement.getLikes()) {
                System.out.println(like.getLikerId());//todo
                System.out.println(consumer.getId());//todo
                if (like.getLikerId().equals(consumer.getId())) {
                    advertisement.getLikes().remove(like);
                    break;
                }
            }
            for (String id : consumer.getLikedAdvertisements()) {
                if (id.equals(advertisement.getId())) {
                    consumer.getLikedAdvertisements().remove(id);
                    break;
                }
            }
        } else {
            liked = true;
            eFabLike.setIconTint(ColorStateList.valueOf(getColor(R.color.heart)));
            eFabLike.setText(String.valueOf(advertisement.getLikes().size() + 1));
            Advertisement.Like like = new Advertisement.Like(
                    consumer.getGender(), consumer.getDob(), consumer.getId());
            advertisement.getLikes().add(like);
            consumer.getLikedAdvertisements().add(advertisement.getId());
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(Advertisement.FIREBASE_IDENTIFIER).child(advertisement.getId())
                .setValue(advertisement).addOnCompleteListener(task ->
                ref.child(Consumer.FIREBASE_IDENTIFIER).child(consumer.getId())
                        .setValue(consumer).addOnCompleteListener(task1 ->
                        eFabLike.setClickable(true)));
        setResult(RESULT_UPDATED);
    }

    @Override
    public void onBackPressed() {
        if (ivFullImage.getVisibility() == View.VISIBLE) {
            ivFullImage.setVisibility(View.GONE);
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private final List<String> images;

        public ImagePagerAdapter(List<String> images) {
            this.images = images;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(HomeDetailActivity.this);
            imageView.setOnClickListener(v -> {
                Glide.with(HomeDetailActivity.this)
                        .load(Uri.parse(images.get(position)))
                        .placeholder(getCircularProgress())
                        .into(ivFullImage);
                ivFullImage.setVisibility(View.VISIBLE);
                ivFullImage.bringToFront();
            });
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(HomeDetailActivity.this)
                    .load(Uri.parse(images.get(position)))
                    .placeholder(getCircularProgress())
                    .into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position,
                                @NonNull Object object) {
            container.removeView((ImageView) object);
        }

        public CircularProgressDrawable getCircularProgress() {
            CircularProgressDrawable drawable
                    = new CircularProgressDrawable(HomeDetailActivity.this);
            drawable.setStyle(CircularProgressDrawable.LARGE);
            drawable.setColorSchemeColors(HomeDetailActivity.this.getColor(R.color.teal_200));
            drawable.start();
            return drawable;
        }
    }

}