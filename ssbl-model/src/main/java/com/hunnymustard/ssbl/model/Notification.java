package com.hunnymustard.ssbl.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

/**
 * This class represents a notification. Notifications are sent by a sender user to
 * a receiver user and are used to convey some information or message. This format of the
 * message is determined by the type attribute.
 * 
 * @author ashwin
 * 
 * @see com.hunnymustard.ssbl.model.User
 */
@Entity
@Table(name="notifications")
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class Notification {
	
	private Integer _id;
	private User _sender, _receiver;
	private String _message;
	private Long _sendTime;
	private Type _type;
	
	public enum Type {
		MESSAGE, SYSTEM, CHALLENGE;
	}
	
	public Notification() {}
	
	public Notification(Integer id, User sender, User receiver, String message, Long sendTime, Type type) {
		_id = id;
		_sender = sender;
		_receiver = receiver;
		_message = message;
		_sendTime = sendTime;
		_type = type;
	}
	
	@Id
	@GenericGenerator(name="gen",strategy="increment")
	@GeneratedValue(generator="gen")
	@Column(name="notification_id", unique=true, nullable=false)
	public Integer getNotificationId() {
		return _id;
	}
	
	public void setNotificationId(Integer id) {
		_id = id;
	}
	
	@OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name="sender_id", nullable=false)
	public User getSender() {
		return _sender;
	}
	
	public void setSender(User sender) {
		_sender = sender;
	}
	
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name="receiver_id", nullable=false)
	public User getReceiver() {
		return _receiver;
	}
	
	public void setReceiver(User receiver) {
		_receiver = receiver;
	}
	
	@Column(name="message", nullable=false)
	public String getMessage() {
		return _message;
	}
	
	public void setMessage(String message) {
		_message = message;
	}
	
	@Column(name="send_time", nullable=false)
	public Long getSendTime() {
		return _sendTime;
	}
	
	public void setSendTime(Long sendTime) {
		_sendTime = sendTime;
	}
	
	@Column(name="type", nullable=false)
	@Enumerated(EnumType.ORDINAL)
	public Type getType() {
		return _type;
	}
	
	public void setType(Type type) {
		_type = type;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(_id)
			.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Notification)
			return new EqualsBuilder()
				.append(_id, ((Notification) obj).getNotificationId())
				.build();
		return false;
	}
}
