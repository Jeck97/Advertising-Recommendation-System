package com.utem.ftmk.ws2.arsconsumer.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.utem.ftmk.ws2.arsconsumer.R;
import com.utem.ftmk.ws2.arsconsumer.model.Client;
import com.utem.ftmk.ws2.arsconsumer.ui.auth.LoginActivity;
import com.utem.ftmk.ws2.arsconsumer.ui.history.HistoryViewModel;
import com.utem.ftmk.ws2.arsconsumer.ui.home.HomeDetailActivity;
import com.utem.ftmk.ws2.arsconsumer.ui.home.HomeViewModel;
import com.utem.ftmk.ws2.arsconsumer.ui.profile.ProfileViewModel;
import com.utem.ftmk.ws2.arsconsumer.utils.LocationUtil;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CHANGE_LOCATION = 102;
    public static final int REQUEST_DETAIL = 103;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    public static final String EXTRA_INITIAL_LOCATION = "extra_initial_location";

    private AppBarConfiguration appBarConfiguration;
    private FirebaseAuth auth;
    private MainViewModel mainViewModel;
    private HomeViewModel homeViewModel;
    private HistoryViewModel historyViewModel;
    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_location) {
            Intent intent = new Intent(this, MainLocationActivity.class);
            intent.putExtra(EXTRA_INITIAL_LOCATION, mainViewModel.getLocation());
            startActivityForResult(intent, REQUEST_CHANGE_LOCATION);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHANGE_LOCATION:
                if (resultCode == RESULT_OK && data != null) {
                    mainViewModel.setLocation((Client.Location) data.getSerializableExtra(
                            MainLocationActivity.EXTRA_SELECTED_LOCATION));
                } else if (resultCode == MainLocationActivity.RESULT_PERMISSION_DINED) {
                    Toast.makeText(this, R.string.error_permission_dined,
                            Toast.LENGTH_LONG).show();
                }
                break;

            case REQUEST_DETAIL:
                if (resultCode == HomeDetailActivity.RESULT_UPDATED) {
                    homeViewModel.updateAdvertisements();
                    historyViewModel.updateAdvertisements();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                finish();
            }
        }
    }

    public void onLogoutClick(View view) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.dialog_message_confirm_logout)
                .setPositiveButton(R.string.button_yes, (dialog, which) -> {
                    auth.signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton(R.string.button_no, null)
                .show();
    }

    private void init() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_history, R.id.nav_profile)
                .setOpenableLayout(drawer).build();
        NavController navController = Navigation.findNavController(
                this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(
                this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View header = navigationView.getHeaderView(0);
        ImageView ivProfileImage = header.findViewById(R.id.iv_nav_profile_image);
        TextView tvUsername = header.findViewById(R.id.tv_nav_username);
        TextView tvEmail = header.findViewById(R.id.tv_nav_email);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        homeViewModel = new ViewModelProvider(this)
                .get(HomeViewModel.class);
        historyViewModel = new ViewModelProvider(this)
                .get(HistoryViewModel.class);
        profileViewModel = new ViewModelProvider(this)
                .get(ProfileViewModel.class);

        if (mainViewModel.getLocation() == null) {
            LocationUtil.getLastKnownLocation(this, location -> {
                mainViewModel.setLocation(location);
                homeViewModel.init(location);
                historyViewModel.init(location);
            });
        }
        mainViewModel.getLiveLocation().observe(this, location -> {
            toolbar.setTitle(location.getName());
            toolbar.setSubtitle(location.getAddress());
            homeViewModel.setLocation(location);
            historyViewModel.setLocation(location);
        });

        mainViewModel.getLiveConsumer().observe(this, consumer -> {
            if (consumer.getProfileImage() != null) {
                Glide.with(this)
                        .load(Uri.parse(consumer.getProfileImage()))
                        .centerCrop()
                        .into(ivProfileImage);
            }
            tvUsername.setText(consumer.getUsername());
            tvEmail.setText(consumer.getEmail());
            historyViewModel.setConsumer(consumer);
            profileViewModel.setConsumer(consumer);
        });
    }
}