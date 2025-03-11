package com.project.WebToolsProject;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.project.WebToolsProject.POJO.*;

@SpringBootApplication
@ComponentScan(basePackages = "com.project.WebToolsProject")

public class WebToolsProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebToolsProjectApplication.class, args);
	}
	@Bean
	public SessionFactory getSessionFactory() {
		Map<String, Object> settings = new HashMap<>();
		
		 settings.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
	     settings.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/courseportal");
	     settings.put("hibernate.connection.username", "root");
	     settings.put("hibernate.connection.password", "webtools@123");

	     settings.put("hibernate.hbm2ddl.auto", "update");
	     settings.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
	     settings.put("hibernate.dialect.storage_engine", "innodb");
	     settings.put("hibernate.show-sql", "true");
	     
	     ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
	    		 .applySettings(settings)
	    		 .build();	
	     MetadataSources metaDataSources = new MetadataSources(serviceRegistry);
	     metaDataSources.addPackage("com.example.demo.models");
	     metaDataSources.addAnnotatedClass(User.class);
	     metaDataSources.addAnnotatedClass(Role.class);
	     metaDataSources.addAnnotatedClass(Admin.class);
	     metaDataSources.addAnnotatedClass(Course.class);
	     metaDataSources.addAnnotatedClass(Faculty.class);
	     metaDataSources.addAnnotatedClass(Registration.class);
	     metaDataSources.addAnnotatedClass(Schedule.class);
	     metaDataSources.addAnnotatedClass(Student.class);


	     

	     Metadata metaData = metaDataSources.buildMetadata();
	     
	     return metaData.getSessionFactoryBuilder().build();
	}

}



