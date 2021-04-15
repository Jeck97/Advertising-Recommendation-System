package com.utem.ftmk.ws2.arsclient.ui.main.subscription;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseError;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.assistant.App;
import com.utem.ftmk.ws2.arsclient.assistant.DateAssistant;
import com.utem.ftmk.ws2.arsclient.assistant.DialogAssistant;
import com.utem.ftmk.ws2.arsclient.model.client.Client;
import com.utem.ftmk.ws2.arsclient.model.client.ClientManager;
import com.utem.ftmk.ws2.arsclient.model.payment.Payment;
import com.utem.ftmk.ws2.arsclient.model.payment.PaymentManager;
import com.utem.ftmk.ws2.arsclient.model.plan.Plan;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

public class SubscriptionPaymentActivity extends AppCompatActivity implements View.OnClickListener {

    // Paypal intent request code to track onActivityResult method
    private static final int REQUEST_PAYPAL = 101;

    // Paypal Configuration Object
    // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
    // or live (ENVIRONMENT_PRODUCTION)
    private static final PayPalConfiguration CONFIG_PAYPAL = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(App.getContext().getString(R.string.paypal_client_id));

    // Default payment info
    private static final String CURRENCY_MALAYSIA = "MYR";
    private static final String PAYMENT_REFERENCE = "Plan Subscription Fee";

    // JSON object identifier
    private static final String RESPONSE = "response";
    private static final String RESPONSE_ID = "id";
    private static final String RESPONSE_CREATE_TIME = "create_time";

    private Plan mPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_payment);

        mPlan = (Plan) getIntent().getSerializableExtra(SubscriptionFragment.EXTRA_PLAN);
        TextView textViewName = findViewById(R.id.textView_subscription_payment_name);
        textViewName.setText(mPlan.getName());
        TextView textViewDescription = findViewById(R.id.textView_subscription_payment_description);
        textViewDescription.setText(mPlan.getDescription());
        TextView textViewDuration = findViewById(R.id.textView_subscription_payment_duration);
        textViewDuration.setText(DateAssistant.planDurationToString(mPlan));
        TextView textViewPrice = findViewById(R.id.textView_subscription_payment_price);
        textViewPrice.setText(getString(R.string.text_preview_subscription_plan_price_placeholder, mPlan.getPrice()));
        Button buttonPay = findViewById(R.id.button_subscription_payment_pay);
        buttonPay.setOnClickListener(this);

        // Start PayPalService
        Intent intentPayPal = new Intent(this, PayPalService.class);
        intentPayPal.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, CONFIG_PAYPAL);
        startService(intentPayPal);


    }

    @Override
    protected void onDestroy() {
        //  Destroy the service when app closes
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        // Creating a paypal payment
        PayPalPayment payPalPayment = new PayPalPayment(BigDecimal.valueOf(mPlan.getPrice()),
                CURRENCY_MALAYSIA, PAYMENT_REFERENCE, PayPalPayment.PAYMENT_INTENT_SALE);

        // Creating Paypal Payment activity intent
        Intent intentPayment = new Intent(this, PaymentActivity.class);
        intentPayment.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, CONFIG_PAYPAL);
        intentPayment.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);

        // Starting the intent activity for result
        // the request code will be used on the method onActivityResult
        startActivityForResult(intentPayment, REQUEST_PAYPAL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PAYPAL) {
            DialogAssistant.showProgressDialog(this, R.string.progress_process_payment);
            Intent intentResult = new Intent();
            if (resultCode == RESULT_OK) {
                assert data != null;
                // Getting the payment confirmation
                System.out.println("result ok from paypal");//todo
                PaymentConfirmation confirmation = data.getParcelableExtra(
                        PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                JSONObject paymentDetail = confirmation.toJSONObject();
                try {
                    String paymentId = paymentDetail.getJSONObject(RESPONSE)
                            .getString(RESPONSE_ID);
                    String paymentDate = paymentDetail.getJSONObject(RESPONSE)
                            .getString(RESPONSE_CREATE_TIME);
                    Payment payment = new Payment(paymentId,
                            DateAssistant.parsePaymentDate(paymentDate).getTime(),
                            ClientManager.getClientUid(), mPlan);

                    System.out.println(paymentDetail);//todo

                    PaymentManager.addPayment(payment, paymentTask -> {
                        System.out.println("payment added");//todo

                        if (paymentTask.isSuccessful()) {
                            System.out.println("payment added successful");//todo

                            ClientManager.getClientOnce(new ClientManager.ClientEventListener() {
                                @Override
                                public void onDataChange(Client client) {
                                    int year = mPlan.getDurationYear();
                                    int month = mPlan.getDurationMonth();
                                    int day = mPlan.getDurationDay();

                                    Client.Subscription subscription = client.getSubscription();
                                    if (subscription == null) {
                                        subscription = new Client.Subscription(true,
                                                DateAssistant.addDate(System.currentTimeMillis(),
                                                        year, month, day), new ArrayList<>());
                                    } else {
                                        if (subscription.getStatus()) {
                                            subscription.setExpiredDate(DateAssistant.addDate(
                                                    subscription.getExpiredDate(), year, month, day));
                                        } else {
                                            subscription.setStatus(true);
                                            subscription.setExpiredDate(DateAssistant.addDate(
                                                    System.currentTimeMillis(), year, month, day));
                                        }
                                    }
                                    subscription.getHistories().add(payment);
                                    client.setSubscription(subscription);
                                    ClientManager.updateClient(client, updateTask -> {
                                        DialogAssistant.dismissProgressDialog();
                                        if (updateTask.isSuccessful()) {
                                            setResult(RESULT_OK);
                                        } else {
                                            intentResult.putExtra(SubscriptionViewPlanActivity.EXTRA_RESULT,
                                                    Objects.requireNonNull(updateTask.getException()).getMessage());
                                            setResult(RESULT_CANCELED, intentResult);
                                        }
                                        finish();
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    DialogAssistant.dismissProgressDialog();
                                    intentResult.putExtra(SubscriptionViewPlanActivity.EXTRA_RESULT,
                                            Objects.requireNonNull(error.getMessage()));
                                    setResult(RESULT_CANCELED, intentResult);
                                    finish();
                                }
                            });
                        } else {
                            DialogAssistant.dismissProgressDialog();
                            intentResult.putExtra(SubscriptionViewPlanActivity.EXTRA_RESULT,
                                    Objects.requireNonNull(paymentTask.getException()).getMessage());
                            setResult(RESULT_CANCELED, intentResult);
                            finish();
                        }
                    });
                } catch (JSONException jsonException) {
                    DialogAssistant.dismissProgressDialog();
                    intentResult.putExtra(SubscriptionViewPlanActivity.EXTRA_RESULT,
                            jsonException.getMessage());
                    setResult(RESULT_CANCELED, intentResult);
                    finish();
                }
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}