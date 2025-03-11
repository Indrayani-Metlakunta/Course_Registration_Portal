package com.project.WebToolsProject.DAO;

import com.project.WebToolsProject.POJO.Course;
import com.project.WebToolsProject.POJO.Registration;
import com.project.WebToolsProject.POJO.RegistrationStatus;
import com.project.WebToolsProject.POJO.Student;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class StudentDAO {

    @Autowired
    private SessionFactory sessionFactory;

    // Save a new student record
    public void saveStudent(Student student) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(student);
            transaction.commit();
        }
    }

    
    public Student getStudentByUserId(Integer userId) {
        Session session = sessionFactory.openSession();
        try {
            Student student = session.createQuery("FROM Student WHERE user.id = :userId", Student.class)
                                     .setParameter("userId", userId)
                                     .uniqueResult();
            System.out.println("Fetched student: " + student);
            return student;
        } finally {
            session.close();
        }
    }



    // Retrieve enrolled courses
   
    public List<Course> getEnrolledCourses(Student student) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "SELECT r.course FROM Registration r WHERE r.student = :student", Course.class)
                    .setParameter("student", student)
                    .getResultList();
        }
    }

    public void enrollInCourse(Student student, Course course) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            student = session.merge(student);
            course = session.merge(course);

            if (course.getAvailableSeats() <= 0) {
                throw new IllegalStateException("No seats available.");
            }

            course.setAvailableSeats(course.getAvailableSeats() - 1);
            Registration registration = new Registration(student, course, RegistrationStatus.REGISTERED, new Date());

            session.persist(registration);
            session.merge(course);

            transaction.commit();
        }
    }

    public void dropCourse(Student student, Course course) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            student = session.merge(student);
            course = session.merge(course);

            // Fetch the registration
            Registration registration = session.createQuery(
                            "FROM Registration r WHERE r.student = :student AND r.course = :course", Registration.class)
                    .setParameter("student", student)
                    .setParameter("course", course)
                    .uniqueResult();

            if (registration == null) {
                throw new IllegalStateException("Student is not enrolled in this course.");
            }

            // Remove the registration from both student and course
            student.getRegistrations().remove(registration);
            course.getRegistrations().remove(registration);

            // Increment the available seats in the course
            course.setAvailableSeats(course.getAvailableSeats() + 1);

            // Remove the registration from the database
            session.remove(registration);

            // Update the entities
            session.merge(student);
            session.merge(course);

            transaction.commit();
        }
    }
 // In StudentDAO.java
    public void updateStudent(Student student) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(student); // Use merge to update the student entity
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Failed to update student: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }





   
}
