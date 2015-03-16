package com.hunnymustard.ssbl.server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hunnymustard.ssbl.model.Notification;
import com.hunnymustard.ssbl.model.User;
import com.hunnymustard.ssbl.server.repository.NotificationRepository;
import com.hunnymustard.ssbl.server.service.NotificationService;

@Service("notificationService")
@Transactional(propagation = Propagation.REQUIRED, readOnly=false)
public class NotificationServiceHibernate implements NotificationService {

	@Autowired
	private NotificationRepository _notificationRepository;
	
	@Override
	public List<Notification> getByNew(User user) {
		return _notificationRepository.findByNew(user);
	}

	@Override
	public Notification create(Notification notification) {
		return _notificationRepository.add(notification);
	}

}
