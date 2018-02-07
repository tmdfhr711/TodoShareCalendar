package com.example.todoshare.todosharecalendar.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.todoshare.todosharecalendar.R;
import com.example.todoshare.todosharecalendar.model.Todo;

import java.util.ArrayList;

/**
 * Created by OHRok on 2017-12-31.
 */

public class TodoListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Todo> items;

    public TodoListAdapter(Context context, ArrayList<Todo> items) {
        this.context = context;
        this.items = items;
    }

    @Override

    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final Context context = parent.getContext();
        if(convertView == null) {
            convertView = View.inflate(context, R.layout.custom_todolist_item, null);

            holder = new ViewHolder();
            holder.writor = (TextView)convertView.findViewById(R.id.custom_writor);
            holder.title = (TextView)convertView.findViewById(R.id.custom_title);
            holder.content = (TextView)convertView.findViewById(R.id.custom_content);
            holder.time = (TextView) convertView.findViewById(R.id.custom_time);

            convertView.setTag(holder);


        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final Todo todo = items.get(position);

        if (todo != null) {
            if (!todo.getmShare().equals("1")) {
                convertView.setBackgroundColor(Color.RED);
            }
            holder.writor.setText(todo.getmWritor());
            holder.title.setText(todo.getmTitle());
            holder.content.setText(todo.getmContent());
            holder.time.setText(todo.getmTime());

        }

        Log.e("MY TODO SHARE NUM", todo.getmShare());

        return convertView;
    }

    private class ViewHolder {
        public TextView writor;
        public TextView title;
        public TextView content;
        public TextView time;
    }
}