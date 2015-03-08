package com.hunnymustard.ssbl.server.repository.impl;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.hunnymustard.ssbl.model.Conversation;
import com.hunnymustard.ssbl.model.Message;
import com.hunnymustard.ssbl.model.User;
import com.hunnymustard.ssbl.server.repository.MessageRepository;

public class MessageRepositoryHibernate extends HibernateRepository<Message, Integer>
		implements MessageRepository {

	@Override
	public Message find(Integer key) {
		return (Message) getSession().get(Message.class, key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Message> findByNew(User user) {
		return (List<Message>) getSession().createCriteria(Message.class)
				.createAlias("sender", "sender")
				.add(Restrictions.gt("sentTime", user.getLastMessageTime()))
				.add(Restrictions.ne("sender.userId", user.getUserId()))
				.addOrder(Order.desc("sentTime"))
				.list();	
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Message> findAdditionalMessages(Integer conversationId, Integer size, Integer additional) {
		return (List<Message>) getSession().createCriteria(Message.class)
				.setFirstResult(size)
				.setMaxResults(additional)
				.createAlias("conversation", "conversation")
				.add(Restrictions.eq("conversation.conversationID", conversationId))
				.addOrder(Order.desc("sentTime"))
				.list();
	}
}
