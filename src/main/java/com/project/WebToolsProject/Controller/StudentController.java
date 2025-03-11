package com.project.WebToolsProject.Controller;

import com.project.WebToolsProject.DAO.CourseDAO;
import com.project.WebToolsProject.DAO.StudentDAO;
import com.project.WebToolsProject.DAO.UserDAO;
import com.project.WebToolsProject.POJO.Course;
import com.project.WebToolsProject.POJO.Student;
import com.project.WebToolsProject.POJO.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentDAO studentDAO;

    @Autowired
    private CourseDAO courseDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    // Helper method to retrieve student from session
    private Student getStudentFromSession(HttpSession session) {
        Student student = (Student) session.getAttribute("student");
        if (student == null) {
            throw new IllegalStateException("Student not found in session");
        }
        
        return student;
    }
    

    @GetMapping("/dashboard")
    public String studentDashboard(Model model, HttpSession session) {
        Student student = getStudentFromSession(session);

        List<Course> availableCourses = courseDAO.getApprovedCourses();
        List<Course> enrolledCourses = studentDAO.getEnrolledCourses(student);
        model.addAttribute("student", student);

        model.addAttribute("availableCourses", availableCourses);
        model.addAttribute("enrolledCourses", enrolledCourses);
        return "student_dashboard";
    }

    // Handle course enrollment
    @PostMapping("/enroll")
    public String enrollStudent(@RequestParam("courseId") Integer courseId, HttpSession session, Model model) {
        Student student = getStudentFromSession(session);

        Course course = courseDAO.getCourseById(courseId);
        if (course == null) {
            model.addAttribute("errorMessage", "Course not found.");
            model.addAttribute("backUrl", "/student/dashboard");
            return "error_page";
        }

        try {
            List<Course> enrolledCourses = studentDAO.getEnrolledCourses(student);

            // Check for duplicate enrollment
            if (enrolledCourses.stream().anyMatch(enrolledCourse -> enrolledCourse.getId().equals(course.getId()))) {
                return "duplicated_course"; // Redirect to duplicate enrollment page
            }

            // Check for time conflicts
            for (Course enrolledCourse : enrolledCourses) {
                if (enrolledCourse.getDay().equals(course.getDay()) &&
                    enrolledCourse.getFromTime().compareTo(course.getToTime()) < 0 &&
                    enrolledCourse.getToTime().compareTo(course.getFromTime()) > 0) {
                    return "time_conflict"; // Redirect to time conflict page
                }
            }

            // Enroll the student
            studentDAO.enrollInCourse(student, course);
            model.addAttribute("message", "Enrollment successful!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred during enrollment: " + e.getMessage());
            model.addAttribute("backUrl", "/student/dashboard");
            return "error_page";
        }

        return "redirect:/student/dashboard";
    }

    // Handle course drop
    @PostMapping("/drop")
    public String dropCourse(@RequestParam("courseId") Integer courseId, HttpSession session, Model model) {
        Student student = getStudentFromSession(session);

        Course course = courseDAO.getCourseById(courseId);
        if (course == null) {
            model.addAttribute("errorMessage", "Course not found.");
            model.addAttribute("backUrl", "/student/dashboard");
            return "error_page";
        }

        try {
            studentDAO.dropCourse(student, course);
            model.addAttribute("message", "Successfully dropped the course.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while dropping the course: " + e.getMessage());
            model.addAttribute("backUrl", "/student/dashboard");
            return "error_page";
        }

        return "redirect:/student/dashboard";
    }
    //new ones
 // Show student profile update form
    @GetMapping("/update-profile")
    public String showUpdateProfileForm(Model model, HttpSession session) {
        Student student = (Student) session.getAttribute("student");
        if (student == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("student", student);
        model.addAttribute("updatePassword", false); // Default to not showing password fields
        return "student_update_profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(
            @ModelAttribute Student student,
            @RequestParam(required = false) String currentPassword,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String confirmPassword,
            HttpSession session,
            Model model) {
        try {
            Student currentStudent = (Student) session.getAttribute("student");
            if (currentStudent == null) {
                return "redirect:/user/login";
            }

            // Update profile details
            currentStudent.setMajor(student.getMajor());
            currentStudent.setEnrollmentYear(student.getEnrollmentYear());
            studentDAO.updateStudent(currentStudent);

            // Handle password update
            if (currentPassword != null && !currentPassword.isEmpty()) {
                User user = currentStudent.getUser();

                // Verify current password
                if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                    model.addAttribute("error", "Current password is incorrect.");
                    return "student_update_profile";
                }

                // Validate new password
                if (!newPassword.equals(confirmPassword)) {
                    model.addAttribute("error", "New password and confirm password do not match.");
                    return "update_student_profile";
                }

                // Update password
                user.setPassword(passwordEncoder.encode(newPassword));
                userDAO.updateUser(user); // Persist password change
            }

            session.setAttribute("student", currentStudent); // Update session
            model.addAttribute("message", "Profile updated successfully!");

        } catch (Exception e) {
            model.addAttribute("error", "Error updating profile: " + e.getMessage());
        }
        return "student_update_profile";
    }



}
