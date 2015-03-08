package com.hunnymustard.ssbl.server.repository.impl;

import com.hunnymustard.ssbl.model.Conversation;
import com.hunnymustard.ssbl.server.repository.ConversationRepository;

public class ConversationRepositoryHibernate extends HibernateRepository<Conversation, Integer>implements ConversationRepository {

	@Override
	public Conversation find(Integer key) {
		return (Conversation) getSession().get(Conversation.class, key);
	}

}
