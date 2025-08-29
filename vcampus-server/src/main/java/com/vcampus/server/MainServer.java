package com.vcampus.server;

import com.vcampus.server.net.ServerSocketManager;

public class MainServer {
    public static void main(String[] args) {
        ServerSocketManager server = new ServerSocketManager(8888);
        server.start();
    }
}
