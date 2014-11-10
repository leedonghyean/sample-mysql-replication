package com.github.nagaseyasuhito.sample.mysql.replication.entity;

import java.sql.Connection;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import lombok.Cleanup;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class UserIT {

	@Test
	public void persistSuccess() throws Exception {
		Map<String, String> properties = ImmutableMap.of("javax.persistence.jdbc.url", System.getProperty("javax.persistence.jdbc.url"));

		@Cleanup
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("sample-mysql-replication", properties);
		@Cleanup
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		entityManager.getTransaction().begin();
		User user = new User();
		user.setName("name");
		user.setPassword("password");
		entityManager.persist(user);
		entityManager.getTransaction().commit();

		entityManager.getTransaction().begin();
		System.out.println(entityManager.createQuery("from User u where u.name = 'name'", User.class).getSingleResult());
		entityManager.getTransaction().commit();

		// from slave
		entityManager.getTransaction().begin();
		entityManager.unwrap(Connection.class).setReadOnly(true);
		// for Hibernate
		// entityManager.unwrap(SessionImplementor.class).connection().setReadOnly(true);
		System.out.println(entityManager.createQuery("from User u where u.name = 'name'", User.class).getSingleResult());
		entityManager.getTransaction().commit();
	}
}
