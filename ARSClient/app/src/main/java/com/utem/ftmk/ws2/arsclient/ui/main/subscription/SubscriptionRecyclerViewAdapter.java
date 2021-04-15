package com.utem.ftmk.ws2.arsclient.ui.main.subscription;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.assistant.App;
import com.utem.ftmk.ws2.arsclient.assistant.DateAssistant;
import com.utem.ftmk.ws2.arsclient.model.plan.Plan;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionRecyclerViewAdapter
        extends RecyclerView.Adapter<SubscriptionRecyclerViewAdapter.ViewHolder> {

    private final Activity mActivity;
    private final LayoutInflater mLayoutInflater;
    private final List<Plan> mPlans;

    public SubscriptionRecyclerViewAdapter(Activity activity) {
        this.mActivity = activity;
        this.mLayoutInflater = LayoutInflater.from(activity);
        this.mPlans = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.card_view_subscription_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mPlans.get(position), mActivity);
    }

    @Override
    public int getItemCount() {
        return mPlans.size();
    }

    public void updatePlans(List<Plan> plans) {
        mPlans.clear();
        mPlans.addAll(plans);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextViewName;
        private final TextView mTextViewDescription;
        private final TextView mTextViewDuration;
        private final TextView mTextViewPrice;
        private final Button mButtonPurchase;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewName = itemView.findViewById(R.id.textView_card_plan_name);
            mTextViewDescription = itemView.findViewById(R.id.textView_card_plan_description);
            mTextViewDuration = itemView.findViewById(R.id.textView_card_plan_duration);
            mTextViewPrice = itemView.findViewById(R.id.textView_card_plan_price);
            mButtonPurchase = itemView.findViewById(R.id.button_card_plan_purchase);
        }

        public void bindData(Plan plan, Activity activity) {

            mTextViewName.setText(plan.getName());
            mTextViewDescription.setText(plan.getDescription());
            mTextViewDuration.setText(DateAssistant.planDurationToString(plan));
            mTextViewPrice.setText(App.getContext().getString(
                    R.string.text_preview_subscription_plan_price_placeholder, plan.getPrice()));
            mButtonPurchase.setOnClickListener(v -> {
                Intent intent = new Intent(activity, SubscriptionPaymentActivity.class);
                intent.putExtra(SubscriptionFragment.EXTRA_PLAN, plan);
                activity.startActivityForResult(intent, SubscriptionViewPlanActivity.REQUEST_PAYMENT);
            });
        }
    }

}
