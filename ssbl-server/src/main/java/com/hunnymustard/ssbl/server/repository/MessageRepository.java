package com.hunnymustard.ssbl.server.repository;

import java.util.List;

import com.hunnymustard.ssbl.model.Conversation;
import com.hunnymustard.ssbl.model.Message;
import com.hunnymustard.ssbl.model.User;

public interface MessageRepository extends GenericRepository<Message, Integer> {

	List<Message> findByNew(User user);
	List<Message> findAdditionalMessages(Integer conversationId, Integer size, Integer additional);
	
}
