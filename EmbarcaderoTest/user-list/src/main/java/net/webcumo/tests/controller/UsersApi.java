package net.webcumo.tests.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import net.webcumo.tests.entities.UserEntity;
import net.webcumo.tests.entities.UsersDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(name = "Users API")
public class UsersApi {
    @Autowired
    private UsersDao usersDao;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/users", produces = "application/json")
    public String listEntities() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(new UserList(usersDao.list()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/users/by/{column}")
    public String listEntities(@PathVariable("column") String orderColumn) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(new UserList(usersDao.list(orderColumn.split(","))));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class UserList {
        @Getter
        private final List<UserEntity> entities;

        public UserList(List entities) {
            this.entities = new ArrayList<>(entities.size());
            for (Object entity: entities) {
                if (entity instanceof UserEntity) {
                    this.entities.add((UserEntity) entity);
                }
            }
        }
    }
}
