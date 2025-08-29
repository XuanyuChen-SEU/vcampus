package com.vcampus.server.net;

import com.vcampus.common.dto.Message;

public interface IMessageServerSrv {
    boolean send(Message msg);
    Message receive();
    boolean validate(Message msg);
    void broadcast(Message msg);
}
