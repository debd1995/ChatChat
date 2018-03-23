package com.wgt.chatchat;

import java.net.Socket;

/**
 * Created by debasish on 23-03-2018.
 */

public class SocketHandler {
    private static Socket socket;

    public static Socket getSocket() {
        return socket;
    }
    public static void setSocket(Socket socket) {
        SocketHandler.socket = socket;
    }
}
