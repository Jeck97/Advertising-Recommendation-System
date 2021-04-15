package com.utem.ftmk.ws2.arsconsumer.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.utem.ftmk.ws2.arsconsumer.R;
import com.utem.ftmk.ws2.arsconsumer.model.Client;

import java.util.List;
import java.util.Locale;

import me.relex.circleindicator.CircleIndicator;

public class HomeMerchantActivity extends AppCompatActivity {

    private Client client;
    private ImageView ivFullImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_merchant);

        client = (Client) getIntent().getSerializableExtra(HomeDetailActivity.EXTRA_CLIENT);

        if (client.getLogo() != null) {
            ImageView tvProfile = findViewById(R.id.iv_merchant_profile);
            Glide.with(HomeMerchantActivity.this)
                    .load(Uri.parse(client.getLogo()))
                    .centerCrop()
                    .into(tvProfile);
        }

        if (client.getImages().size() != 0) {
            TextView tvNoImage = findViewById(R.id.tv_merchant_no_image);
            tvNoImage.setVisibility(View.GONE);

            ViewPager vp = findViewById(R.id.vp_merchant);
            vp.setAdapter(new ImagePagerAdapter(client.getImages()));

            CircleIndicator indicator = findViewById(R.id.indicator_merchant);
            indicator.setViewPager(vp);

            ivFullImage = findViewById(R.id.iv_merchant_full_image);
            ivFullImage.setOnClickListener(v -> ivFullImage.setVisibility(View.GONE));
        }

        TextView tvName = findViewById(R.id.tv_merchant_name);
        tvName.setText(client.getStoreName());

        TextView tvAddress = findViewById(R.id.tv_merchant_address);
        tvAddress.setText(client.getLocation().getAddress());

        if (client.getDescription() != null) {
            TextView tvDescription = findViewById(R.id.tv_merchant_description);
            tvDescription.setText(client.getDescription());
        }

    }

    public void OnBackClick(View view) {
        finish();
    }

    public void OnAddressClick(View view) {
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f",
                client.getLocation().getLatitude(), client.getLocation().getLongitude());
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
    }

    @Override
    public void onBackPressed() {
        if (ivFullImage != null) {
            if (ivFullImage.getVisibility() == View.VISIBLE) {
                ivFullImage.setVisibility(View.GONE);
            }
        } else {
            super.onBackPressed();
        }
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
            ImageView imageView = new ImageView(HomeMerchantActivity.this);
            imageView.setOnClickListener(v -> {
                Glide.with(HomeMerchantActivity.this)
                        .load(Uri.parse(images.get(position)))
                        .placeholder(getCircularProgress())
                        .into(ivFullImage);
                ivFullImage.setVisibility(View.VISIBLE);
                ivFullImage.bringToFront();
            });
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(HomeMerchantActivity.this)
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
                    = new CircularProgressDrawable(HomeMerchantActivity.this);
            drawable.setStyle(CircularProgressDrawable.LARGE);
            drawable.setColorSchemeColors(HomeMerchantActivity.this.getColor(R.color.teal_200));
            drawable.start();
            return drawable;
        }
    }
}