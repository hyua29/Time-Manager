package com.app.cooper.time_manager.custom.views;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.app.cooper.time_manager.R;
import com.app.cooper.time_manager.activities.ShowEventActivity;
import com.app.cooper.time_manager.objects.Event;

import java.util.List;

public class EventPickerDialog extends DialogFragment {

    private ListView eventListView;
    private List<Event> eventList;
    private EventListAdaptor adaptor;

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        adaptor = new EventListAdaptor(getActivity(), eventList);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.event_list_view, null);
        eventListView = dialogView.findViewById(R.id.eventListview);
        eventListView.setAdapter(adaptor);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event eventSelected= (Event) eventListView.getAdapter().getItem(position);
                Intent intent = new Intent(getActivity(), ShowEventActivity.class);
                intent.putExtra("eventSelected", eventSelected);
                startActivity(intent);
            }
        });
        return alertDialog;
    }
}
