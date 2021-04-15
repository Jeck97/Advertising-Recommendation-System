package com.utem.ftmk.ws2.arsconsumer;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.utem.ftmk.ws2.arsconsumer.model.Advertisement;
import com.utem.ftmk.ws2.arsconsumer.ui.MainActivity;
import com.utem.ftmk.ws2.arsconsumer.ui.MainViewModel;
import com.utem.ftmk.ws2.arsconsumer.ui.home.HomeDetailActivity;
import com.utem.ftmk.ws2.arsconsumer.utils.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdvertisementRecyclerViewAdapter
        extends RecyclerView.Adapter<AdvertisementRecyclerViewAdapter.ViewHolder> {

    public static final String EXTRA_ADVERTISEMENT = "extra_advertisement";
    public static final String EXTRA_CONSUMER = "extra_consumer";

    private final AppCompatActivity activity;
    private final LayoutInflater inflater;
    private final List<Advertisement> advertisements;

    public AdvertisementRecyclerViewAdapter(AppCompatActivity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.advertisements = new ArrayList<>();
    }

    public void updateList(List<Advertisement> advertisements) {
        this.advertisements.clear();
        this.advertisements.addAll(advertisements);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.card_view_advertisement_post,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(advertisements.get(position));
    }

    @Override
    public int getItemCount() {
        return advertisements.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final View view;

        private final ImageView ivCoverImage;
        private final TextView tvTitle;
        private final TextView tvMerchant;
        private final TextView tvCategories;
        private final TextView tvLike;
        private final TextView tvDistance;
        private final TextView tvPostedDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            ivCoverImage = itemView.findViewById(R.id.iv_ads_cover_image);
            tvTitle = itemView.findViewById(R.id.tv_ads_title);
            tvMerchant = itemView.findViewById(R.id.tv_ads_merchant);
            tvCategories = itemView.findViewById(R.id.tv_ads_categories);
            tvLike = itemView.findViewById(R.id.tv_ads_like);
            tvDistance = itemView.findViewById(R.id.tv_ads_distance);
            tvPostedDate = itemView.findViewById(R.id.tv_ads_posted_date);
        }

        public void bindData(Advertisement advertisement) {
            Glide.with(activity).load(Uri.parse(advertisement.getCoverImage())).into(ivCoverImage);
            tvTitle.setText(advertisement.getTitle());
            tvMerchant.setText(advertisement.getMerchantName());

            String[] allCategories = activity.getResources()
                    .getStringArray(R.array.advertisement_categories);
            List<String> categoriesString = advertisement.getCategories().stream()
                    .map(integer -> allCategories[integer]).collect(Collectors.toList());
            tvCategories.setText(TextUtils.join(", ", categoriesString));
            tvLike.setText(String.valueOf(advertisement.getLikes().size()));
            tvDistance.setText(activity.getString(R.string.placeholder_distance_far_away,
                    advertisement.getDistance()));
            tvPostedDate.setText(DateTimeUtil.format(advertisement.getPostedDate()));

            view.setOnClickListener(v -> {
                Intent intent = new Intent(activity, HomeDetailActivity.class);
                intent.putExtra(EXTRA_ADVERTISEMENT, advertisement);
                intent.putExtra(EXTRA_CONSUMER, new ViewModelProvider(activity)
                        .get(MainViewModel.class).getConsumer());
                activity.startActivityForResult(intent, MainActivity.REQUEST_DETAIL);
            });
        }
    }
}
