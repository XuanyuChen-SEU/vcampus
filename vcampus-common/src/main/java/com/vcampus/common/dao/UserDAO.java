package com.vcampus.common.dao;

import com.vcampus.common.dto.User;
import java.util.List;

public interface UserDAO {
    User findById(int id);
    User findByUsername(String username);
    List<User> findAll();
    boolean insert(User user);
    boolean update(User user);
    boolean delete(int id);
}
