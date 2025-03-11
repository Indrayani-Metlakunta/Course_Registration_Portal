package com.project.WebToolsProject.DAO;

import com.project.WebToolsProject.POJO.Course;
import com.project.WebToolsProject.POJO.Registration;
import com.project.WebToolsProject.POJO.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class RegistrationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    // Enroll a student in a course
    public void enrollStudentInCourse(Student student, Course course) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            // Fetch student and course to ensure they are attached to the session
            student = session.merge(student);
            course = session.merge(course);

            // Check if the student is already enrolled
            if (student.getCourses().contains(course)) {
                throw new IllegalStateException("Student is already enrolled in this course.");
            }

            // Decrease available seats
            if (course.getAvailableSeats() > 0) {
                course.setAvailableSeats(course.getAvailableSeats() - 1);
            } else {
                throw new IllegalStateException("No seats available for this course.");
            }

            // Add the course to the student's courses
            student.getCourses().add(course);

            // Persist the changes
            session.merge(course);
            session.merge(student);

            transaction.commit();
        }
    }

    // Drop a student from a course
    public void dropStudentFromCourse(Student student, Course course) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            // Fetch student and course to ensure they are attached to the session
            student = session.merge(student);
            course = session.merge(course);

            // Remove the course from the student's courses
            if (!student.getCourses().remove(course)) {
                throw new IllegalStateException("Student is not enrolled in this course.");
            }

            // Increment available seats
            course.setAvailableSeats(course.getAvailableSeats() + 1);

            // Persist the changes
            session.merge(course);
            session.merge(student);

            transaction.commit();
        }
    }

    // Fetch enrolled courses for a student
    public List<Course> getEnrolledCoursesForStudent(Student student) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "SELECT c FROM Course c JOIN c.students s WHERE s = :student", Course.class)
                    .setParameter("student", student)
                    .getResultList();
        }
    }

    // Fetch registrations for a list of courses
    public List<Registration> getRegistrationsForCourses(List<Course> courses) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "FROM Registration r WHERE r.course IN :courses", Registration.class)
                    .setParameter("courses", courses)
                    .list();
        }
    }
    public void deleteRegistrationsByCourseId(Integer courseId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            List<Registration> registrations = session.createQuery("FROM Registration r WHERE r.course.id = :courseId", Registration.class)
                                                      .setParameter("courseId", courseId)
                                                      .list();
            for (Registration registration : registrations) {
                session.remove(registration);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error deleting registrations for course ID: " + courseId, e);
        } finally {
            session.close();
        }
    }

}
