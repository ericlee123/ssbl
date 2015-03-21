package com.hunnymustard.ssbl.client;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.hunnymustard.ssbl.model.User;

public class HttpGetExample {

	public static void main(String[] args) throws ClientProtocolException, IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet("http://localhost:8080/ssbl-server/smash/search/user?lat=33.049126&lon=-96.819387&radius=5.0");
		HttpResponse response = client.execute(get);
		String json = EntityUtils.toString(response.getEntity());
		
		ObjectMapper mapper = new ObjectMapper();
		// Two different ways of deserializing the same json string. The first one is much
		// more verbose, but is also slightly better. If you are deserializing a simple object,
		// than you just write the name of the class ex. mapper.readValue(json, User.class);
		List<User> u1 = mapper.readValue(json, TypeFactory.defaultInstance().constructCollectionType(List.class, User.class));
		List<User> u2 = mapper.readValue(json, new TypeReference<List<User>>() {});
	
		System.out.println(u1.size());
		System.out.println(u2.size());
	}
}
