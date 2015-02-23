package com.hunnymustard.ssbl.server.repository.hibernate;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("eventRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly=false)
public class EventRepositoryHibernate {

}
