//package com.project.WebToolsProject.Config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import com.project.WebToolsProject.DAO.FacultyDAO;
//import com.project.WebToolsProject.DAO.StudentDAO;
//import com.project.WebToolsProject.DAO.UserDAO;
//import com.project.WebToolsProject.POJO.Faculty;
//import com.project.WebToolsProject.POJO.Student;
//import com.project.WebToolsProject.POJO.User;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//
//import java.io.IOException;
//
//@Component
//public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//
//    @Autowired
//    private UserDAO userDAO;
//
//    @Autowired
//    private StudentDAO studentDAO;
//    @Autowired
//    private FacultyDAO facultyDAO;
//
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        String role = null;
//
//        // Get the role from authorities
//        for (GrantedAuthority authority : authentication.getAuthorities()) {
//            role = authority.getAuthority();
//        }
//
//        // Fetch the User object
//        String email = authentication.getName(); // This gets the logged-in user's email
//        User user = userDAO.getUserByEmail(email);
//
//        if (role.equals("ROLE_ADMIN")) {
//            response.sendRedirect("/admin/dashboard");
//            return;
//        } else if (role.equals("ROLE_STUDENT")) {
//            HttpSession session = request.getSession();
//
//            // Fetch the student associated with the user
//            Student student = studentDAO.getStudentByUserId(user.getId());
//            if (student != null) {
//                session.setAttribute("student", student); // Add student to session
//            }
//
//            response.sendRedirect("/student/dashboard");
//            return;
//        } else if (role.equals("ROLE_FACULTY")) {
//            HttpSession session = request.getSession();
//
//            // Fetch the faculty associated with the user
//            Faculty faculty = facultyDAO.getFacultyByUserId(user.getId());
//            if (faculty != null) {
//                session.setAttribute("faculty", faculty); // Add faculty to session
//                System.out.println("Faculty added to session: " + faculty);
//            }
//
//            response.sendRedirect("/faculty/dashboard");
//            return;
//        }
//
//
//        response.sendRedirect("/login?error=role-not-found");
//    }
//}
//


package com.project.WebToolsProject.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.project.WebToolsProject.DAO.FacultyDAO;
import com.project.WebToolsProject.DAO.StudentDAO;
import com.project.WebToolsProject.DAO.UserDAO;
import com.project.WebToolsProject.POJO.Faculty;
import com.project.WebToolsProject.POJO.Student;
import com.project.WebToolsProject.POJO.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private StudentDAO studentDAO;

    @Autowired
    private FacultyDAO facultyDAO;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String role = null;

        // Get the role from authorities
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            role = authority.getAuthority();
        }

        // Fetch the User object
        String email = authentication.getName(); // This gets the logged-in user's email
        User user = userDAO.getUserByEmail(email);

        if (role.equals("ROLE_ADMIN")) {
            response.sendRedirect("/admin/dashboard");
            return;
        } else if (role.equals("ROLE_STUDENT")) {
            HttpSession session = request.getSession();

            // Fetch the student associated with the user
            Student student = studentDAO.getStudentByUserId(user.getId());
            if (student != null) {
                session.setAttribute("student", student); // Add student to session
            }

            response.sendRedirect("/student/dashboard");
            return;
        } else if (role.equals("ROLE_FACULTY")) {
            HttpSession session = request.getSession();

            // Fetch the faculty associated with the user
            Faculty faculty = facultyDAO.getFacultyByUserId(user.getId());
            if (faculty != null) {
                session.setAttribute("faculty", faculty); // Add faculty to session
                System.out.println("Faculty added to session: " + faculty);
            }

            response.sendRedirect("/faculty/dashboard");
            return;
        }

        response.sendRedirect("/login?error=role-not-found");
    }
}

