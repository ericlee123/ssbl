package com.hunnymustard.ssbl.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.SelectBeforeUpdate;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@DynamicUpdate(value=true)
@SelectBeforeUpdate
@Table(name="conversations")
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
	
	@Id
	@GenericGenerator(name="gen",strategy="increment")
	@GeneratedValue(generator="gen")
	@Column(name="conversation_id", unique=true, nullable=false)
	public Integer getConversationId() {
		return _id;
	}
	
	public void setConversationId(Integer id) {
		_id = id;
	}
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="conversation_users",
            inverseJoinColumns=@JoinColumn(name="user_id"),
            joinColumns=@JoinColumn(name="conversation_id"))
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
	
	@OneToMany(mappedBy="conversation", fetch=FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.EXTRA)
	@OrderBy("sent_time DESC")
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
