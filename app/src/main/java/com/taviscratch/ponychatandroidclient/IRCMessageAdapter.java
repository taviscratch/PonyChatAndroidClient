package com.taviscratch.ponychatandroidclient;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class IRCMessageAdapter extends ArrayAdapter<IRCMessage> {


    static class ViewHolder {
        public TextView normalMessageTextView, actionMessageTextView, sendernameTextView, timepostedTextView;
    }


    public IRCMessageAdapter(Context context, int resource) {
        super(context, resource);
    }

    public IRCMessageAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public IRCMessageAdapter(Context context, int resource, IRCMessage[] objects) {
        super(context, resource, objects);
    }

    public IRCMessageAdapter(Context context, int resource, int textViewResourceId, IRCMessage[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public IRCMessageAdapter(Context context, int resource, List<IRCMessage> objects) {
        super(context, resource, objects);
    }

    public IRCMessageAdapter(Context context, int resource, int textViewResourceId, List<IRCMessage> objects) {
        super(context, resource, textViewResourceId, objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //if(position == 0) position = 1;

        View rowView = convertView;
        IRCMessage message = super.getItem(position);

        if(rowView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewHolder viewHolder = new ViewHolder();

            if(message.getType() == IRCMessage.MessageType.NORMAL) {
                rowView = inflater.inflate(R.layout.irc_message_normal, parent, false);

                viewHolder.sendernameTextView = (TextView) rowView.findViewById(R.id.irc_message_normal_sendername);
                viewHolder.timepostedTextView = (TextView) rowView.findViewById(R.id.irc_message_normal_timeposted);
                viewHolder.normalMessageTextView = (TextView) rowView.findViewById(R.id.irc_message_normal_message);

                viewHolder.sendernameTextView.setText(message.getSender());
                viewHolder.timepostedTextView.setText(message.getFormattedTime());
                viewHolder.normalMessageTextView.setText(message.getMessage());
            }
            else if(message.getType() == IRCMessage.MessageType.ACTION){
                rowView = inflater.inflate(R.layout.irc_message_action, parent, false);
                viewHolder.actionMessageTextView = (TextView) rowView.findViewById(R.id.irc_message_action_text);
                viewHolder.actionMessageTextView.setText(message.getSender() + " " + message.getMessage());
            }

            rowView.setTag(viewHolder);

        } else {
            ViewHolder viewHolder = (ViewHolder) rowView.getTag();

            if(message.getType() == IRCMessage.MessageType.NORMAL) {
                viewHolder.sendernameTextView.setText(message.getSender());
                viewHolder.timepostedTextView.setText(message.getFormattedTime());
                viewHolder.normalMessageTextView.setText(message.getMessage());
            }
            else if(message.getType() == IRCMessage.MessageType.ACTION){
                viewHolder.actionMessageTextView.setText(message.getSender() + " " + message.getMessage());
            }
        }

        return rowView;
    }
}
