package com.app.cooper.time_manager.custom.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.app.cooper.time_manager.R;
import com.app.cooper.time_manager.objects.Event;

import java.util.List;

/**
 * event list adaptor
 */
public class EventListAdaptor extends BaseAdapter {
    private Context context;
    private List<Event> eventList;

    public EventListAdaptor(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {  // if no view, inflate it
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.event_cell, null); // register list_book_itemml here
        }

        TextView title = convertView.findViewById(R.id.eventTitle);

        title.setText(eventList.get(position).getEventName());

        return convertView;
    }
}
