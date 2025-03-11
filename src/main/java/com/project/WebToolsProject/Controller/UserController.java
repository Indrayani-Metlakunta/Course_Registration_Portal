package com.project.WebToolsProject.Controller;

import com.project.WebToolsProject.DAO.*;
import com.project.WebToolsProject.POJO.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private FacultyDAO facultyDAO;

    @Autowired
    private StudentDAO studentDAO;

    @Autowired
    private AdminDAO adminDAO;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    // Display registration form
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Handle registration
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            // Set the registration date
            user.setRegistrationDate(new Date());

            // Encrypt the password
            String encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);

            // Fetch or create the role
            Role role = roleDAO.getOrCreateRoleByName(user.getRoleName());
            if (role == null) {
                model.addAttribute("error", "Invalid role selected.");
                return "register"; // Redirect back to the registration page with an error message
            }

            // Assign the role to the user
            user.setRole(role);

            // Save the user to the database
            userDAO.saveUser(user);

            // Save role-specific details
            switch (role.getName().toLowerCase()) {
                case "student":
                    Student student = new Student();
                    student.setUser(user); // Link with user
                    student.setEnrollmentYear(2024); // Example field
                    student.setMajor("Computer Science"); // Example field
                    studentDAO.saveStudent(student);
                    break;
                    

                case "faculty":
                    Faculty faculty = new Faculty();
                    faculty.setUser(user); // Link with user
                    faculty.setDepartment("Default Department"); // Example field
                    faculty.setPosition("Assistant Professor"); // Example field
                    facultyDAO.saveFaculty(faculty);
                    break;

                case "admin":
                    Admin admin = new Admin();
                    admin.setUser(user); // Link with user
                    admin.setAdminLevel(1); // Example field
                    adminDAO.saveAdmin(admin);
                    break;

                default:
                    throw new IllegalArgumentException("Invalid role specified: " + role.getName());
            }

            // Redirect to login after successful registration
            return "redirect:/user/login";

        } catch (Exception e) {
            model.addAttribute("error", "An error occurred during registration: " + e.getMessage());
            return "register"; // Redirect back to the registration page with an error message
        }
    }

    // Display login form
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        User user = userDAO.getUserByEmail(email);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // Set the logged-in user in the session
            session.setAttribute("loggedInUser", user);
            System.out.println("User logged in: " + user);

            // Check the role of the user
            if ("student".equalsIgnoreCase(user.getRole().getName())) {
                Student student = studentDAO.getStudentByUserId(user.getId());
                if (student != null) {
                    session.setAttribute("student", student); // Add Student to session
                    System.out.println("Student added to session: " + student);
                    return "redirect:/student/dashboard";
                } else {
                    model.addAttribute("error", "Student record not found for the user.");
                    return "login";
                }
            } else if ("faculty".equalsIgnoreCase(user.getRole().getName())) {
                Faculty faculty = facultyDAO.getFacultyByUserId(user.getId());
                if (faculty != null) {
                    session.setAttribute("faculty", faculty); // Add Faculty to session
                    System.out.println("Faculty added to session: " + faculty);
                    return "redirect:/faculty/dashboard";
                } else {
                    model.addAttribute("error", "Faculty record not found for the user.");
                    return "login";
                }
            } else if ("admin".equalsIgnoreCase(user.getRole().getName())) {
                Admin admin = adminDAO.getAdminByUserId(user.getId());
                if (admin != null) {
                    session.setAttribute("admin", admin); // Add Admin to session
                    System.out.println("Admin added to session: " + admin);
                    return "redirect:/admin/dashboard";
                } else {
                    model.addAttribute("error", "Admin record not found for the user.");
                    return "login";
                }
            } else {
                model.addAttribute("error", "Invalid role assigned to the user.");
                return "login";
            }
        }

        // If the user does not exist or password is invalid
        model.addAttribute("error", "Invalid email or password.");
        return "login";
    }

    @GetMapping("/check-session")
    public String checkSession(HttpSession session) {
        System.out.println("Session attributes: " + session.getAttribute("student"));
        return "check-session";
    }

        
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/user/login?logout=true";
    }
    
    //new ones
 // Add methods to display and update password
    @GetMapping("/update-password")
    public String showUpdatePasswordForm(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("user", loggedInUser);
        return "update_password";
    }

    @PostMapping("/update-password")
    public String updatePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 HttpSession session,
                                 Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }

        // Check if old password matches
        if (!passwordEncoder.matches(oldPassword, loggedInUser.getPassword())) {
            model.addAttribute("error", "Incorrect old password");
            return "update_password";
        }

        // Update password
        String encryptedPassword = passwordEncoder.encode(newPassword);
        loggedInUser.setPassword(encryptedPassword);
        userDAO.updateUser(loggedInUser);

        model.addAttribute("message", "Password updated successfully!");
        return "update_password";
    }

}
