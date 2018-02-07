package com.example.todoshare.todosharecalendar.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;

/**
 * Created by OHRok on 2017-12-30.
 */

public class DateDecorator implements DayViewDecorator {
    private int color = 0;
    private final ArrayList<CalendarDay> dates;
    private ColorDrawable drawable;
    private Context context;

    public DateDecorator(Context context, int color, ArrayList<CalendarDay> dates) {
        this.context = context;
        this.color = color;
        this.dates = new ArrayList<>(dates);
        drawable = new ColorDrawable(color);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(10, color));
    }
}

