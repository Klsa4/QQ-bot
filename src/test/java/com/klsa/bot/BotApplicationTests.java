package com.klsa.bot;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.klsa.bot.Constants.BotURLConstants;
import com.klsa.bot.Service.GetAccessBotService;
import com.klsa.bot.config.QQConfiguration;
import com.klsa.bot.pojo.BotToken;

@SpringBootTest
class BotApplicationTests {

	@Autowired
	QQConfiguration qqConfiguration;

	@Autowired
	HttpClient httpClient;

	@Autowired
	GetAccessBotService getAccessBotService;

	@Test
	void contextLoads() {

	}

	@Test
	void testConnectToBot() throws ClientProtocolException, IOException {
		String url = BotURLConstants.GetAppAccessToken;

		Map<String, String> data = new HashMap<>();
		data.put("appId", qqConfiguration.getAppId());
		data.put("clientSecret", qqConfiguration.getAppSecret());
		// data.add("token", "0WZ17nd69qUX4avE7bM9I2AMxd6b3KiI");
		// data.add("Authorization", "Bot 102090753.0WZ17nd69qUX4avE7bM9I2AMxd6b3KiI");

		String json = JSON.toJSONString(data);
		
		// System.out.println(json);

		
		HttpPost httpPost = new HttpPost(url);
		StringEntity reqeustBody = new StringEntity(json);
		reqeustBody.setContentType("application/json");
		httpPost.setEntity(reqeustBody);
		
		HttpResponse response = httpClient.execute(httpPost);
		String responseBody  = EntityUtils.toString(response.getEntity());
		
		BotToken botToken = JSON.parseObject(responseBody, BotToken.class);

		System.out.println(botToken);
	}

	@Test
	public void testGetAccessToken() throws Exception {
		HttpPost httpPost = new HttpPost();
		httpPost = (HttpPost)getAccessBotService.getAccessToken(5, httpPost);
		System.out.println(httpPost);
	}

	@Test
	public void testWSS() {
		String url = BotURLConstants.OPENAPI_BASIC_API + "/gateway";
		try {
			HttpGet httpGet = new HttpGet(url);
			httpGet = (HttpGet) getAccessBotService.getAccessToken(5, httpGet);
			HttpResponse response = httpClient.execute(httpGet);
			String json = EntityUtils.toString(response.getEntity());			
			System.out.println(json);
		} catch (Exception e) {
			throw new RuntimeException("超时");
		}
	}

	
}
