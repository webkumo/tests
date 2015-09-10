package net.webcumo.tests.entities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Service
public class InitUsers {
    private static final ThreadLocal<DateFormat> FORMATTER =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("MM/dd/yyyy"));

    @Autowired
    public void setUsersDao(UsersDao usersDao) {
        URL resource = getClass().getClassLoader().getResource("userdata.txt");
        if (resource == null) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.openStream()))) {
            br.lines().map(this::parseString).forEach(usersDao::save);
        } catch (IOException e) {
            //TODO add log
            e.printStackTrace();
        }
        usersDao.list().forEach(System.out::println);
    }

    private UserEntity parseString(String string) {
        String[] strings = string.split(",");
        if (strings.length < 6) {
            throw new IllegalArgumentException("Wrong parameters count in users list");
        }
        UserEntity entity = new UserEntity();
        entity.setFirstName(strings[0].trim());
        entity.setLastName(strings[1].trim());
        try {
            entity.setDate(FORMATTER.get().parse(strings[2].trim()));
        } catch (ParseException e) {
            //TODO add log
            e.printStackTrace();
            return null;
        }
        entity.setLoginName(strings[3].trim());
        entity.setPassword(strings[4].trim());
        entity.setEnabled(Boolean.parseBoolean(strings[5].trim()));
        return entity;
    }
}
