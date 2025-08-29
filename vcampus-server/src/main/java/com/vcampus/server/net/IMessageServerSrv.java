package com.vcampus.server.net;

public interface IMessageServerSrv {
    boolean send(Message msg);
    Message receive();
    boolean validate(Message msg);
    void broadcast(Message msg);
}
