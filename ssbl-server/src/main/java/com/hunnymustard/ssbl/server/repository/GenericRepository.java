package com.hunnymustard.ssbl.server.repository;

import java.io.Serializable;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This is the generic parent interface of all repositories.
 * It exposes a basic CRUD interface for interacting with the
 * database.
 * 
 * @author ashwin
 *
 * @param <E> Entity
 * @param <K> Key
 */
public abstract class GenericRepository<E, K extends Serializable> {

	@Autowired
	protected SessionFactory _factory;
	
	public abstract E find(K key);
	
	public E add(E entity) {
		_factory.getCurrentSession().saveOrUpdate(entity);
		return entity;
	}

	public void remove(E entity) {
		_factory.getCurrentSession().delete(entity);
	}

	public E update(E entity) {
		_factory.getCurrentSession().update(entity);
		return entity;
	}
}
