package com.vcampus.server.dao.impl;

import com.vcampus.common.dao.IUserDao;
import com.vcampus.common.dto.User;
import com.vcampus.server.service.DBService;

import java.sql.*;
import java.util.*;
public class UserDao implements IUserDao {

    public DBService dbService ;
    public UserDao() {
        IUDInit();
    }


    @Override
    public void IUDInit()
    {
        dbService= new DBService();
        dbService.initialize();
    }
    @Override
    public User getUserById(String id)
    {
        return dbService.searchRecordByField(dbService.dbManager, "userId", id);
    }


    @Override
    public boolean updateUser(User user) {
        // 七位用户ID（使用String避免首位0丢失）
        String userId=user.getUserId();
        // 加密后的密码（传输为明文和存储为密文）
        String password=user.getPassword();
        return dbService.updateRecordByField(dbService.dbManager, "password", password,"userId", userId );
    }

    @Override
    public boolean deleteUser(User user) {
        
        return false;
    }

}