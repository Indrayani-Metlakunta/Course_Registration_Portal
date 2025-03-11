package com.project.WebToolsProject.DAO;

import com.project.WebToolsProject.POJO.Schedule;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ScheduleDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public void deleteSchedulesByCourseId(Integer courseId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            List<Schedule> schedules = session.createQuery("FROM Schedule s WHERE s.course.id = :courseId", Schedule.class)
                                              .setParameter("courseId", courseId)
                                              .list();
            for (Schedule schedule : schedules) {
                session.remove(schedule);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error deleting schedules for course ID: " + courseId, e);
        } finally {
            session.close();
        }
    }
}
