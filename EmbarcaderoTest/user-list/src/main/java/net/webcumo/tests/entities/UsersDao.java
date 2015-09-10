package net.webcumo.tests.entities;

import java.util.List;

public interface UsersDao {
    void save(UserEntity entity);
    List<UserEntity> list();
    List<UserEntity> list(String... order);
}
