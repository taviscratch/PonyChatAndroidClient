package com.taviscratch.ponychatandroidclient.irc;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.taviscratch.ponychatandroidclient.R;

import java.util.List;

public class IRCMessageAdapter extends ArrayAdapter<IRCMessage> {

    static class TypedViewHolder {
        public IRCMessage.MessageType type;
    }

    static class PRIVMSGViewHolder extends TypedViewHolder {
        public TextView normalMessageTextView, sendernameTextView, timepostedTextView;
    }
    static class ACTIONViewHolder extends TypedViewHolder {
        public TextView actionMessageTextView;

    }

    private int chatNameColor, chatMessageColor, chatEventColor, chatActionColor;


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
        View rowView = convertView;
        IRCMessage message = super.getItem(position);

        // Ensure that we have a workable rowview
        if(rowView == null || ((TypedViewHolder)rowView.getTag()).type != message.getType()) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if(message.getType() == IRCMessage.MessageType.PRIVMSG) {
                PRIVMSGViewHolder viewHolder = new PRIVMSGViewHolder();
                rowView = inflater.inflate(R.layout.irc_message_normal, parent, false);
                viewHolder.timepostedTextView = (TextView) rowView.findViewById(R.id.irc_message_normal_timeposted);
                viewHolder.normalMessageTextView = (TextView) rowView.findViewById(R.id.irc_message_normal_message);
                viewHolder.sendernameTextView = (TextView) rowView.findViewById(R.id.irc_message_normal_sendername);
                rowView.setTag(viewHolder);
            } else if(message.getType() == IRCMessage.MessageType.ACTION) {
                ACTIONViewHolder viewHolder = new ACTIONViewHolder();
                rowView = inflater.inflate(R.layout.irc_message_action, parent, false);
                viewHolder.actionMessageTextView = (TextView) rowView.findViewById(R.id.irc_message_action_text);
                rowView.setTag(viewHolder);
            }

        }

        // Fill data
        if(message.getType() == IRCMessage.MessageType.PRIVMSG) {
            PRIVMSGViewHolder viewHolder = (PRIVMSGViewHolder) rowView.getTag();
            viewHolder.sendernameTextView.setText(message.getSender());
            viewHolder.timepostedTextView.setText(message.getFormattedTime());
            viewHolder.normalMessageTextView.setText(message.getMessage());

            // set theme colors
            viewHolder.sendernameTextView.setTextColor(chatNameColor);
            viewHolder.timepostedTextView.setTextColor(chatNameColor);
            viewHolder.timepostedTextView.setAlpha(.75f);
            viewHolder.normalMessageTextView.setTextColor(chatMessageColor);

        } else if(message.getType() == IRCMessage.MessageType.ACTION) {
            ACTIONViewHolder viewHolder = (ACTIONViewHolder) rowView.getTag();
            viewHolder.actionMessageTextView.setText(message.getSender() + " " + message.getMessage());
            viewHolder.actionMessageTextView.setTextColor(chatActionColor);
        }

        return rowView;
    }


    public void setChatNameColor(int chatNameColor) {
        this.chatNameColor = chatNameColor;
    }

    public void setChatMessageColor(int chatMessageColor) {
        this.chatMessageColor = chatMessageColor;
    }

    public void setChatEventColor(int chatEventColor) {
        this.chatEventColor = chatEventColor;
    }

    public void setChatActionColor(int chatActionColor) {
        this.chatActionColor = chatActionColor;
    }
}
