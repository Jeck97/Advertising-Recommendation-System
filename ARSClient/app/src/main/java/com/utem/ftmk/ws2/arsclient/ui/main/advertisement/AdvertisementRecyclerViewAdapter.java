package com.utem.ftmk.ws2.arsclient.ui.main.advertisement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.assistant.App;
import com.utem.ftmk.ws2.arsclient.assistant.DateAssistant;
import com.utem.ftmk.ws2.arsclient.assistant.DialogAssistant;
import com.utem.ftmk.ws2.arsclient.model.advertisement.Advertisement;

import java.util.ArrayList;
import java.util.List;

// TODO: show no data available
// TODO: re-upload the rejected post
// TODO: can delete post
// TODO: show the reason of rejected

public class AdvertisementRecyclerViewAdapter
        extends RecyclerView.Adapter<AdvertisementRecyclerViewAdapter.ViewHolder> {

    private final List<Advertisement> mAdvertisements;
    private Context mContext;

    public AdvertisementRecyclerViewAdapter() {
        mAdvertisements = new ArrayList<>();
    }

    public void updateAdvertisement(List<Advertisement> advertisements) {
        mAdvertisements.clear();
        mAdvertisements.addAll(advertisements);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.card_view_advertisement_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int reversedPosition = mAdvertisements.size() - 1 - position;
        holder.bindData(mAdvertisements.get(reversedPosition));
    }

    @Override
    public int getItemCount() {
        return mAdvertisements.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public static final String EXTRA_HOLDER_ADVERTISEMENT = "extra_holder_advertisement";

        private final ImageView mImageViewCover;
        private final TextView mTextViewTitle;
        private final TextView mTextViewCategories;
        private final TextView mTextViewStatus;
        private final TextView mTextViewLiked;
        private final TextView mTextViewUploadedDate;

        private Advertisement mAdvertisement;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mImageViewCover = view.findViewById(R.id.imageView_card_cover);
            mTextViewTitle = view.findViewById(R.id.textView_card_title);
            mTextViewCategories = view.findViewById(R.id.textView_card_categories);
            mTextViewStatus = view.findViewById(R.id.textView_card_status);
            mTextViewLiked = view.findViewById(R.id.textView_card_liked);
            mTextViewUploadedDate = view.findViewById(R.id.textView_card_date_uploaded);
        }

        public void bindData(Advertisement advertisement) {

            mAdvertisement = advertisement;

            Glide.with(mContext)
                    .load(Uri.parse(advertisement.getCoverImage()))
                    .placeholder(DialogAssistant.getCircularProgress(mContext))
                    .into(mImageViewCover);
            mTextViewTitle.setText(advertisement.getTitle());
            mTextViewCategories.setText(advertisement.getCategoriesToString());
            mTextViewStatus.setText(advertisement.getStatusToString());
            mTextViewLiked.setText(String.valueOf(advertisement.getLikes().size()));
            mTextViewUploadedDate.setText(String.format(App.getContext().getString(R.string.text_preview_advertisement_uploaded_date),
                    DateAssistant.formatDefault(advertisement.getUploadedDate())));

            switch (advertisement.getStatus()) {
                case Advertisement.STATUS_ACTIVATED:
                    mTextViewStatus.setTextColor(App.getContext().getColor(R.color.status_activated));
                    break;
                case Advertisement.STATUS_EXPIRED:
                    mTextViewStatus.setTextColor(App.getContext().getColor(R.color.status_expired));
                    break;
                case Advertisement.STATUS_PENDING:
                    mTextViewStatus.setTextColor(App.getContext().getColor(R.color.status_pending));
                    break;
                case Advertisement.STATUS_REJECTED:
                    mTextViewStatus.setTextColor(App.getContext().getColor(R.color.status_rejected));
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            Activity activity = (Activity) mContext;
            Intent intent = new Intent(activity, AdvertisementDetailActivity.class);
            intent.putExtra(EXTRA_HOLDER_ADVERTISEMENT, mAdvertisement);
            activity.startActivity(intent);
        }
    }

}
