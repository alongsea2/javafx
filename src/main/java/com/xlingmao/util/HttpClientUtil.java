package com.xlingmao.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;



public class HttpClientUtil {

	public static final Logger logger = Logger.getLogger(HttpClientUtil.class);

	public static String get(String url) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String responseBody = "";
		HttpGet httpget = new HttpGet(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
		httpget.setConfig(requestConfig);
		logger.info(" http request url : " + url);
		try {
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				HttpEntity entity = response.getEntity();
				logger.info(" http request status : " + response.getStatusLine());
				if (entity != null) {
					responseBody = EntityUtils.toString(entity);
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			logger.error(e);
		} catch (ParseException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
		logger.info(" http request response : " + responseBody);
		return responseBody;
	}
	
	public static String getWithHeaders(String url,Map<String,String> headers) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String responseBody = "";
		HttpGet httpget = new HttpGet(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
		httpget.setConfig(requestConfig);
		logger.info(" http request url : " + url);
		try {
			for (Entry<String, String> entry : headers.entrySet()) {
				httpget.addHeader(entry.getKey(), entry.getValue());
			}
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				HttpEntity entity = response.getEntity();
				logger.info(" http request status : " + response.getStatusLine());
				if (entity != null) {
					responseBody = EntityUtils.toString(entity);
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			logger.error(e);
		} catch (ParseException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
		if (StringUtils.isNotEmpty(url) && url.contains("messages/logs")) {
			return responseBody;
		}
		logger.info(" http request response : " + responseBody);
		return responseBody;
	}

	public static String post(String url) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String responseBody = "";
		HttpPost httppost = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
		httppost.setConfig(requestConfig);
		logger.info(" http request url : " + url);
		try {
			CloseableHttpResponse response = httpclient.execute(httppost);
			logger.info(" http request status : " + response.getStatusLine());
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					responseBody = EntityUtils.toString(entity, "UTF-8");
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			logger.error(e);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
		logger.info(" http request response : " + responseBody);
		return responseBody;
	}
	
	public static String postWithHeaders(String url,Map<String,String> headers) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String responseBody = "";
		HttpPost httppost = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
		httppost.setConfig(requestConfig);
		logger.info(" http request url : " + url);
		try {
			for (Entry<String, String> entry : headers.entrySet()) {
				httppost.addHeader(entry.getKey(), entry.getValue());
			}
			CloseableHttpResponse response = httpclient.execute(httppost);
			logger.info(" http request status : " + response.getStatusLine());
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					responseBody = EntityUtils.toString(entity, "UTF-8");
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			logger.error(e);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
		logger.info(" http request response : " + responseBody);
		return responseBody;
	}


	public static String postWithParams(String url, List<NameValuePair> params) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String responseBody = "";
		HttpPost httppost = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
		httppost.setConfig(requestConfig);
		logger.info(" http request url : " + url);
		try {
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(params, "UTF-8");
			httppost.setEntity(uefEntity);
			CloseableHttpResponse response = httpclient.execute(httppost);
			logger.info(" http request status : " + response.getStatusLine());
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					responseBody = EntityUtils.toString(entity, "UTF-8");
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			logger.error(e);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
		logger.info(" http request response : " + responseBody);
		return responseBody;
	}
	
	public static String postWithHeadersAndParams(String url,Map<String,String> headers,List<NameValuePair> params) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String responseBody = "";
		HttpPost httppost = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
		httppost.setConfig(requestConfig);
		logger.info(" http request url : " + url);
		try {
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(params, "UTF-8");
			for (Entry<String, String> entry : headers.entrySet()) {
				httppost.addHeader(entry.getKey(), entry.getValue());
			}
			httppost.setEntity(uefEntity);
			CloseableHttpResponse response = httpclient.execute(httppost);
			logger.info(" http request status : " + response.getStatusLine());
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					responseBody = EntityUtils.toString(entity, "UTF-8");
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			logger.error(e);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
		logger.info(" http request response : " + responseBody);
		return responseBody;
	}
	
	public static String postWithHeadersAndJsonBody(String url,Map<String,String> headers,String jsonBody){
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String responseBody = "";
		HttpPost httppost = new HttpPost(url);
		//HttpPut httppost = new HttpPut(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
		httppost.setConfig(requestConfig);
		logger.info(" http request url : " + url);
		try {
			StringEntity jsonEntity = new StringEntity(jsonBody,"UTF-8");
			if(null != headers){
				for (Entry<String, String> entry : headers.entrySet()) {
					httppost.addHeader(entry.getKey(), entry.getValue());
				}
			}
			httppost.setEntity(jsonEntity);
			CloseableHttpResponse response = httpclient.execute(httppost);
			logger.info(" http request status : " + response.getStatusLine());
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					responseBody = EntityUtils.toString(entity, "UTF-8");
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			logger.error(e);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
		logger.info(" http request response : " + responseBody);
		return responseBody;
	}
	/**
	 * 
	 * @param url请求地址
	 * @param requestData 请求数据
	 * @param certUrl 证书地址
	 * @param certPassword 证书密码，默认商户号
	 * @return
	 */
	public static void main(String[] args) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("account", "hcs00611"));
		params.add(new BasicNameValuePair("password", "236547"));
		params.add(new BasicNameValuePair("mobile", "18662580895"));
		params.add(new BasicNameValuePair("content", "您的验证码为123456，请于2分钟内正确输入，如非本人操作请忽略息。(小灵猫)【畅卓科技】"));
		params.add(new BasicNameValuePair("action", "send"));
		postWithParams("http://sms.chanzor.com:8001/sms.aspx", params);
	}
}
