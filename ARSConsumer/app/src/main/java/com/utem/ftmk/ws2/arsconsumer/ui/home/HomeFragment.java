package com.utem.ftmk.ws2.arsconsumer.ui.home;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.utem.ftmk.ws2.arsconsumer.AdvertisementRecyclerViewAdapter;
import com.utem.ftmk.ws2.arsconsumer.R;

import java.util.Arrays;

public class HomeFragment extends Fragment {

    // TODO: Remain the performed results after switch the fragment
    // TODO: Perform combination of search, sort, and filter

    private HomeViewModel homeViewModel;

    private ProgressBar pbLoading;
    private AdvertisementRecyclerViewAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        pbLoading = root.findViewById(R.id.pb_ads_loading);
        pbLoading.setVisibility(View.VISIBLE);

        SearchView svSearch = root.findViewById(R.id.sv_ads);
        svSearch.setOnQueryTextListener(onQueryTextListener);
        String query = homeViewModel.getSearchKey();
        if (!query.isEmpty()) {
            svSearch.setQuery(query, true);
        }

        ImageView ivSort = root.findViewById(R.id.iv_ads_sort);
        ivSort.setOnClickListener(onSortClick);

        ImageView ivFilter = root.findViewById(R.id.iv_ads_filter);
        ivFilter.setOnClickListener(onFilterClick);

        adapter = new AdvertisementRecyclerViewAdapter((AppCompatActivity) requireActivity());
        RecyclerView rvAdvertisement = root.findViewById(R.id.rv_ads);
        rvAdvertisement.setAdapter(adapter);

        homeViewModel.getInitiator().observe(getViewLifecycleOwner(), initObserver);

        return root;
    }

    private final Observer<Void> initObserver = new Observer<Void>() {
        @Override
        public void onChanged(Void aVoid) {
            homeViewModel.getLiveLocation().observe(getViewLifecycleOwner(),
                    location -> homeViewModel.updateAdvertisements(location));
            homeViewModel.getLiveSearchKey().observe(getViewLifecycleOwner(),
                    searchKey -> homeViewModel.updateAdvertisements(searchKey));
            homeViewModel.getLiveSelectedSort().observe(getViewLifecycleOwner(),
                    selectedSort -> homeViewModel.updateAdvertisements(selectedSort));
            homeViewModel.getLiveSelectedFilter().observe(getViewLifecycleOwner(),
                    selectedFilter -> homeViewModel.updateAdvertisements(selectedFilter));
            homeViewModel.getLiveAdvertisement().observe(getViewLifecycleOwner(),
                    advertisements -> {
                        adapter.updateList(advertisements);
                        pbLoading.setVisibility(View.GONE);
                    });
            homeViewModel.getInitiator().removeObserver(initObserver);
        }
    };

    private final SearchView.OnQueryTextListener onQueryTextListener
            = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            homeViewModel.setSearchKey(newText);
            return false;
        }
    };

    private final View.OnClickListener onSortClick = v ->
            new AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.dialog_title_sorting)
                    .setSingleChoiceItems(R.array.sorting_types,
                            homeViewModel.getSelectedSort(),
                            (dialog, which) -> {
                                homeViewModel.setSelectedSort(which);
                                dialog.dismiss();
                            })
                    .setNegativeButton(R.string.button_cancel, null)
                    .show();

    private final View.OnClickListener onFilterClick = v -> {
        boolean[] selectedFilter = homeViewModel.getSelectedFilter();
        AlertDialog alertDialog = new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.dialog_title_filtering)
                .setMultiChoiceItems(R.array.advertisement_categories, selectedFilter,
                        (dialog, which, isChecked) -> selectedFilter[which] = isChecked)
                .setPositiveButton(R.string.button_filter,
                        (dialog, which) -> homeViewModel.setSelectedFilter(selectedFilter))
                .setNegativeButton(R.string.button_cancel, null)
                .setNeutralButton(R.string.button_clear_all, null)
                .show();

        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                .setOnClickListener(button -> {
                    Arrays.fill(selectedFilter, false);
                    for (int i = 0; i < selectedFilter.length; i++) {
                        alertDialog.getListView().setItemChecked(i, false);
                    }
                });

    };
}