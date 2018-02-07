package com.example.todoshare.todosharecalendar.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.example.todoshare.todosharecalendar.R;
import com.example.todoshare.todosharecalendar.utils.OneDayDecorator;
import com.example.todoshare.todosharecalendar.utils.SaturdayDecorator;
import com.example.todoshare.todosharecalendar.utils.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

public class TodoAddCalendarActivity extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener {

    MaterialCalendarView materialCalendarView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_add_calendar);

        init();
    }

    private void init(){
        materialCalendarView = (MaterialCalendarView) findViewById(R.id.add_calendarview);

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new OneDayDecorator());

        materialCalendarView.setOnDateChangedListener(this);
        materialCalendarView.setOnMonthChangedListener(this);

    }

    private void selectDate() {
        CalendarDay date = materialCalendarView.getSelectedDate();
        Intent intent = new Intent(TodoAddCalendarActivity.this, TodoAddDetailContentActivity.class);
        intent.putExtra("day", date.getDay());
        intent.putExtra("month", date.getMonth());
        intent.putExtra("year", date.getYear());

        startActivity(intent);
        finish();
    }
    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        selectDate();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(TodoAddCalendarActivity.this, MainActivity.class));
        finish();
    }


}
