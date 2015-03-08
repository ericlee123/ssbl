package com.hunnymustard.ssbl.server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hunnymustard.ssbl.model.Message;
import com.hunnymustard.ssbl.model.User;
import com.hunnymustard.ssbl.server.repository.ConversationRepository;
import com.hunnymustard.ssbl.server.repository.MessageRepository;
import com.hunnymustard.ssbl.server.repository.UserRepository;
import com.hunnymustard.ssbl.server.service.MessagingService;

@Service("messagingService")
@Transactional(propagation = Propagation.REQUIRED, readOnly=false)
public class MessagingServiceHibernate implements MessagingService {

	@Autowired
	private ConversationRepository _conversationRepository;
	
	@Autowired
	private MessageRepository _messageRepository;

	@Autowired
	private UserRepository _userRepository;
	
	@Override
	public Message sendMessage(Message message) {
		// If the conversation doesn't already exist create it, otherwise update
		message.setConversation(_conversationRepository.add(message.getConversation()));
		return _messageRepository.add(message);
	}

	@Override
	public List<Message> getNewMessages(User user) {
		List<Message> messages = _messageRepository.findByNew(user);
		user.setLastMessageTime(System.currentTimeMillis());
		_userRepository.update(user);
		return messages;
	}

	@Override
	public List<Message> getAdditionalMessages(Integer conversationId, Integer size, Integer additional) {
		return _messageRepository.findAdditionalMessages(conversationId, size, additional);
	}

}
