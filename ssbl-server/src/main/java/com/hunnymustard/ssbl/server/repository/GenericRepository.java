package com.hunnymustard.ssbl.server.repository;

import java.io.Serializable;

public interface GenericRepository<E, K extends Serializable> {

	E find(K key);
	E add(E entity);
	void remove(E entity);
	E update(E entity);
	
	Object load(Class<?> cls, String property, K key);
}
