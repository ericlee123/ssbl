package com.hunnymustard.ssbl.server.repository.impl;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hunnymustard.ssbl.server.repository.GenericRepository;

@Repository
@Transactional(propagation = Propagation.REQUIRED, readOnly=false)
public abstract class HibernateRepository<E, K extends Serializable> implements GenericRepository<E, K> {

	@Autowired
	private SessionFactory _factory;
	
	protected Session getSession() {
		return _factory.getCurrentSession();
	}
	
	@Override
	public Object load(Class<?> cls, String property, K key) {
		return getSession().createCriteria(cls)
				.setProjection(Projections.property(property))
				.add(Restrictions.idEq(key))
				.uniqueResult();		
	}
	
	@Override
	public E add(E entity) {
		getSession().saveOrUpdate(entity);
		return entity;
	}
	
	@Override
	public void remove(E entity) {
		getSession().delete(entity);
	}

	@Override
	@SuppressWarnings("unchecked")
	public E update(E entity) {
		return (E) getSession().merge(entity);
	}
}
