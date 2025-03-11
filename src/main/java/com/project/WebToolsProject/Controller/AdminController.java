package com.project.WebToolsProject.Controller;

import com.project.WebToolsProject.DAO.CourseDAO;
import com.project.WebToolsProject.DAO.FacultyDAO;
import com.project.WebToolsProject.DAO.StudentDAO;
import com.project.WebToolsProject.DAO.UserDAO;
import com.project.WebToolsProject.POJO.Course;
import com.project.WebToolsProject.POJO.Faculty;
import com.project.WebToolsProject.POJO.Role;
import com.project.WebToolsProject.POJO.Student;
import com.project.WebToolsProject.POJO.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private CourseDAO courseDAO;
    
    @Autowired // Add this annotation
    private FacultyDAO facultyDAO;
    @Autowired // Add this annotation
    private StudentDAO studentDAO;

    // Admin dashboard
    @GetMapping("/dashboard")
    public String adminDashboard(Model model, HttpSession session) {
        List<Course> pendingCourses = courseDAO.getPendingCourses();
        model.addAttribute("pendingCourses", pendingCourses);
        return "admin_dashboard";
    }

    // Approve a course
    @PostMapping("/approve-course")
    public String approveCourse(@RequestParam("courseId") Integer courseId) {
        Course course = courseDAO.getCourseById(courseId);
        if (course != null) {
            course.setStatus("Approved");
            courseDAO.updateCourse(course);
        }
        return "redirect:/admin/dashboard";
    }

    // Reject a course
    @PostMapping("/reject-course")
    public String rejectCourse(@RequestParam("courseId") Integer courseId) {
        Course course = courseDAO.getCourseById(courseId);
        if (course != null) {
            course.setStatus("Rejected");
            courseDAO.updateCourse(course);
        }
        return "redirect:/admin/dashboard";
    }

 // Display approved courses
    @GetMapping("/approved-courses")
    public String showApprovedCourses(Model model) {
        List<Course> approvedCourses = courseDAO.getCoursesByStatus("Approved");
        model.addAttribute("courses", approvedCourses);
        return "approved_courses";
    }

    // Display rejected courses
    @GetMapping("/rejected-courses")
    public String showRejectedCourses(Model model) {
        List<Course> rejectedCourses = courseDAO.getCoursesByStatus("Rejected");
        model.addAttribute("courses", rejectedCourses);
        return "rejected_courses";
    }


    // Manage users
    @GetMapping("/manage-users")
    public String manageUsers(Model model) {
        List<User> users = userDAO.getAllUsers();
        model.addAttribute("users", users);
        return "manage_users";
    }

    @PostMapping("/delete-user")
    public String deleteUser(@RequestParam("userId") Integer userId, Model model) {
        try {
            userDAO.deleteUserAndAssociatedRecords(userId); // Call the DAO method
            model.addAttribute("successMessage", "User deleted successfully.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error deleting user: " + e.getMessage());
        }

        // Reload the user list for the UI
        List<User> users = userDAO.getAllUsers();
        model.addAttribute("users", users);
        return "manage_users";
    }




   

    // Edit a user
   
    @GetMapping("/edit-user")
    public String editUser(@RequestParam("userId") Integer userId, Model model) {
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new IllegalStateException("User not found for ID: " + userId);
        }
        List<Role> roles = userDAO.getAllRoles(); // Ensure this method exists in UserDAO
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "edit_user";
    }

    @PostMapping("/save-user")
    public String saveUser(@ModelAttribute User user, @RequestParam("roleId") Integer roleId) {
        Role role = userDAO.getRoleById(roleId);
        if (role == null) {
            throw new IllegalStateException("Role not found for ID: " + roleId);
        }

        User existingUser = userDAO.getUserById(user.getId());
        if (existingUser == null) {
            throw new IllegalStateException("User not found for ID: " + user.getId());
        }

        // Update fields in the existing user
        existingUser.setEmail(user.getEmail());
        existingUser.setFullName(user.getFullName());
        existingUser.setPassword(user.getPassword()); // Ensure password is updated
        existingUser.setRole(role);
        userDAO.updateUser(existingUser);

        // Handle faculty-specific updates
        if ("faculty".equalsIgnoreCase(role.getName())) {
            Faculty faculty = facultyDAO.getFacultyByUserId(existingUser.getId());
            if (faculty != null) {
                faculty.setDepartment("Updated Department");
                facultyDAO.updateFaculty(faculty);
            }
        }

        // Handle student-specific updates
        else if ("student".equalsIgnoreCase(role.getName())) {
            Student student = studentDAO.getStudentByUserId(existingUser.getId());
            if (student != null) {
                student.setMajor("Updated Major");
                studentDAO.updateStudent(student);
            }
        }

        return "redirect:/admin/manage-users";
    }

}
