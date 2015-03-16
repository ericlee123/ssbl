package com.hunnymustard.ssbl.server.service;

import java.util.List;

import com.hunnymustard.ssbl.model.Notification;
import com.hunnymustard.ssbl.model.User;

public interface NotificationService {

	List<Notification> getByNew(User user);
	
	Notification create(Notification notification);
	
}
