package com.project.WebToolsProject.DAO;

import com.project.WebToolsProject.POJO.User;
import com.project.WebToolsProject.POJO.Student;
import com.project.WebToolsProject.POJO.Faculty;
import com.project.WebToolsProject.POJO.Registration;
import com.project.WebToolsProject.POJO.Role;
import com.project.WebToolsProject.POJO.Admin;
import com.project.WebToolsProject.POJO.Course;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    // Save a new user (for registration)
    public boolean saveUser(User user) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.persist(user); // Save user
            transaction.commit();
            logger.info("User saved successfully: " + user.getEmail());
            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error saving user: ", e);
            return false;
        } finally {
            if (session != null) {
                session.close();
                logger.debug("Session closed after saving user.");
            }
        }
    }

    // Retrieve user by email (for login)
    public User getUserByEmail(String email) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            User user = query.uniqueResult();
            logger.info("User retrieved successfully: " + email);
            return user;
        } catch (HibernateException e) {
            logger.error("Error retrieving user by email: ", e);
            return null;
        } finally {
            if (session != null) {
                session.close();
                logger.debug("Session closed after retrieving user by email.");
            }
        }
    }

    // Retrieve all users
    public List<User> getAllUsers() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("FROM User", User.class).list();
        } finally {
            session.close();
        }
    }

    
    public void deleteUserAndAssociatedRecords(Integer userId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            // Fetch the User by ID
            User user = session.get(User.class, userId);
            if (user == null) {
                throw new IllegalStateException("User not found with ID: " + userId);
            }

            // Check user role and delete associated records
            if (user.getRole() != null) {
                String roleName = user.getRole().getName().toLowerCase();

                switch (roleName) {
                    case "faculty":
                        // Delete Faculty and their associated courses
                        Faculty faculty = session.createQuery(
                                "FROM Faculty WHERE user.id = :userId", Faculty.class)
                                .setParameter("userId", userId)
                                .uniqueResult();
                        if (faculty != null) {
                            // Delete associated courses
                            for (Course course : faculty.getCourses()) {
                                session.remove(course);
                            }
                            session.remove(faculty);
                        }
                        break;

                    case "student":
                        // Delete Student and their registrations
                        Student student = session.createQuery(
                                "FROM Student WHERE user.id = :userId", Student.class)
                                .setParameter("userId", userId)
                                .uniqueResult();
                        if (student != null) {
                            for (Registration registration : student.getRegistrations()) {
                                session.remove(registration);
                            }
                            session.remove(student);
                        }
                        break;

                    case "admin":
                        // Delete Admin (if needed)
                        Admin admin = session.createQuery(
                                "FROM Admin WHERE user.id = :userId", Admin.class)
                                .setParameter("userId", userId)
                                .uniqueResult();
                        if (admin != null) {
                            session.remove(admin);
                        }
                        break;

                    default:
                        throw new IllegalStateException("Unsupported role: " + roleName);
                }
            }

            // Finally, delete the user record
            session.remove(user);

            // Commit the transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error deleting user and associated records: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }


    // Fetch a user by ID
    public User getUserById(Integer userId) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(User.class, userId);
        } finally {
            session.close();
        }
    }
    public List<Role> getAllRoles() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("FROM Role", Role.class).list();
        } finally {
            session.close();
        }
    }
    public Role getRoleById(Integer roleId) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(Role.class, roleId);
        } finally {
            session.close();
        }
    }



    // Update user details
    public void updateUser(User user) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(user); // Update user details
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error updating user: ", e);
            throw e;
        } finally {
            session.close();
        }
    }
    
    
}
