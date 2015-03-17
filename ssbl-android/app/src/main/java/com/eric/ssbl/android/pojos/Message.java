package com.eric.ssbl.android.pojos;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name="messages")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
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
	
	@Id
	@GenericGenerator(name="gen",strategy="increment")
	@GeneratedValue(generator="gen")
	@Column(name="message_id", unique=true, nullable=false)
	public Integer getMessageId() {
		return _id;
	}
	
	public void setMessageId(Integer id) {
		_id = id;
	}
	
	@ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="conversation_id")
	public Conversation getConversation() {
		return _conversation;
	}
	
	public void setConversation(Conversation conversation) {
		_conversation = conversation;
	}
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sender_id")
	public User getSender() {
		return _sender;
	}
	
	public void setSender(User sender) {
		_sender = sender;
	}
	
	@Column(name="body")
	@Type(type="text")
	public String getBody() {
		return _body;
	}
	
	public void setBody(String body) {
		_body = body;
	}
	
	@Column(name="sent_time")
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
