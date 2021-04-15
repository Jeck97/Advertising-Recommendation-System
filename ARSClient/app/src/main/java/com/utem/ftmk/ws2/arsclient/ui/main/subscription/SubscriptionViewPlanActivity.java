package com.utem.ftmk.ws2.arsclient.ui.main.subscription;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.model.plan.Plan;
import com.utem.ftmk.ws2.arsclient.model.plan.PlanManager;

import java.util.List;

public class SubscriptionViewPlanActivity extends AppCompatActivity {

    public static final int REQUEST_PAYMENT = 101;
    public static final String EXTRA_RESULT = "intent_result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_view_plan);

        SubscriptionRecyclerViewAdapter adapter = new SubscriptionRecyclerViewAdapter(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_view_plans);
        recyclerView.setAdapter(adapter);

        PlanManager.getPlans(new PlanManager.PlanEventListener() {
            @Override
            public void onDataChange(List<Plan> plans) {
                adapter.updatePlans(plans);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(SubscriptionViewPlanActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PAYMENT) {
            if (resultCode == RESULT_OK) {
                finish();
            } else {
                if (data != null) {
                    String errorMessage = data.getStringExtra(EXTRA_RESULT);
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
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