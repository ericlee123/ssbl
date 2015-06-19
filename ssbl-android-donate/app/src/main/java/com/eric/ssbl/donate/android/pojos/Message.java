package com.eric.ssbl.donate.android.pojos;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonIdentityInfo(scope=Message.class, generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class Message {
	
	private Integer _id;
	private Conversation _conversation;
	private User _sender;
	private Long _sentTime;
	private String _body;
	
	public Message() {}
	
	public Message(Integer id, Conversation conversation, User sender, Long sentTime, String body) {
		_id = id;
		_conversation = conversation;
		_sender = sender;
		_sentTime = sentTime;
		_body = body;
	}

	public Integer getMessageId() {
		return _id;
	}
	
	public void setMessageId(Integer id) {
		_id = id;
	}

	public Conversation getConversation() {
		return _conversation;
	}
	
	public void setConversation(Conversation conversation) {
		_conversation = conversation;
	}

	public User getSender() {
		return _sender;
	}
	
	public void setSender(User sender) {
		_sender = sender;
	}

	public String getBody() {
		return _body;
	}
	
	public void setBody(String body) {
		_body = body;
	}

	public Long getSentTime() {
		return _sentTime;
	}
	
	public void setSentTime(Long sentTime) {
		_sentTime = sentTime;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(_id)
			.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Message)
			return new EqualsBuilder()
				.append(_id, ((Message) obj).getMessageId())
				.build();
		return false;
	}
}
