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
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.hunnymustard.ssbl.model.User;

public class HttpPostExample {

	public static void main(String[] args) throws ClientProtocolException, IOException {
		User user = new User();
		user.setUsername("fucker");
		user.setPassword("password");
		user.setEmail("fucker@password.com");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Hibernate4Module());
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost("http://localhost:8080/ssbl-server/smash/auth/register");
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
