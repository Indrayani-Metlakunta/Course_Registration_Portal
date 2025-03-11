package com.project.WebToolsProject.DAO;

import com.project.WebToolsProject.POJO.Role;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RoleDAO {

    @Autowired
    private SessionFactory sessionFactory;

    // Retrieve or create a role by name
    public Role getOrCreateRoleByName(String roleName) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            Query<Role> query = session.createQuery("FROM Role WHERE name = :roleName", Role.class);
            query.setParameter("roleName", roleName);
            Role role = query.uniqueResult();

            if (role == null) {
                // If role doesn't exist, create it
                role = new Role();
                role.setName(roleName);
                session.persist(role);
            }

            transaction.commit();
            return role;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    // Retrieve all roles
    public List<Role> getAllRoles() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("FROM Role", Role.class).list();
        } finally {
            session.close();
        }
    }
}
