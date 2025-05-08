package com.example.llamachatbot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messageList;
    private Context context;

    public ChatAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getSender();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == Message.USER) {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_user, parent, false);
            return new UserViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_bot, parent, false);
            return new BotViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String text = messageList.get(position).getText();

        if (holder.getItemViewType() == Message.USER) {
            ((UserViewHolder) holder).textView.setText(text);
        } else {
            ((BotViewHolder) holder).textView.setText(text);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewMessage);
        }
    }

    static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewMessage);
        }
    }
}
