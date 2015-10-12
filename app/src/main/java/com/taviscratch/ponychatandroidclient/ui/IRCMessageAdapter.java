package com.taviscratch.ponychatandroidclient.ui;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.taviscratch.ponychatandroidclient.PonyChatApplication;
import com.taviscratch.ponychatandroidclient.R;
import com.taviscratch.ponychatandroidclient.irc.IRCMessage;
import com.taviscratch.ponychatandroidclient.utility.Constants;

import java.util.List;

public class IRCMessageAdapter extends ArrayAdapter<IRCMessage> {

    static class TypedViewHolder {
        public IRCMessage.MessageType type;
    }

    static class NORMALViewHolder extends TypedViewHolder {
        public TextView normalMessageTextView, sendernameTextView, timepostedTextView;
    }
    static class ACTIONViewHolder extends TypedViewHolder {
        public TextView actionMessageTextView;
    }
    static class EVENTViewHolder extends TypedViewHolder {
        public TextView eventMessageTextView;
    }
    static class ERRORViewHolder extends TypedViewHolder {
        public TextView errorMessageTextView;
    }
    static class RAWViewHolder extends TypedViewHolder {
        public TextView rawMessageTextView;
    }


    private int mChatNameColor, mChatMessageColor, mChatEventColor,
            mChatActionColor, mChatErrorColor, mChatRawColor;


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
        IRCMessage.MessageType messageType = message.getType();

        // Ensure that we have a workable rowview
        if(rowView == null || ((TypedViewHolder)rowView.getTag()).type != messageType) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            switch (messageType) {
                case PRIVMSG: {
                    NORMALViewHolder viewHolder = new NORMALViewHolder();
                    rowView = inflater.inflate(R.layout.irc_message_normal, parent, false);
                    viewHolder.timepostedTextView = (TextView) rowView.findViewById(R.id.irc_message_normal_timeposted);
                    viewHolder.normalMessageTextView = (TextView) rowView.findViewById(R.id.irc_message_normal_message);
                    viewHolder.sendernameTextView = (TextView) rowView.findViewById(R.id.irc_message_normal_sendername);
                    rowView.setTag(viewHolder);
                    break;
                }

                case ACTION: {
                    ACTIONViewHolder viewHolder = new ACTIONViewHolder();
                    rowView = inflater.inflate(R.layout.irc_message_action, parent, false);
                    viewHolder.actionMessageTextView = (TextView) rowView.findViewById(R.id.irc_message_action_text);
                    rowView.setTag(viewHolder);
                    break;
                }

                case EVENT: {
                    EVENTViewHolder viewHolder = new EVENTViewHolder();
                    rowView = inflater.inflate(R.layout.irc_message_event, parent, false);
                    viewHolder.eventMessageTextView = (TextView) rowView.findViewById(R.id.irc_message_event_text);
                    rowView.setTag(viewHolder);
                    break;
                }

                case ERROR: {
                    ERRORViewHolder viewHolder = new ERRORViewHolder();
                    rowView = inflater.inflate(R.layout.irc_message_error, parent, false);
                    viewHolder.errorMessageTextView = (TextView) rowView.findViewById(R.id.irc_message_error_text);
                    rowView.setTag(viewHolder);
                    break;
                }

                case RAW: {
                    RAWViewHolder viewHolder = new RAWViewHolder();
                    rowView = inflater.inflate(R.layout.irc_message_raw, parent, false);
                    viewHolder.rawMessageTextView = (TextView) rowView.findViewById(R.id.irc_message_raw_text);
                    rowView.setTag(viewHolder);
                    break;
                }
            }
        }


        // Fill data
        switch(messageType) {

            case PRIVMSG: {
                NORMALViewHolder viewHolder = (NORMALViewHolder) rowView.getTag();
                // set data
                viewHolder.sendernameTextView.setText(message.getSender());
                viewHolder.timepostedTextView.setText(message.getFormattedTime());
                viewHolder.normalMessageTextView.setText(message.getMessage());
                // set theme colors
                viewHolder.sendernameTextView.setTextColor(mChatNameColor);
                viewHolder.timepostedTextView.setTextColor(mChatNameColor);
                viewHolder.timepostedTextView.setAlpha(.75f);
                viewHolder.normalMessageTextView.setTextColor(mChatMessageColor);
                break;
            }

            case ACTION: {
                ACTIONViewHolder viewHolder = (ACTIONViewHolder) rowView.getTag();
                // set data
                viewHolder.actionMessageTextView.setText(message.getSender() + " " + message.getMessage());
                // set theme colors
                viewHolder.actionMessageTextView.setTextColor(mChatActionColor);
                break;
            }

            case EVENT: {
                EVENTViewHolder viewHolder = (EVENTViewHolder) rowView.getTag();
                // set data
                viewHolder.eventMessageTextView.setText(message.getMessage());
                // set theme colors
                viewHolder.eventMessageTextView.setTextColor(mChatEventColor);
                break;
            }

            case ERROR: {
                ERRORViewHolder viewHolder = (ERRORViewHolder) rowView.getTag();
                // set data
                viewHolder.errorMessageTextView.setText(message.getMessage());
                // set theme colors
                viewHolder.errorMessageTextView.setTextColor(mChatErrorColor);
                break;
            }

            case RAW: {
                RAWViewHolder viewHolder = (RAWViewHolder) rowView.getTag();
                // set data
                viewHolder.rawMessageTextView.setText(message.getMessage());
                // set theme colors
                viewHolder.rawMessageTextView.setTextColor(mChatRawColor);
                break;
            }
        }

        return rowView;
    }




    public void updateThemeData() {
        // get the preferences for the current theme being used
        SharedPreferences sharedPreferences = PonyChatApplication.getAppContext()
                .getSharedPreferences(Constants.ThemeColorPreferenceConstants.PREFS_NAME, 0);

        // set member variables to the corresponding preference data
        mChatNameColor = sharedPreferences.getInt(Constants.ThemeColorPreferenceConstants.CHAT_NAME,-1);
        mChatMessageColor = sharedPreferences.getInt(Constants.ThemeColorPreferenceConstants.CHAT_MESSAGE,-1);
        mChatActionColor = sharedPreferences.getInt(Constants.ThemeColorPreferenceConstants.CHAT_ACTION,-1);
        mChatEventColor = sharedPreferences.getInt(Constants.ThemeColorPreferenceConstants.CHAT_EVENT,-1);
        mChatErrorColor = sharedPreferences.getInt(Constants.ThemeColorPreferenceConstants.CHAT_ERROR,-1);
        mChatRawColor = sharedPreferences.getInt(Constants.ThemeColorPreferenceConstants.CHAT_RAW,-1);
    }
}
