package com.eric.ssbl.donate.android.pojos;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
@JsonIdentityInfo(scope=Notification.class, generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
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

	public Integer getNotificationId() {
		return _id;
	}
	
	public void setNotificationId(Integer id) {
		_id = id;
	}

	public User getSender() {
		return _sender;
	}
	
	public void setSender(User sender) {
		_sender = sender;
	}

	public User getReceiver() {
		return _receiver;
	}
	
	public void setReceiver(User receiver) {
		_receiver = receiver;
	}

	public String getMessage() {
		return _message;
	}
	
	public void setMessage(String message) {
		_message = message;
	}

	public Long getSendTime() {
		return _sendTime;
	}
	
	public void setSendTime(Long sendTime) {
		_sendTime = sendTime;
	}

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
