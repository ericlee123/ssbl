package com.hunnymustard.ssbl.server.repository.impl;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.REQUIRED, readOnly=false)
public abstract class HibernateRepository<E, K extends Serializable> {

	@Autowired
	private SessionFactory _factory;
	
	protected Session getSession() {
		return _factory.getCurrentSession();
	}
	
	public E add(E entity) {
		getSession().saveOrUpdate(entity);
		return entity;
	}

	public void remove(E entity) {
		getSession().delete(entity);
	}

	@SuppressWarnings("unchecked")
	public E update(E entity) {
		return (E) getSession().merge(entity);
	}
}
