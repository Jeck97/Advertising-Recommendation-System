package com.utem.ftmk.ws2.arsclient.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseError;
import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.assistant.DeveloperHelper;
import com.utem.ftmk.ws2.arsclient.model.advertisement.Advertisement;
import com.utem.ftmk.ws2.arsclient.model.advertisement.AdvertisementManager;
import com.utem.ftmk.ws2.arsclient.model.client.Client;
import com.utem.ftmk.ws2.arsclient.model.client.ClientManager;
import com.utem.ftmk.ws2.arsclient.ui.authentication.LoginActivity;
import com.utem.ftmk.ws2.arsclient.ui.main.advertisement.AdvertisementViewModel;
import com.utem.ftmk.ws2.arsclient.ui.main.profile.ProfileViewModel;
import com.utem.ftmk.ws2.arsclient.ui.main.statistic.StatisticViewModel;
import com.utem.ftmk.ws2.arsclient.ui.main.subscription.SubscriptionFragment;
import com.utem.ftmk.ws2.arsclient.ui.main.subscription.SubscriptionViewModel;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static boolean init;
    private static boolean clientInitDone;
    private static boolean advertisementsInitDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println(System.currentTimeMillis());//todo

        if (!ClientManager.isClientLoggedIn()) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
            return;
        }
        init = true;
        clientInitDone = false;
        advertisementsInitDone = false;
        setContentView(R.layout.activity_standby_welcome);

        HomeViewModel homeViewModel
                = new ViewModelProvider(this).get(HomeViewModel.class);
        AdvertisementViewModel advertisementViewModel
                = new ViewModelProvider(this).get(AdvertisementViewModel.class);
        StatisticViewModel statisticViewModel
                = new ViewModelProvider(this).get(StatisticViewModel.class);
        SubscriptionViewModel subscriptionViewModel
                = new ViewModelProvider(this).get(SubscriptionViewModel.class);
        ProfileViewModel profileViewModel
                = new ViewModelProvider(this).get(ProfileViewModel.class);


        homeViewModel.getLiveClient().observe(this, client -> {
            advertisementViewModel.setClient(client);
            subscriptionViewModel.setClient(client);
            profileViewModel.setClient(client);
        });

        homeViewModel.getLiveAdvertisements().observe(this, advertisements -> {
            advertisementViewModel.setAdvertisements(advertisements);
            statisticViewModel.setAdvertisements(advertisements);
        });

        ClientManager.getClient(new ClientManager.ClientEventListener() {
            @Override
            public void onDataChange(Client client) {
                homeViewModel.setClient(client);
                clientInitDone = true;
                if (init && advertisementsInitDone) {
                    setContentView(R.layout.activity_home);
                    BottomNavigationView navView = findViewById(R.id.nav_view);
                    NavController navController = Navigation.findNavController(
                            HomeActivity.this, R.id.nav_host_fragment);
                    NavigationUI.setupWithNavController(navView, navController);
                    init = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(),
                        Toast.LENGTH_LONG).show();
                if (init) {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });

        AdvertisementManager.getAdvertisements(new AdvertisementManager
                .AdvertisementsValueListener() {
            @Override
            public void onDataChange(List<Advertisement> advertisements) {

                homeViewModel.setAdvertisements(advertisements);
                advertisementsInitDone = true;
                if (init && clientInitDone) {
                    setContentView(R.layout.activity_home);
                    BottomNavigationView navView = findViewById(R.id.nav_view);
                    NavController navController = Navigation.findNavController(
                            HomeActivity.this, R.id.nav_host_fragment);
                    NavigationUI.setupWithNavController(navView, navController);
                    init = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(),
                        Toast.LENGTH_LONG).show();
                if (init) {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });

//            // TODO: testing
//            DeveloperHelper.deleteLike(); //TODO: delete it
//            DeveloperHelper.insertPlan(); //TODO: delete it
//            DeveloperHelper.insertPayment(); //TODO: delete it
    }



}