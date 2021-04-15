package com.utem.ftmk.ws2.arsclient.ui.main.subscription;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.assistant.DateAssistant;
import com.utem.ftmk.ws2.arsclient.assistant.DialogAssistant;
import com.utem.ftmk.ws2.arsclient.model.client.Client;
import com.utem.ftmk.ws2.arsclient.model.client.ClientManager;
import com.utem.ftmk.ws2.arsclient.model.payment.Payment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubscriptionFragment extends Fragment implements View.OnClickListener {

    public static final String EXTRA_PLAN = "extra_plan";

    private SubscriptionViewModel mSubscriptionViewModel;

    private TextView mTextViewValidDays;
    private TextView mTextViewValidDate;
    private TextView mTextViewWarning;
    private ImageView mImageViewStatusIcon;

    private PaymentHistoryAdapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mSubscriptionViewModel = new ViewModelProvider(requireActivity()).get(SubscriptionViewModel.class);
        mAdapter = new PaymentHistoryAdapter(requireActivity());

        View root = inflater.inflate(R.layout.fragment_subscription, container, false);

        mTextViewValidDays = root.findViewById(R.id.textView_subscription_valid_days);
        mTextViewValidDate = root.findViewById(R.id.textView_subscription_valid_date);
        mTextViewWarning = root.findViewById(R.id.textView_subscription_warning);
        mImageViewStatusIcon = root.findViewById(R.id.imageView_subscription_status_icon);

        ImageView imageViewViewPlanIcon = root.findViewById(R.id.imageView_subscription_view_plan_icon);
        imageViewViewPlanIcon.setOnClickListener(this);
        ListView listViewPaymentHistory = root.findViewById(R.id.listView_payment_history);
        listViewPaymentHistory.setAdapter(mAdapter);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) root;
        swipeRefreshLayout.setOnRefreshListener(() -> mSubscriptionViewModel.refreshSubscription());

        mSubscriptionViewModel.getLiveClient().observe(getViewLifecycleOwner(), client -> {
            Client.Subscription subscription = client.getSubscription();
            if (subscription == null) {
                mTextViewValidDays.setText(getString(R.string.text_preview_subscription_no_subscription));
                mTextViewValidDate.setVisibility(View.GONE);
                mTextViewWarning.setText(getString(R.string.text_preview_subscription_warning_no_subscription));
                mImageViewStatusIcon.setImageResource(R.drawable.ic_status_error);
            } else if (!subscription.getStatus()) {
                mTextViewValidDays.setText(getString(R.string.text_preview_subscription_expired));
                mTextViewValidDate.setVisibility(View.VISIBLE);
                mTextViewValidDate.setText(getString(R.string.text_preview_subscription_valid_until_date, DateAssistant.formatDefault(subscription.getExpiredDate())));
                mTextViewWarning.setText(getString(R.string.text_preview_subscription_warning_expired));
                mImageViewStatusIcon.setImageResource(R.drawable.ic_status_error);
                mAdapter.updateList(subscription.getHistories());
            } else {
                long durationLeft = subscription.getExpiredDate() - System.currentTimeMillis();
                mTextViewValidDays.setText(getString(R.string.text_preview_subscription_valid_duration,
                        DateAssistant.planLeftDurationToString(durationLeft)));
                mTextViewValidDate.setVisibility(View.VISIBLE);
                mTextViewValidDate.setText(getString(R.string.text_preview_subscription_valid_until_date, DateAssistant.formatDefault(subscription.getExpiredDate())));
                if (durationLeft <= 3 * DateAssistant.DAY_IN_MILLIS) {
                    mTextViewWarning.setText(getString(R.string.text_preview_subscription_warning_due_soon));
                    mImageViewStatusIcon.setImageResource(R.drawable.ic_status_warning);
                } else {
                    mTextViewWarning.setText("");
                    mImageViewStatusIcon.setImageResource(R.drawable.ic_status_ok);
                }
                mAdapter.updateList(subscription.getHistories());
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return root;
    }

    @Override
    public void onClick(View v) {
        if (ClientManager.isClientVerified()) {
            requireActivity().startActivity(new Intent(requireActivity(),
                    SubscriptionViewPlanActivity.class));
        } else {
            ClientManager.reloadClient(task -> {
                if (ClientManager.isClientVerified()) {
                    requireActivity().startActivity(new Intent(requireActivity(),
                            SubscriptionViewPlanActivity.class));
                } else {
                    DialogAssistant.showMessageDialog(requireActivity(),
                            R.string.dialog_message_prompt_verify_email);
                }
            });
        }
    }

    public static class PaymentHistoryAdapter extends ArrayAdapter<Payment> {

        private final Context mContext;
        private final LayoutInflater mLayoutInflater;
        private List<Payment> mPayments;

        public PaymentHistoryAdapter(@NonNull Context context) {
            super(context, R.layout.layout_subscription_list_item);
            this.mContext = context;
            this.mLayoutInflater = LayoutInflater.from(context);
            this.mPayments = new ArrayList<>();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.layout_subscription_list_item, parent, false);
            }

            Payment payment = mPayments.get(position);

            TextView textViewDate = convertView.findViewById(R.id.textView_subscription_list_date);
            textViewDate.setText(DateAssistant.formatDefault(payment.getPaidDate()));

            TextView textViewName = convertView.findViewById(R.id.textView_subscription_list_name);
            textViewName.setText(payment.getPlan().getName());

            TextView textViewPrice = convertView.findViewById(R.id.textView_subscription_list_price);
            textViewPrice.setText(mContext.getString(R.string.text_preview_subscription_plan_price_placeholder, payment.getPlan().getPrice()));

            return convertView;
        }

        public void updateList(List<Payment> payments) {
            mPayments = new ArrayList<>(payments);
            Collections.reverse(mPayments);
            clear();
            addAll(mPayments);
        }
    }

}