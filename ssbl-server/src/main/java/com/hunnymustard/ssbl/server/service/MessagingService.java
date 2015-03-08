package com.hunnymustard.ssbl.server.service;

import java.util.List;

import com.hunnymustard.ssbl.model.Conversation;
import com.hunnymustard.ssbl.model.Message;
import com.hunnymustard.ssbl.model.User;

public interface MessagingService {

	List<Message> getNewMessages(User user);
	List<Message> getAdditionalMessages(Integer conversationId, Integer size, Integer additional);
	
	Message sendMessage(Message message);
}
