package com.hunnymustard.ssbl.server.repository;

import java.util.List;

import com.hunnymustard.ssbl.model.Notification;
import com.hunnymustard.ssbl.model.User;

public interface NotificationRepository extends GenericRepository<Notification, Integer> {

	List<Notification> findByNew(User user);
	
}
