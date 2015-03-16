package com.hunnymustard.ssbl.server.service;

import java.util.List;

import com.hunnymustard.ssbl.model.Conversation;
import com.hunnymustard.ssbl.model.Message;
import com.hunnymustard.ssbl.model.User;

public interface MessagingService {

	List<Message> getByNew(User user);
	List<Message> getByConversation(Integer conversationId, Integer size, Integer additional);
	
	Message send(Message message);
}
