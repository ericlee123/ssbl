package com.hunnymustard.ssbl.server.repository.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hunnymustard.ssbl.model.Conversation;
import com.hunnymustard.ssbl.server.repository.ConversationRepository;

@Repository("conversationRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly=false)
public class ConversationRepositoryHibernate extends HibernateRepository<Conversation, Integer>implements ConversationRepository {

	@Override
	public Conversation find(Integer key) {
		return (Conversation) getSession().get(Conversation.class, key);
	}

}
