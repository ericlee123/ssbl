package com.hunnymustard.ssbl.server.service.impl;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hunnymustard.ssbl.model.Message;
import com.hunnymustard.ssbl.model.User;
import com.hunnymustard.ssbl.server.repository.MessageRepository;
import com.hunnymustard.ssbl.server.repository.UserRepository;
import com.hunnymustard.ssbl.server.service.MessagingService;

@Service("messagingService")
@Transactional(propagation = Propagation.REQUIRED, readOnly=false)
public class MessagingServiceHibernate implements MessagingService {
	
	@Autowired
	private MessageRepository _messageRepository;
	
	@Autowired
	private UserRepository _userRepository;
	
	@Override
	public Message send(Message message) {
		// If the conversation doesn't already exist create it, otherwise update
		return _messageRepository.add(message);
	}

	@Override
	public List<Message> getByNew(User user) {
		// Find the new messages for the user and then update their last message timestamp
		List<Message> messages = _messageRepository.findByNew(user);
		user.setLastMessageTime(System.currentTimeMillis());
		_userRepository.update(user);

		for(Message message : messages) {
			Hibernate.initialize(message.getConversation().getRecipients());
			Hibernate.initialize(message.getSender());
		}
		
		return messages;
	}

	@Override
	public List<Message> getByConversation(Integer conversationId, Integer size, Integer additional) {
		List<Message> messages = _messageRepository.findByConversation(conversationId, size, additional);
		 for(Message message : messages)
			Hibernate.initialize(message.getSender());
		return messages;
	}

	@Override
	public void delete(Message message) {
		_messageRepository.remove(message);
	}
}
