package com.wgt.chatchat;

import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by debasish on 23-03-2018.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<ChatModel> chatList;
    private RecyclerView rv;

    public ChatAdapter() {
        this.chatList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_layout, parent, false);
        if (rv == null && parent instanceof RecyclerView) {
            rv = (RecyclerView) parent;
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatModel chat = chatList.get(position);

        holder.msg.setText(chat.getMsg());
        holder.setIsMe(chat.getIsMe());
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView msg;
        LinearLayout layout_msg;

        public ViewHolder(View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.tv_msg);
            layout_msg = itemView.findViewById(R.id.layout_msg);
        }

        public void setIsMe(boolean isMe) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            if (isMe) {
                lp.setMargins(50, 10, 5, 10);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    msg.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                }
            } else {
                lp.setMargins(5, 10, 50, 10);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    msg.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                }
            }
            layout_msg.setLayoutParams(lp);
        }
    }

    public int addChat(ChatModel chatModel) {
        chatList.add(chatModel);
        notifyItemChanged(chatList.size()-1);
        if (rv != null) {
            rv.scrollToPosition(chatList.size()-1);
        }
        return chatList.size();
    }
}
