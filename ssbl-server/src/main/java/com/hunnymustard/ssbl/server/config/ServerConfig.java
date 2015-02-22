package com.hunnymustard.ssbl.server.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@PropertySource({"classpath:/messenger.properties"})
@ComponentScan({"com.ashwin.messenger"})
public class ServerConfig {

//	http://stackoverflow.com/questions/22315672/how-to-configure-spring-mvc-with-pure-java-based-configuration
	@Autowired
	private Environment env;
	 
	@Bean
	public DataSource hikariDataSource() {
		HikariConfig config = new HikariConfig();
		config.setMaximumPoolSize(100);
		config.setDataSourceClassName(env.getProperty("dataSourceClassName"));
		config.addDataSourceProperty("serverName", env.getProperty("dataSource.serverName"));
		config.addDataSourceProperty("port", env.getProperty("dataSource.port"));
		config.addDataSourceProperty("databaseName", env.getProperty("dataSource.databaseName"));
		config.addDataSourceProperty("user", env.getProperty("dataSource.user"));
		config.addDataSourceProperty("password", env.getProperty("dataSource.password"));
		return new HikariDataSource(config);
	}
	
	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(hikariDataSource());
		sessionFactory.setPackagesToScan(new String[] { "com.hunnymustard.ssbl.model" });
		
		Properties hibernate = new Properties();
		hibernate.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
		hibernate.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
		hibernate.setProperty("hibernate.globally_quoted_identifiers", env.getProperty("hibernate.globally_quoted_identifiers"));
		hibernate.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
		sessionFactory.setHibernateProperties(hibernate);

		return sessionFactory;
	}
	
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Hibernate4Module());
		return mapper;
	}
	
	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}
	
	@Bean
	@Autowired
	public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(sessionFactory);
		return txManager;
	}
}
