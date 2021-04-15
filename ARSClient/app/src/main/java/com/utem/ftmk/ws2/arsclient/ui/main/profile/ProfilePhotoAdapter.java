package com.utem.ftmk.ws2.arsclient.ui.main.profile;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.assistant.DialogAssistant;

import java.util.ArrayList;
import java.util.List;

public class ProfilePhotoAdapter extends RecyclerView.Adapter<ProfilePhotoAdapter.ViewHolder> {

    private final Context mContext;
    private final List<String> mPhotos;
    private OnViewHolderClickListener mListener;

    public ProfilePhotoAdapter(Context context, List<String> photos) {
        mContext = context;
        mPhotos = photos != null ? new ArrayList<>(photos) : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.card_view_photo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(mContext)
                .load(Uri.parse(mPhotos.get(position)))
                .placeholder(DialogAssistant.getCircularProgress(mContext))
                .into(holder.mImageView);

        holder.mImageView.setOnClickListener(v -> mListener.onClick(
                mPhotos.get(holder.getAdapterPosition()), holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public void addPhoto(String photo) {
        mPhotos.add(photo);
        notifyItemInserted(mPhotos.size() - 1);
    }

    public void addPhotos(List<String> photos, int count) {
        int currentSize = mPhotos.size();
        mPhotos.addAll(photos);
        notifyItemRangeInserted(currentSize, count);
        System.out.println("addPhotos");//todo
    }

    public void removedPhoto(int position) {
        mPhotos.remove(position);
        notifyItemRemoved(position);
    }

    public List<String> getPhotos() {
        return mPhotos;
    }

    public void setOnViewHolderClickListener(OnViewHolderClickListener listener) {
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView_profile_photo);
        }
    }

    public interface OnViewHolderClickListener {
        void onClick(String photo, int position);
    }

}
