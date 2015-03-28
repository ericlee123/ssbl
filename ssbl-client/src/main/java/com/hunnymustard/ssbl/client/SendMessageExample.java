package com.hunnymustard.ssbl.client;

import java.io.IOException;

import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hunnymustard.ssbl.model.Conversation;
import com.hunnymustard.ssbl.model.Message;
import com.hunnymustard.ssbl.model.User;

public class SendMessageExample {

	public static void main(String[] args) throws ClientProtocolException, IOException {

		User u1 = new User();
		u1.setUserId(5);
		u1.setUsername("ashwin");
		
		User u2 = new User();
		u2.setUserId(3);
		u2.setUsername("papaya");
		
		Conversation conversation = new Conversation();
		conversation.setConversationId(3);
		conversation.addRecipient(u1);
		conversation.addRecipient(u2);
		
		Message message = new Message();
		message.setBody("bod");
		message.setSender(u1);
		message.setConversation(conversation);
		message.setMessageId(null);
		message.setSentTime(System.currentTimeMillis());
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost("http://localhost:8080/ssbl-server/smash/messaging/ashwin/5");
		post.setHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));
		post.setHeader(new BasicHeader(HttpHeaders.ACCEPT, "application/json"));
		
		StringEntity data = new StringEntity(mapper.writeValueAsString(message), "UTF-8");
		data.setContentType("application/json");
		post.setEntity(data);
		
		CloseableHttpResponse response = client.execute(post);
		System.out.println(EntityUtils.toString(response.getEntity()));
		EntityUtils.consume(response.getEntity());
	}
}
