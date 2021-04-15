package com.utem.ftmk.ws2.arsclient.ui.main.statistic;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.utem.ftmk.ws2.arsclient.model.advertisement.Advertisement;
import com.utem.ftmk.ws2.arsclient.model.client.ClientManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticViewModel extends ViewModel {

    public static final int DATE_DAY = 0;
    public static final int DATE_MONTH = 1;
    public static final int DATE_YEAR = 2;
    public static final int DATE_PERIOD_START = 3;
    public static final int DATE_PERIOD_END = 4;

    private final MutableLiveData<Long> mDateDay;
    private final MutableLiveData<Long> mDateMonth;
    private final MutableLiveData<Long> mDateYear;
    private final MutableLiveData<Long> mDatePeriodStart;
    private final MutableLiveData<Long> mDatePeriodEnd;

    private final MutableLiveData<List<Advertisement>> mDayAdvertisements;
    private final MutableLiveData<List<Advertisement>> mMonthAdvertisements;
    private final MutableLiveData<List<Advertisement>> mYearAdvertisements;
    private final MutableLiveData<List<Advertisement>> mPeriodAdvertisements;

    private List<Advertisement> mAdvertisements;

    public StatisticViewModel() {

        mDateDay = new MutableLiveData<>();
        mDateMonth = new MutableLiveData<>();
        mDateYear = new MutableLiveData<>();
        mDatePeriodStart = new MutableLiveData<>();
        mDatePeriodEnd = new MutableLiveData<>();

        mDayAdvertisements = new MutableLiveData<>();
        mMonthAdvertisements = new MutableLiveData<>();
        mYearAdvertisements = new MutableLiveData<>();
        mPeriodAdvertisements = new MutableLiveData<>();

        mAdvertisements = new ArrayList<>();
    }

    public void setAdvertisements(List<Advertisement> advertisements) {
        mAdvertisements = advertisements;
    }

    public LiveData<List<Advertisement>> getLiveAdvertisements(int filterType) {
        switch (filterType) {
            case DATE_DAY:
                return mDayAdvertisements;
            case DATE_MONTH:
                return mMonthAdvertisements;
            case DATE_YEAR:
                return mYearAdvertisements;
            default:
                return mPeriodAdvertisements;
        }
    }

    public List<Advertisement> getAdvertisements(int filterType) {
        switch (filterType) {
            case DATE_DAY:
                return mDayAdvertisements.getValue();
            case DATE_MONTH:
                return mMonthAdvertisements.getValue();
            case DATE_YEAR:
                return mYearAdvertisements.getValue();
            default:
                return mPeriodAdvertisements.getValue();
        }
    }

    public void updateAdvertisements(long start, long end, int filterType) {

        List<Advertisement> advertisements = new ArrayList<>(mAdvertisements);
        advertisements = getFilteredAdvertisement(advertisements, null);
        advertisements = getFilteredAdvertisement(advertisements, start, end);

        switch (filterType) {
            case DATE_DAY:
                mDayAdvertisements.setValue(advertisements);
                break;
            case DATE_MONTH:
                mMonthAdvertisements.setValue(advertisements);
                break;
            case DATE_YEAR:
                mYearAdvertisements.setValue(advertisements);
                break;
            default:
                mPeriodAdvertisements.setValue(advertisements);
        }
    }

    public LiveData<Long> getLiveDate(int dateType) {
        switch (dateType) {
            case DATE_DAY:
                return mDateDay;
            case DATE_MONTH:
                return mDateMonth;
            case DATE_YEAR:
                return mDateYear;
            case DATE_PERIOD_START:
                return mDatePeriodStart;
            case DATE_PERIOD_END:
                return mDatePeriodEnd;
            default:
                return null;
        }
    }

    public Long getDate(int dateType) {
        switch (dateType) {
            case DATE_DAY:
                return mDateDay.getValue();
            case DATE_MONTH:
                return mDateMonth.getValue();
            case DATE_YEAR:
                return mDateYear.getValue();
            case DATE_PERIOD_START:
                return mDatePeriodStart.getValue();
            case DATE_PERIOD_END:
                return mDatePeriodEnd.getValue();
            default:
                return null;
        }
    }

    public void setDate(Long date, int dateType) {
        switch (dateType) {
            case DATE_DAY:
                mDateDay.setValue(date);
                break;
            case DATE_MONTH:
                mDateMonth.setValue(date);
                break;
            case DATE_YEAR:
                mDateYear.setValue(date);
                break;
            case DATE_PERIOD_START:
                mDatePeriodStart.setValue(date);
                break;
            case DATE_PERIOD_END:
                mDatePeriodEnd.setValue(date);
                break;
        }
    }

    private List<Advertisement> getFilteredAdvertisement(List<Advertisement> advertisements,
                                                         long start, long end) {
        return advertisements.stream().filter(advertisement -> advertisement.getPostedDate()
                >= start && advertisement.getPostedDate() <= end).collect(Collectors.toList());
    }

    private List<Advertisement> getFilteredAdvertisement(List<Advertisement> advertisements,
                                                         @Nullable String status) {
        return status != null ?
                advertisements.stream().filter(advertisement ->
                        advertisement.getStatus().equals(status)).collect(Collectors.toList()) :
                advertisements.stream().filter(advertisement ->
                        advertisement.getStatus().equals(Advertisement.STATUS_ACTIVATED)
                                || advertisement.getStatus().equals(Advertisement.STATUS_EXPIRED))
                        .collect(Collectors.toList());
    }
}