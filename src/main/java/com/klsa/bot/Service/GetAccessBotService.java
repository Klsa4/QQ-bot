package com.klsa.bot.Service;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.klsa.bot.Constants.BotURLConstants;
import com.klsa.bot.config.QQConfiguration;
import com.klsa.bot.pojo.BotToken;
import com.klsa.bot.pojo.WSS;

import java.io.IOException;
import java.util.*;

@Service
public class GetAccessBotService {

	@Autowired
	HttpClient httpClient;

	@Autowired
	QQConfiguration qqConfiguration;
    

	// 获取OpenApi所需token
    public BotToken getAccessToken(Integer times) {
		try {
			// 重试次数用完，返回空指针
			if(times <= 0) return null;

			String url = BotURLConstants.GetAppAccessToken;

			Map<String, String> data = new HashMap<>();
			data.put("appId", qqConfiguration.getAppId());
			data.put("clientSecret", qqConfiguration.getAppSecret());

			String json = JSON.toJSONString(data);
			
			HttpClient httpClient = HttpClients.createDefault();
			
			HttpPost httpPost = new HttpPost(url);
			StringEntity reqeustBody = new StringEntity(json);
			reqeustBody.setContentType("application/json");
			httpPost.setEntity(reqeustBody);
			
			HttpResponse response = httpClient.execute(httpPost);
			String responseBody  = EntityUtils.toString(response.getEntity());

			BotToken botToken = JSON.parseObject(responseBody, BotToken.class);
			if(botToken == null) {
				Thread.sleep(20);
				return getAccessToken(times - 1);
			}

			if(botToken.getExpires_in() < 2) {
				Thread.sleep(2500);
				return getAccessToken(times - 1);
			}

			return botToken;
		} catch(Exception e) {
			throw new RuntimeException("获取QQ后台token出错");
		}
		// {
		// 	"headers": {
		// 	  "Authorization": "QQBot {ACCESS_TOKEN}",
		// 	  "X-Union-Appid": "{BOT_APPID}",
		// 	}
		// }
    }

	// 使Http请求的请求头添加token的方法重构
	public HttpRequestBase getAccessToken(Integer times, HttpRequestBase reqeust) {
		BotToken botToken;
		try {
			botToken = getAccessToken(times);
		} catch (Exception e) {
			throw new RuntimeException("");
		}
		Header header1 = new BasicHeader("Authorization", "QQBot " + botToken.getAccess_token());
        Header header2 = new BasicHeader("X-Union-Appid", qqConfiguration.getAppId());

		reqeust.setHeader(header1);
		reqeust.setHeader(header2);

		return reqeust;
	}

	public String getWSS() {
		String url = BotURLConstants.OPENAPI_BASIC_API_GATEWAY;
		try {
			HttpGet httpGet = new HttpGet(url);
			httpGet = (HttpGet) getAccessToken(5, httpGet);
			HttpResponse response = httpClient.execute(httpGet);
			String json = EntityUtils.toString(response.getEntity());			
			WSS wss = JSON.parseObject(json, WSS.class);
			return wss.getUrl();
		} catch (Exception e) {
			throw new RuntimeException("超时");
		}
	}

}
