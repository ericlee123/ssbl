package com.hunnymustard.ssbl.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name="conversations")
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class Conversation {

	private Integer _id;
	private List<Recipient> _recipients;
	private List<Message> _messages;
	
	public Conversation() {}
	
	public Conversation(Integer id, List<Recipient> recipients, List<Message> messages) {
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
	
	@OneToMany(mappedBy="conversation", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	public List<Recipient> getRecipients() {
		return _recipients;
	}
	
	public void setRecipients(List<Recipient> recipients) {
		_recipients = recipients;
	}
	
	// Returns the recipient for a particular student, or null if the student is not a recipient
	// to the conversation or the conversations does not have any recipients.
	@Transient
	public Recipient getRecipient(User user) {
		if(_recipients != null)
			for(Recipient recipient : _recipients)
				if(recipient.getUser().equals(user))
					return recipient;
		return null;
	}
	
	public void addRecipient(Recipient recipient) {
		if(_recipients == null) _recipients = new ArrayList<Recipient>();
		_recipients.add(recipient);
	}
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="conversation", fetch=FetchType.LAZY)
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
