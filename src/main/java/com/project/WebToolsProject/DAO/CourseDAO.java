package com.project.WebToolsProject.DAO;

import com.project.WebToolsProject.POJO.Course;
import com.project.WebToolsProject.POJO.Faculty;
import com.project.WebToolsProject.POJO.Registration;
import com.project.WebToolsProject.POJO.Schedule;
import com.project.WebToolsProject.POJO.Student;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CourseDAO {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ScheduleDAO scheduleDAO;

    @Autowired
    private RegistrationDAO registrationDAO;

    // Save a new course
    public void saveCourse(Course course) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(course);
            transaction.commit();
        }
    }

    // Retrieve all courses taught by a faculty
    public List<Course> getCoursesByFaculty(Faculty faculty) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Course WHERE faculty = :faculty", Course.class)
                    .setParameter("faculty", faculty)
                    .list();
        }
    }

    // Retrieve a course by its ID
    public Course getCourseById(Integer courseId) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(Course.class, courseId);
        } finally {
            session.close();
        }
    }
    // Fetch only approved courses
    public List<Course> getApprovedCourses() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Course WHERE status = 'Approved'", Course.class).list();
        }
    }

    // Retrieve pending courses
    public List<Course> getPendingCourses() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Course WHERE status = 'Pending'", Course.class).list();
        }
    }



    // Retrieve courses by status
    public List<Course> getCoursesByStatus(String status) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Course WHERE status = :status", Course.class)
                    .setParameter("status", status)
                    .list();
        }
    }
    //new
    public void updateCourse(Course course) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(course);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    // Delete a course by its ID
  
    public void deleteCourse(Integer courseId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            // Fetch the course by its ID
            Course course = session.createQuery("FROM Course WHERE id = :courseId", Course.class)
                                   .setParameter("courseId", courseId)
                                   .uniqueResult();

            if (course == null) {
                throw new IllegalStateException("Course not found with ID: " + courseId);
            }

            // Remove the course from the faculty's course list
            Faculty faculty = course.getFaculty();
            if (faculty != null) {
                faculty.getCourses().remove(course);
                session.merge(faculty); // Update the faculty in the database
            }

            // Delete schedules and registrations associated with the course
            scheduleDAO.deleteSchedulesByCourseId(courseId);
            registrationDAO.deleteRegistrationsByCourseId(courseId);

            // Delete the course
            session.remove(course);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error deleting course: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }
    // Retrieve registrations for a list of courses
    public List<Registration> getRegistrationsForCourses(List<Course> courses) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM Registration r WHERE r.course IN (:courses)", Registration.class)
                    .setParameter("courses", courses)
                    .getResultList();
        }
    }
}
