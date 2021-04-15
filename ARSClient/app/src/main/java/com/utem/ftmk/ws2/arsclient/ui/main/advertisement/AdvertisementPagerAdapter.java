package com.utem.ftmk.ws2.arsclient.ui.main.advertisement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.assistant.App;

import java.util.Arrays;
import java.util.List;

public class AdvertisementPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragments;
    private final List<String> mPageTitles;

    public AdvertisementPagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragments) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mFragments = fragments;
        this.mPageTitles = Arrays.asList(App.getContext().getResources().getStringArray(R.array.tabs_title));
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mPageTitles.get(position);
    }
}
