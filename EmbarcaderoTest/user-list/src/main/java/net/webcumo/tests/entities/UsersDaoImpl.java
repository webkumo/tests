package net.webcumo.tests.entities;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsersDaoImpl implements UsersDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(UserEntity entity) {
        if (entity == null) {
            return;
        }
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.save(entity);
        tx.commit();
        session.close();
    }

    @Override
    public List<UserEntity> list() {
        return list(new String[0]);
    }

    @Override
    public List<UserEntity> list(String... order) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Criteria criteria = session.createCriteria(UserEntity.class);
        Arrays.stream(order).map(Order::asc).forEach(criteria::addOrder);
        @SuppressWarnings("unchecked")
        List<UserEntity> list = new ArrayList<>((List<UserEntity>)criteria.list());
        tx.commit();
        session.close();
        return list;
    }
}
