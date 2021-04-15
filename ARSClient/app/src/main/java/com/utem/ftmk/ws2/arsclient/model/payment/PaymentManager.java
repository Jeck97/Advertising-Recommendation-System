package com.utem.ftmk.ws2.arsclient.model.payment;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.utem.ftmk.ws2.arsclient.assistant.FirebaseIdentifier;

public class PaymentManager {

    private static final DatabaseReference sPaymentRef = FirebaseDatabase.getInstance()
            .getReference().child(FirebaseIdentifier.PAYMENT);

    public static void addPayment(Payment payment, PaymentCompleteListener<Void> listener) {
        sPaymentRef.child(payment.getId()).setValue(payment).addOnCompleteListener(listener::onComplete);
    }

    public interface PaymentCompleteListener<T> {
        void onComplete(Task<T> task);
    }
}
