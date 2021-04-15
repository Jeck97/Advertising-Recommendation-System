package com.utem.ftmk.ws2.arsclient.assistant;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utem.ftmk.ws2.arsclient.model.advertisement.Advertisement;
import com.utem.ftmk.ws2.arsclient.model.client.Client;
import com.utem.ftmk.ws2.arsclient.model.client.ClientManager;
import com.utem.ftmk.ws2.arsclient.model.payment.Payment;
import com.utem.ftmk.ws2.arsclient.model.plan.Plan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class DeveloperHelper {

    public static void deleteLike() {
        //        TODO: delete like
        FirebaseDatabase.getInstance().getReference().child(FirebaseIdentifier.ADVERTISEMENT)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Advertisement advertisement = snapshot1.getValue(Advertisement.class);
                    advertisement.setLikes(new ArrayList<>());

                    FirebaseDatabase.getInstance().getReference()
                            .child(FirebaseIdentifier.ADVERTISEMENT)
                            .child(advertisement.getId()).setValue(advertisement)
                            .addOnCompleteListener(task -> System.out.println("deleted"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public static void insertPlan() {
        //        TODO: inset plan
            String[] planName = new String[]{
                    "Daily Plan",
                    "Daily Plan+",
                    "Weekly Plan",
                    "Weekly Plan+",
                    "Monthly Plan",
                    "Monthly Plan+",
                    "Monthly Plan+ Pro",
                    "Annually Plan"
            };

            String[] planDescription = new String[]{
                    "Unlimited to post your advertisement within a day.",
                    "Unlimited to post your advertisement within three days. Upgrade your daily plan with a value price.",
                    "Unlimited to post your advertisement within a week.",
                    "Unlimited to post your advertisement within two weeks. Upgrade your weekly plan with a value price.",
                    "Unlimited to post your advertisement within a month.",
                    "Unlimited to post your advertisement within three months. Upgrade your monthly plan with a value price.",
                    "Unlimited to post your advertisement within six months. Upgrade your monthly plan with a supreme price.",
                    "Unlimited to post your advertisement within a year."
            };

            double[] planPrice = new double[]{
                    4.99,
                    9.99,
                    19.99,
                    29.99,
                    49.99,
                    99.99,
                    149.99,
                    199.99
            };

            int[][] planDuration = new int[][]{
                    {0, 0, 1},
                    {0, 0, 3},
                    {0, 0, 7},
                    {0, 0, 14},
                    {0, 1, 0},
                    {0, 3, 0},
                    {0, 6, 0},
                    {1, 0, 0},
            };

            DatabaseReference planRef = FirebaseDatabase.getInstance().getReference().child("plan");
            for (int i = 0; i < 8; i++) {
                DatabaseReference currentPlanRef = planRef.push();
                Plan plan = new Plan(currentPlanRef.getKey(), planName[i], planDescription[i], planDuration[i][0], planDuration[i][1], planDuration[i][2], planPrice[i]);
                currentPlanRef.setValue(plan).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("plan insert successful: " + currentPlanRef.getKey());
                    } else {
                        System.out.println(task.getException().getMessage());
                    }
                });
            }
    }

    public static void insertPayment() {
        //TODO: insert payment
        FirebaseDatabase.getInstance().getReference().child("plan").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Plan> plans = new ArrayList<>();
                for (DataSnapshot planSnapshot : snapshot.getChildren()) {
                    plans.add(planSnapshot.getValue(Plan.class));
                }
                System.out.println(plans.size());


                Random random = new Random();
                Calendar calendar = Calendar.getInstance();
                calendar.set(2019, 0, 1);
                DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference().child("payment");
                for (int i = 0; i < 100; i++) {
                    int randomDay = random.nextInt(8);
                    calendar.add(Calendar.DAY_OF_MONTH, randomDay);

                    int randomClientId = random.nextInt(9) + 1;
                    String clientId = "client00" + randomClientId;

                    int randomPlan = random.nextInt(8);
                    Plan plan = plans.get(randomPlan);

                    DatabaseReference currentPaymentRef = paymentRef.push();
                    Payment payment = new Payment(currentPaymentRef.getKey(), calendar.getTimeInMillis(), clientId, plan);
                    currentPaymentRef.setValue(payment).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            System.out.println("payment insert successful: " + currentPaymentRef.getKey());
                        } else {
                            System.out.println(task.getException().getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}
