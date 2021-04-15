package com.utem.ftmk.ws2.arsclient.model.plan;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utem.ftmk.ws2.arsclient.assistant.FirebaseIdentifier;

import java.util.ArrayList;
import java.util.List;

public class PlanManager {

    private static final DatabaseReference sPlanRef = FirebaseDatabase.getInstance()
            .getReference().child(FirebaseIdentifier.PLAN);

    public static void getPlans(PlanEventListener listener) {
        sPlanRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Plan> plans = new ArrayList<>();
                for (DataSnapshot planSnapshot:snapshot.getChildren()) {
                    plans.add(planSnapshot.getValue(Plan.class));
                }
                listener.onDataChange(plans);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onCancelled(error);
            }
        });
    }

    public interface PlanEventListener {
        void onDataChange(List<Plan> plans);
        void onCancelled(DatabaseError error);
    }

}
