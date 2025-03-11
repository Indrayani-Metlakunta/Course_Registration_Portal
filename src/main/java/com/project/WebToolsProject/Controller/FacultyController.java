//package com.project.WebToolsProject.Controller;
//
//import com.project.WebToolsProject.DAO.CourseDAO;
//import com.project.WebToolsProject.DAO.FacultyDAO;
//import com.project.WebToolsProject.DAO.RegistrationDAO;
//import com.project.WebToolsProject.POJO.Course;
//import com.project.WebToolsProject.POJO.Faculty;
//import com.project.WebToolsProject.POJO.Registration;
//import com.project.WebToolsProject.POJO.User;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Controller
//@RequestMapping("/faculty")
//public class FacultyController {
//
//    @Autowired
//    private FacultyDAO facultyDAO;
//
//    @Autowired
//    private CourseDAO courseDAO;
//
//    @Autowired
//    private RegistrationDAO registrationDAO;
//
//    // Display faculty dashboard
//    @GetMapping("/dashboard")
//    public String facultyDashboard(Model model, HttpSession session) {
//        User loggedInUser = (User) session.getAttribute("loggedInUser");
//        if (loggedInUser == null) {
//            return "redirect:/user/login"; // Redirect to login if not logged in
//        }
//
//        Faculty faculty = facultyDAO.getFacultyByEmail(loggedInUser.getEmail());
//        if (faculty != null) {
//            List<Course> courses = courseDAO.getCoursesByFaculty(faculty);
//            model.addAttribute("courses", courses);
//        }
//        model.addAttribute("faculty", faculty);
//
//        model.addAttribute("course", new Course());
//        return "faculty_dashboard";
//    }
//
//    // Add a new course
//    @PostMapping("/add-course")
//    public String addCourse(@ModelAttribute Course course, HttpSession session, Model model) {
//        User loggedInUser = (User) session.getAttribute("loggedInUser");
//        if (loggedInUser == null) {
//            return "redirect:/user/login"; // Redirect to login if not logged in
//        }
//
//        Faculty faculty = facultyDAO.getFacultyByEmail(loggedInUser.getEmail());
//        if (faculty == null) {
//            throw new IllegalStateException("Faculty not found for logged-in user");
//        }
//
//        // Associate course with faculty
//        course.setFaculty(faculty);
//        course.setStatus("Pending"); // Default status
//        course.setAvailableSeats(course.getMaxSeats()); // Ensure availableSeats is set
//
//        courseDAO.saveCourse(course); // Save course
//        return "course_success"; // Redirect to dashboard
//    }
//
//
//    // Edit course details
//    @GetMapping("/edit-course/{id}")
//    public String editCourse(@PathVariable("id") Integer id, Model model) {
//        Course course = courseDAO.getCourseById(id);
//        model.addAttribute("course", course);
//        return "edit_course"; // Create a separate template for editing courses
//    }
//
//    @PostMapping("/update-course")
//    public String updateCourse(@ModelAttribute Course course, HttpSession session) {
//        // Fetch the logged-in user from the session
//        User loggedInUser = (User) session.getAttribute("loggedInUser");
//
//        if (loggedInUser == null || !"faculty".equalsIgnoreCase(loggedInUser.getRole().getName())) {
//            throw new IllegalStateException("Logged-in user is not authorized as faculty.");
//        }
//
//        // Fetch the faculty associated with the logged-in user
//        Faculty faculty = facultyDAO.getFacultyByUserId(loggedInUser.getId());
//        if (faculty == null) {
//            throw new IllegalStateException("Faculty record not found for logged-in user.");
//        }
//
//        // Associate the course with the faculty
//        course.setFaculty(faculty);
//
//        // Fetch the existing course from the database
//        Course existingCourse = courseDAO.getCourseById(course.getId());
//
//        // Check if maxSeats has increased and update availableSeats accordingly
//     // Check if maxSeats has increased and update availableSeats accordingly
//        if (existingCourse != null && course.getMaxSeats() > existingCourse.getMaxSeats()) {
//            // Calculate the difference in maxSeats
//            int maxSeatsDifference = course.getMaxSeats() - existingCourse.getMaxSeats();
//
//            // Update availableSeats by adding the difference
//            course.setAvailableSeats(existingCourse.getAvailableSeats() + maxSeatsDifference);
//        }
//
//
//        // Ensure availableSeats is set
//        if (course.getAvailableSeats() == null) {
//            course.setAvailableSeats(course.getMaxSeats());
//        }
//
//        // Ensure status is not null
//        if (course.getStatus() == null) {
//            course.setStatus("Pending");
//        }
//
//        // Update the course in the database
//        courseDAO.updateCourse(course);
//
//        // Redirect to the faculty dashboard after successful update
//        return "redirect:/faculty/dashboard";
//    }
//
//    // Display enrolled students
//    @GetMapping("/enrolled-students")
//    public String showEnrolledStudents(Model model, HttpSession session) {
//        User loggedInUser = (User) session.getAttribute("loggedInUser");
//        if (loggedInUser == null) {
//            return "redirect:/user/login";
//        }
//
//        Faculty faculty = facultyDAO.getFacultyByUserId(loggedInUser.getId());
//        if (faculty == null) {
//            throw new IllegalStateException("Faculty not found for logged-in user");
//        }
//
//        // Fetch courses taught by this faculty
//        List<Course> courses = courseDAO.getCoursesByFaculty(faculty);
//
//        // Fetch registrations for these courses
//        List<Registration> registrations = registrationDAO.getRegistrationsForCourses(courses);
//
//        model.addAttribute("registrations", registrations);
//        return "enrolled_students"; // Create this Thymeleaf template
//    }
//   //new
//        @PostMapping("/delete-course")
//        public String deleteCourse(@RequestParam("courseId") Integer courseId, HttpSession session) {
//            // Fetch the logged-in user from the session
//            User loggedInUser = (User) session.getAttribute("loggedInUser");
//
//            if (loggedInUser == null || !"faculty".equalsIgnoreCase(loggedInUser.getRole().getName())) {
//                return "redirect:/user/login";
//            }
//
//            // Fetch the faculty associated with the logged-in user
//            Faculty faculty = facultyDAO.getFacultyByUserId(loggedInUser.getId());
//            if (faculty == null) {
//                throw new IllegalStateException("Faculty record not found for logged-in user.");
//            }
//
//            // Delete the course
//            courseDAO.deleteCourse(courseId);
//
//            // Redirect to the faculty dashboard after successful deletion
//            return "redirect:/faculty/dashboard";
//        }
//    
//}


