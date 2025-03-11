package com.project.WebToolsProject.DAO;

import com.project.WebToolsProject.POJO.Admin;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AdminDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Admin getAdminByUserId(Integer userId) {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("FROM Admin WHERE user.id = :userId", Admin.class)
                          .setParameter("userId", userId)
                          .uniqueResult();
        } finally {
            session.close();
        }
    }

    public void saveAdmin(Admin admin) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(admin);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
