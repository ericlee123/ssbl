package com.hunnymustard.ssbl.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

public class Notification {
	
	private Integer _id;
	private User _sender, _receiver;
	private String _message;
	private Long _sendTime;
	private Type _type;
	
	public enum Type {
		MESSAGE, SYSTEM, CHALLENGE;
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
	@JoinColumn(name="sender", nullable=false)
	public User getSender() {
		return _sender;
	}
	
	public void setSender(User sender) {
		_sender = sender;
	}
	
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name="receiver", nullable=false)
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