package com.project.WebToolsProject.Controller;

import com.project.WebToolsProject.DAO.CourseDAO;
import com.project.WebToolsProject.DAO.FacultyDAO;
import com.project.WebToolsProject.DAO.RegistrationDAO;
import com.project.WebToolsProject.DAO.UserDAO;
import com.project.WebToolsProject.POJO.Course;
import com.project.WebToolsProject.POJO.Faculty;
import com.project.WebToolsProject.POJO.Registration;
import com.project.WebToolsProject.POJO.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/faculty")
public class FacultyController {

    @Autowired
    private FacultyDAO facultyDAO;

    @Autowired
    private CourseDAO courseDAO;

    @Autowired
    private RegistrationDAO registrationDAO;
    @Autowired
    private PasswordEncoder passwordEncoder; // Inject PasswordEncoder
    @Autowired
    private UserDAO userDAO;

    // Display faculty dashboard
    @GetMapping("/dashboard")
    public String facultyDashboard(Model model, HttpSession session) {
        Faculty faculty = (Faculty) session.getAttribute("faculty");
        if (faculty == null) {
            return "redirect:/user/login"; // Redirect to login if not logged in
        }

        List<Course> courses = courseDAO.getCoursesByFaculty(faculty);
        model.addAttribute("courses", courses);
        model.addAttribute("faculty", faculty);
        model.addAttribute("course", new Course());
        return "faculty_dashboard";
    }

