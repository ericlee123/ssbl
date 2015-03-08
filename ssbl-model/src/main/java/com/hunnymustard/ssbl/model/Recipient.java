package com.hunnymustard.ssbl.model;

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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name="recipients")
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class Recipient {

	private Integer _id;
	private User _user;
	private Conversation _conversation;
	private long _lastViewTime;
	
	public Recipient(Integer id, User user, Conversation conversation, Long lastViewTime) {
		_id = id;
		_user = user;
		_conversation = conversation;
		_lastViewTime = lastViewTime;
	}
	
	@Id
	@GenericGenerator(name="gen",strategy="increment")
	@GeneratedValue(generator="gen")
	@Column(name="recipient_id", unique=true, nullable=false)
	public Integer getRecipientID() {
		return _id;
	}
	
	public void setRecipientID(Integer id) {
		_id = id;
	}
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="user_id")
	public User getUser() {
		return _user;
	}
	
	public void setUser(User user) {
		_user = user;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="conversation_id")
	public Conversation getConversation() {
		return _conversation;
	}
	
	public void setConversation(Conversation conversation) {
		_conversation = conversation;
	}
	
	@Column(name="last_view_time")
	public Long getLastViewTime() {
		return _lastViewTime;
	}
	
	public void setLastViewTime(Long lastViewTime) {
		_lastViewTime = lastViewTime;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(_id)
			.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Recipient)
			return new EqualsBuilder()
				.append(_id, ((Recipient) obj).getRecipientID())
				.build();
		return false;
	}
}
