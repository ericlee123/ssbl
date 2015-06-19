package com.eric.ssbl.donate.android.pojos;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo(scope=Conversation.class, generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class Conversation {

	private Integer _id;
	private List<User> _recipients;
	private List<Message> _messages;
	
	public Conversation() {}
	
	public Conversation(Integer id, List<User> recipients, List<Message> messages) {
		_id = id;
		_recipients = recipients;
		_messages = messages;
	}

	public Integer getConversationId() {
		return _id;
	}
	
	public void setConversationId(Integer id) {
		_id = id;
	}

	public List<User> getRecipients() {
		return _recipients;
	}
	
	public void setRecipients(List<User> recipients) {
		_recipients = recipients;
	}
	
	public void addRecipient(User recipient) {
		if(_recipients == null) _recipients = new ArrayList<User>();
		_recipients.add(recipient);
	}

	public List<Message> getMessages() {
		return _messages;
	}
	
	public void setMessages(List<Message> messages) {
		_messages = messages;
	}
	
	public void addMessage(Message message) {
		if(_messages == null) _messages = new ArrayList<Message>();
		_messages.add(message);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(_id)
			.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Conversation)
			return new EqualsBuilder()
				.append(_id, ((Conversation) obj).getConversationId())
				.build();
		return false;
	}
}
