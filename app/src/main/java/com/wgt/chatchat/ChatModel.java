package com.wgt.chatchat;

/**
 * Created by debasish on 23-03-2018.
 */

public class ChatModel {
    private String msg;
    private boolean isMe;

    public ChatModel(String msg, boolean isMe) {
        this.msg = msg;
        this.isMe = isMe;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean getIsMe() {
        return isMe;
    }

    public void setIsMe(boolean me) {
        isMe = me;
    }
}
