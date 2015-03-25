package com.hunnymustard.ssbl.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import com.hunnymustard.ssbl.model.Notification;
import com.hunnymustard.ssbl.model.User;

public class HttpPostExample {

	public static void main(String[] args) throws ClientProtocolException, IOException {
		User user = new User();
		user.setUsername("fucker1243");
		user.setPassword("passwordpass");
		user.setEmail("fucker1243@password.com");
		
		List<User> friends = new ArrayList<User>();
		User friend = new User();
		friend.setUserId(1);
		friend.setUsername("stateFromJakeFarm");
		friend.setEmail("sfjf@test.com");
		friends.add(friend);
		
		user.setFriends(friends);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost("http://ec2-54-69-43-179.us-west-2.compute.amazonaws.com:8080/ssbl-server-2.0/smash/auth/register");
		post.setHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));
		post.setHeader(new BasicHeader(HttpHeaders.ACCEPT, "application/json"));
		
		StringEntity data = new StringEntity(mapper.writeValueAsString(user), "UTF-8");
		data.setContentType("application/json");
		post.setEntity(data);
		
		CloseableHttpResponse response = client.execute(post);
		System.out.println(EntityUtils.toString(response.getEntity()));
		EntityUtils.consume(response.getEntity());
	}
}
