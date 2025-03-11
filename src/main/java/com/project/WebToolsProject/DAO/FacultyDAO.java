package com.project.WebToolsProject.DAO;

import com.project.WebToolsProject.POJO.Course;

import com.project.WebToolsProject.POJO.Faculty;
import com.project.WebToolsProject.POJO.Schedule;
import com.project.WebToolsProject.POJO.User;


import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FacultyDAO {
	

    @Autowired
    private SessionFactory sessionFactory;

    // Fetch Faculty by their email (via the associated User entity)
    public Faculty getFacultyByEmail(String email) {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("FROM Faculty f WHERE f.user.email = :email", Faculty.class)
                          .setParameter("email", email)
                          .uniqueResult();
        } finally {
            session.close();
        }
    }
    public Faculty getFacultyByUserId(Integer userId) {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("FROM Faculty WHERE user.id = :userId", Faculty.class)
                          .setParameter("userId", userId)
                          .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    // Save Faculty
    public void saveFaculty(Faculty faculty) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(faculty);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    // Update Faculty
    public void updateFaculty(Faculty faculty) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(faculty);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void deleteFacultyWithCourses(Integer userId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            // Fetch the faculty
            Faculty faculty = session.createQuery(
                    "FROM Faculty f WHERE f.user.id = :userId", Faculty.class)
                    .setParameter("userId", userId)
                    .uniqueResult();

            if (faculty == null) {
                throw new IllegalStateException("Faculty not found with User ID: " + userId);
            }

            // Fetch and delete all related courses and their schedules
            for (Course course : faculty.getCourses()) {
                // Delete schedules for each course
                List<Schedule> schedules = session.createQuery(
                        "FROM Schedule s WHERE s.course.id = :courseId", Schedule.class)
                        .setParameter("courseId", course.getId())
                        .getResultList();

                for (Schedule schedule : schedules) {
                    session.remove(schedule);
                }

                // Delete the course
                session.remove(course);
            }

            // Delete the faculty
            session.remove(faculty);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error deleting faculty and associated courses: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }


}

    
    

