package com.utem.ftmk.ws2.arsclient.ui.main.statistic;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.assistant.DateAssistant;
import com.utem.ftmk.ws2.arsclient.assistant.DialogAssistant;
import com.utem.ftmk.ws2.arsclient.model.advertisement.Advertisement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final int FILTER_DAY = 0;
    private static final int FILTER_MONTH = 1;
    private static final int FILTER_YEAR = 2;
    private static final int FILTER_PERIOD = 3;

    private LinearLayout mLayoutFilterDay;
    private LinearLayout mLayoutFilterMonth;
    private LinearLayout mLayoutFilterYear;
    private LinearLayout mLayoutFilterPeriod;
    private Spinner mSpinnerFilter;

    private TextView mTextViewChartTitle;
    private TextView mTextViewChartLabel;
    private BarChart mBarChart;
    private StatisticAdapter mStatisticAdapter;

    private StatisticViewModel mStatisticViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mStatisticViewModel = new ViewModelProvider(requireActivity()).get(StatisticViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistic, container, false);

        mTextViewChartTitle = root.findViewById(R.id.textView_chart_title);
        mTextViewChartLabel = root.findViewById(R.id.textView_chart_y_label);

        mBarChart = root.findViewById(R.id.barChart_statistic);
        mBarChart.setScaleEnabled(false);
        mBarChart.getLegend().setEnabled(false);
        mBarChart.getDescription().setEnabled(true);
        mBarChart.getDescription().setText(getString(R.string.bar_chart_advertisement_x_label));
        mBarChart.getDescription().setTextSize(14f);
        mBarChart.getAxisRight().setDrawLabels(false);
        mBarChart.getXAxis().setGranularityEnabled(true);
        mBarChart.getXAxis().setDrawGridLines(false);
        mBarChart.getXAxis().setLabelRotationAngle(45);
        mBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        mStatisticAdapter = new StatisticAdapter(requireActivity());
        ListView listView = root.findViewById(R.id.listView_statistic);
        listView.setAdapter(mStatisticAdapter);

        mSpinnerFilter = root.findViewById(R.id.spinner_date_type);
        mSpinnerFilter.setSelection(FILTER_MONTH);
        mSpinnerFilter.setOnItemSelectedListener(this);

        mLayoutFilterDay = root.findViewById(R.id.layout_statistic_filter_day);
        TextView textViewFilterDay = mLayoutFilterDay.findViewById(R.id.textView_statistic_selected_date);
        textViewFilterDay.setOnClickListener(new OnDateClickListener(StatisticViewModel.DATE_DAY));
        ImageView imageViewDecreaseDay = mLayoutFilterDay.findViewById(R.id.imageView_decrease_date);
        imageViewDecreaseDay.setOnClickListener(new OnChevronClickListener(FILTER_DAY));
        ImageView imageViewIncreaseDay = mLayoutFilterDay.findViewById(R.id.imageView_increase_date);
        imageViewIncreaseDay.setOnClickListener(new OnChevronClickListener(FILTER_DAY));

        mLayoutFilterMonth = root.findViewById(R.id.layout_statistic_filter_month);
        TextView textViewFilterMonth = mLayoutFilterMonth.findViewById(R.id.textView_statistic_selected_date);
        textViewFilterMonth.setOnClickListener(new OnDateClickListener(StatisticViewModel.DATE_MONTH));
        ImageView imageViewDecreaseMonth = mLayoutFilterMonth.findViewById(R.id.imageView_decrease_date);
        imageViewDecreaseMonth.setOnClickListener(new OnChevronClickListener(FILTER_MONTH));
        ImageView imageViewIncreaseMonth = mLayoutFilterMonth.findViewById(R.id.imageView_increase_date);
        imageViewIncreaseMonth.setOnClickListener(new OnChevronClickListener(FILTER_MONTH));

        mLayoutFilterYear = root.findViewById(R.id.layout_statistic_filter_year);
        TextView textViewFilterYear = mLayoutFilterYear.findViewById(R.id.textView_statistic_selected_date);
        textViewFilterYear.setOnClickListener(new OnDateClickListener(StatisticViewModel.DATE_YEAR));
        ImageView imageViewDecreaseYear = mLayoutFilterYear.findViewById(R.id.imageView_decrease_date);
        imageViewDecreaseYear.setOnClickListener(new OnChevronClickListener(FILTER_YEAR));
        ImageView imageViewIncreaseYear = mLayoutFilterYear.findViewById(R.id.imageView_increase_date);
        imageViewIncreaseYear.setOnClickListener(new OnChevronClickListener(FILTER_YEAR));

        mLayoutFilterPeriod = root.findViewById(R.id.layout_statistic_filter_period);
        TextView textViewStartPeriod = mLayoutFilterPeriod.findViewById(R.id.textView_statistic_period_start);
        textViewStartPeriod.setOnClickListener(new OnDateClickListener(StatisticViewModel.DATE_PERIOD_START));
        TextView textViewEndPeriod = mLayoutFilterPeriod.findViewById(R.id.textView_statistic_period_end);
        textViewEndPeriod.setOnClickListener(new OnDateClickListener(StatisticViewModel.DATE_PERIOD_END));

        mStatisticViewModel.getLiveDate(StatisticViewModel.DATE_DAY).observe(getViewLifecycleOwner(),
                new DateObserver(textViewFilterDay, StatisticViewModel.DATE_DAY));
        mStatisticViewModel.getLiveDate(StatisticViewModel.DATE_MONTH).observe(getViewLifecycleOwner(),
                new DateObserver(textViewFilterMonth, StatisticViewModel.DATE_MONTH));
        mStatisticViewModel.getLiveDate(StatisticViewModel.DATE_YEAR).observe(getViewLifecycleOwner(),
                new DateObserver(textViewFilterYear, StatisticViewModel.DATE_YEAR));
        mStatisticViewModel.getLiveDate(StatisticViewModel.DATE_PERIOD_START).observe(getViewLifecycleOwner(),
                new DateObserver(textViewStartPeriod, StatisticViewModel.DATE_PERIOD_START));
        mStatisticViewModel.getLiveDate(StatisticViewModel.DATE_PERIOD_END).observe(getViewLifecycleOwner(),
                new DateObserver(textViewEndPeriod, StatisticViewModel.DATE_PERIOD_END));

        mStatisticViewModel.getLiveAdvertisements(FILTER_DAY).observe(getViewLifecycleOwner(), mAdvertisementsObserver);
        mStatisticViewModel.getLiveAdvertisements(FILTER_MONTH).observe(getViewLifecycleOwner(), mAdvertisementsObserver);
        mStatisticViewModel.getLiveAdvertisements(FILTER_YEAR).observe(getViewLifecycleOwner(), mAdvertisementsObserver);
        mStatisticViewModel.getLiveAdvertisements(FILTER_PERIOD).observe(getViewLifecycleOwner(), mAdvertisementsObserver);

        return root;
    }

    private final Observer<List<Advertisement>> mAdvertisementsObserver = this::updateBarChat;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mLayoutFilterDay.setVisibility(View.GONE);
        mLayoutFilterMonth.setVisibility(View.GONE);
        mLayoutFilterYear.setVisibility(View.GONE);
        mLayoutFilterPeriod.setVisibility(View.GONE);
        switch (position) {
            case FILTER_DAY:
                if (mStatisticViewModel.getDate(StatisticViewModel.DATE_DAY) == null) {
                    mStatisticViewModel.setDate(System.currentTimeMillis(),
                            StatisticViewModel.DATE_DAY);
                }
                mLayoutFilterDay.setVisibility(View.VISIBLE);
                break;
            case FILTER_MONTH:
                if (mStatisticViewModel.getDate(StatisticViewModel.DATE_MONTH) == null) {
                    mStatisticViewModel.setDate(System.currentTimeMillis(),
                            StatisticViewModel.DATE_MONTH);
                }
                mLayoutFilterMonth.setVisibility(View.VISIBLE);
                break;
            case FILTER_YEAR:
                if (mStatisticViewModel.getDate(StatisticViewModel.DATE_YEAR) == null) {
                    mStatisticViewModel.setDate(System.currentTimeMillis(),
                            StatisticViewModel.DATE_YEAR);
                }
                mLayoutFilterYear.setVisibility(View.VISIBLE);
                break;
            case FILTER_PERIOD:
                if (mStatisticViewModel.getDate(StatisticViewModel.DATE_PERIOD_START) == null) {
                    mStatisticViewModel.setDate(DateAssistant.addDays(System.currentTimeMillis(),
                            -1), StatisticViewModel.DATE_PERIOD_START);
                }
                if (mStatisticViewModel.getDate(StatisticViewModel.DATE_PERIOD_END) == null) {
                    mStatisticViewModel.setDate(System.currentTimeMillis(),
                            StatisticViewModel.DATE_PERIOD_END);
                }
                mLayoutFilterPeriod.setVisibility(View.VISIBLE);
                break;
        }
        updateBarChat(mStatisticViewModel.getAdvertisements(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class DateObserver implements Observer<Long> {

        private final TextView mTextViewDate;
        private final int mDateType;

        public DateObserver(TextView textViewDate, int filterType) {
            mTextViewDate = textViewDate;
            mDateType = filterType;
        }

        @Override
        public void onChanged(Long date) {
            mTextViewDate.setText(DateAssistant.format(date, mDateType));
            updateAdvertisementsByDate();
        }
    }

    private class OnChevronClickListener implements View.OnClickListener {

        private final StatisticViewModel mViewModel;
        private final int mDateType;

        public OnChevronClickListener(int dateType) {
            this.mViewModel = StatisticFragment.this.mStatisticViewModel;
            this.mDateType = dateType;
        }

        @Override
        public void onClick(View v) {
            int dateToChange = v.getId() == R.id.imageView_decrease_date ? -1 : 1;
            mViewModel.setDate(DateAssistant.addDate(mViewModel.getDate(mDateType),
                    dateToChange, mDateType), mDateType);
        }
    }

    private class OnDateClickListener implements View.OnClickListener {

        private final StatisticViewModel mViewModel;
        private final int mDateType;

        public OnDateClickListener(int dateType) {
            this.mViewModel = StatisticFragment.this.mStatisticViewModel;
            this.mDateType = dateType;
        }

        @Override
        public void onClick(View v) {
            switch (mDateType) {
                case StatisticViewModel.DATE_MONTH:
                case StatisticViewModel.DATE_YEAR:
                    DialogAssistant.showFilterMonthPickerDialog(v.getContext(),
                            mViewModel, mDateType);
                    break;
                case StatisticViewModel.DATE_DAY:
                case StatisticViewModel.DATE_PERIOD_START:
                case StatisticViewModel.DATE_PERIOD_END:
                    DialogAssistant.showFilterDatePickerDialog(v.getContext(),
                            mViewModel, mDateType);
                    break;
            }
        }
    }

    private void updateAdvertisementsByDate() {
        long startDate = 0;
        long endDate = 0;
        Calendar selectedDate;
        Calendar calendar = Calendar.getInstance();

        int filterType = mSpinnerFilter.getSelectedItemPosition();
        switch (filterType) {
            case FILTER_DAY:
                selectedDate = DateAssistant.toCalendar(mStatisticViewModel
                        .getDate(StatisticViewModel.DATE_DAY));
                calendar.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH),
                        selectedDate.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                startDate = calendar.getTimeInMillis();
                calendar.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH),
                        selectedDate.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                endDate = calendar.getTimeInMillis();
                break;
            case FILTER_MONTH:
                selectedDate = DateAssistant.toCalendar(mStatisticViewModel
                        .getDate(StatisticViewModel.DATE_MONTH));
                calendar.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH),
                        1, 0, 0, 0);
                startDate = calendar.getTimeInMillis();
                calendar.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH) + 1,
                        0, 23, 59, 59);
                endDate = calendar.getTimeInMillis();
                break;
            case FILTER_YEAR:
                selectedDate = DateAssistant.toCalendar(mStatisticViewModel
                        .getDate(StatisticViewModel.DATE_YEAR));
                calendar.set(selectedDate.get(Calendar.YEAR), 0, 1,
                        0, 0, 0);
                startDate = calendar.getTimeInMillis();
                calendar.set(selectedDate.get(Calendar.YEAR) + 1, 0, 0,
                        23, 59, 59);
                endDate = calendar.getTimeInMillis();
                break;
            case FILTER_PERIOD:
                selectedDate = DateAssistant.toCalendar(mStatisticViewModel
                        .getDate(StatisticViewModel.DATE_PERIOD_START));
                calendar.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH),
                        selectedDate.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                startDate = calendar.getTimeInMillis();
                if (mStatisticViewModel.getDate(StatisticViewModel.DATE_PERIOD_END) == null) {
                    return;
                }
                selectedDate = DateAssistant.toCalendar(mStatisticViewModel
                        .getDate(StatisticViewModel.DATE_PERIOD_END));
                calendar.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH),
                        selectedDate.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                endDate = calendar.getTimeInMillis();
                break;
        }
        mStatisticViewModel.updateAdvertisements(startDate, endDate, filterType);
    }

    private void updateBarChat(List<Advertisement> advertisements) {

        List<Advertisement> copyAdvertisements = new ArrayList<>(advertisements);
        Collections.reverse(copyAdvertisements);

        mTextViewChartTitle.setText(getString(R.string.bar_chart_advertisement_number, advertisements.size()));
        mStatisticAdapter.updateList(copyAdvertisements);
        if (copyAdvertisements.size() == 0) {
            mTextViewChartLabel.setVisibility(View.GONE);
            mBarChart.clear();
            return;
        }
        mTextViewChartLabel.setVisibility(View.VISIBLE);

        List<String> titles = copyAdvertisements.stream().map(advertisement ->
                advertisement.getTitle().length() > 10 ?
                        advertisement.getTitle().substring(0, 10) + "…" :
                        advertisement.getTitle()).collect(Collectors.toList());
        List<BarEntry> entries = new ArrayList<>();
        for (int index = 0; index < copyAdvertisements.size(); index++) {
            entries.add(new BarEntry(index, copyAdvertisements.get(index).getLikes().size()));
        }

        BarDataSet barDataSet = new BarDataSet(entries,
                getString(R.string.bar_chart_advertisement_x_label));
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        mBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(titles));
        mBarChart.clear();
        mBarChart.setData(new BarData(barDataSet));
        mBarChart.getData().setHighlightEnabled(false);
        mBarChart.invalidate();
        mBarChart.fitScreen();
        mBarChart.setVisibleXRangeMaximum(5f);
        mBarChart.animateY(1000);

    }

    private static class StatisticAdapter extends ArrayAdapter<Advertisement> {

        private final Context mContext;
        private final LayoutInflater mInflater;
        private List<Advertisement> mAdvertisements;

        public StatisticAdapter(@NonNull Context context) {
            super(context, R.layout.layout_statistic_list_item);
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
            this.mAdvertisements = new ArrayList<>();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.layout_statistic_list_item,
                        parent, false);
            }

            ImageView imageViewStatus = convertView.findViewById(R.id.imageView_statistic_list_status);
            imageViewStatus.setBackgroundColor(mAdvertisements.get(position).getStatus()
                    .equals(Advertisement.STATUS_ACTIVATED) ?
                    mContext.getColor(R.color.status_activated) :
                    mContext.getColor(R.color.status_expired));
            TextView textViewTitle = convertView.findViewById(R.id.textView_statistic_list_title);
            textViewTitle.setText(mAdvertisements.get(position).getTitle());
            TextView textViewDate = convertView.findViewById(R.id.textView_statistic_list_date);
            textViewDate.setText(DateAssistant.formatDefault(
                    mAdvertisements.get(position).getPostedDate()));
            TextView textViewLikes = convertView.findViewById(R.id.textView_statistic_list_likes);
            textViewLikes.setText(String.valueOf(mAdvertisements.get(position).getLikes().size()));
            return convertView;
        }

        public void updateList(List<Advertisement> advertisements) {
            mAdvertisements = new ArrayList<>(advertisements);
            clear();
            addAll(mAdvertisements);
        }
    }

}