    // Add a new course
    @PostMapping("/add-course")
    public String addCourse(@ModelAttribute Course course, HttpSession session) {
        Faculty faculty = (Faculty) session.getAttribute("faculty");
        if (faculty == null) {
            return "redirect:/user/login"; // Redirect to login if not logged in
        }

        course.setFaculty(faculty);
        course.setStatus("Pending");
        course.setAvailableSeats(course.getMaxSeats());

        courseDAO.saveCourse(course); // Save course
        return "redirect:/faculty/dashboard";
    }

    // Edit course details
    @GetMapping("/edit-course/{id}")
    public String editCourse(@PathVariable("id") Integer id, Model model) {
        Course course = courseDAO.getCourseById(id);
        model.addAttribute("course", course);
        return "edit_course";
    }

    @PostMapping("/update-course")
    public String updateCourse(@ModelAttribute Course course, HttpSession session) {
        Faculty faculty = (Faculty) session.getAttribute("faculty");
        if (faculty == null) {
            return "redirect:/user/login"; // Redirect to login if not logged in
        }

        // Fetch the existing course from the database
        Course existingCourse = courseDAO.getCourseById(course.getId());

        // If existing course exists, handle `availableSeats` logic
        if (existingCourse != null) {
            if (course.getAvailableSeats() == null) {
                course.setAvailableSeats(existingCourse.getAvailableSeats());
            } else if (course.getMaxSeats() > existingCourse.getMaxSeats()) {
                // Handle increased maxSeats by adjusting availableSeats
                int maxSeatsDifference = course.getMaxSeats() - existingCourse.getMaxSeats();
                course.setAvailableSeats(existingCourse.getAvailableSeats() + maxSeatsDifference);
            }
        }

        // Associate the course with the logged-in faculty
        course.setFaculty(faculty);

        // Ensure status is not null
        if (course.getStatus() == null) {
            course.setStatus("Pending");
        }

        // Update the course in the database
        courseDAO.updateCourse(course);

        return "redirect:/faculty/dashboard";
    }


    // Display enrolled students
    @GetMapping("/enrolled-students")
    public String showEnrolledStudents(Model model, HttpSession session) {
        Faculty faculty = (Faculty) session.getAttribute("faculty");
        if (faculty == null) {
            return "redirect:/user/login";
        }

        List<Course> courses = courseDAO.getCoursesByFaculty(faculty);
        List<Registration> registrations = registrationDAO.getRegistrationsForCourses(courses);

        model.addAttribute("registrations", registrations);
        return "enrolled_students";
    }

    // Delete a course
    @PostMapping("/delete-course")
    public String deleteCourse(@RequestParam("courseId") Integer courseId, HttpSession session) {
        Faculty faculty = (Faculty) session.getAttribute("faculty");
        if (faculty == null) {
            return "redirect:/user/login";
        }

        courseDAO.deleteCourse(courseId);
        return "redirect:/faculty/dashboard";
    }
    
    //new ones
 // Show faculty profile update form
    @GetMapping("/update-profile")
    public String showFacultyProfileUpdateForm(Model model, HttpSession session) {
        Faculty faculty = (Faculty) session.getAttribute("faculty");
        if (faculty == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("faculty", faculty);
        return "faculty_update_profile";
    }

    // Handle faculty profile update
    @PostMapping("/update-profile")
    public String updateFacultyProfile(
            @ModelAttribute Faculty updatedFaculty,
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session, Model model) {
        
        Faculty faculty = (Faculty) session.getAttribute("faculty");
        if (faculty == null) {
            return "redirect:/user/login";
        }

        // Update department and position
        faculty.setDepartment(updatedFaculty.getDepartment());
        faculty.setPosition(updatedFaculty.getPosition());

        // Handle password update
        User user = faculty.getUser(); // Fetch the associated User object
        if (!currentPassword.isEmpty() || !newPassword.isEmpty() || !confirmPassword.isEmpty()) {
            // Validate current password
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                model.addAttribute("error", "Current password is incorrect.");
                return "faculty_update_profile";
            }

            // Validate new password and confirm password
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("error", "New password and confirm password do not match.");
                return "faculty_update_profile";
            }

            // Update the password
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        // Save updated faculty and user details
        facultyDAO.updateFaculty(faculty);
        userDAO.updateUser(user);

        model.addAttribute("message", "Profile updated successfully.");
        return "faculty_update_profile";
    }


}

