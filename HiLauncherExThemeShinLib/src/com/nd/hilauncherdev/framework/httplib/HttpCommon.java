package com.nd.hilauncherdev.framework.httplib;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.SSLHandshakeException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

/**
 * 网络连接通用类
 * @author pdw
 * @version 1.0
 * @date 2012-5-16 下午07:08:40 <br>
 *       http commmon class,using for usual http comunication <br>
 *       this class is based on httpclient
 */
public class HttpCommon {
	
	private static final String TAG = "HttpCommon" ;
	
	public static final String CHARSET_UTF_8 = org.apache.http.protocol.HTTP.UTF_8;
	/**
	 * auto retry to re-connect
	 */
	public static final int MAX_REQUEST_RETRY_COUNTS = 3;

	/**
	 * connection timeout for 15 seconds
	 */
	private static final int CONNECTION_TIME_OUT = 5000;
	/**
	 * waiting data timeout for 30 seconds
	 */
	private static final int SOCKET_TIME_OUT = 30000;
	// method post
	public static final String POST = "POST";
	// method get
	public static final String GET = "GET";

	/**
	 * 重连时间间隔,2秒
	 */
	public final static int RETRY_SLEEP_TIME = 2000;

	/**
	 * 下载缓冲区
	 */
	public static int BUFFER_SIZE = 2048; // 缓冲区大小
	
	/**
	 * URL地址
	 */
	private String url;
	/**
	 * 编码格式
	 */
	private String encoding = CHARSET_UTF_8;
	
	public HttpCommon() {
		
	}

	public HttpCommon(String url) {
		this.url = utf8URLencode(url);
	}
	
	/**
	 * 地址与编码
	 * <br>Author:ryan
	 * <br>Date:2012-8-1下午08:41:56
	 */
	public HttpCommon(String url, String encoding) {
		this.url = utf8URLencode(url);
		this.encoding = encoding ;
	}

	/**
	 * handler for request exception
	 */
	private HttpRequestRetryHandler mReqRetryHandler = new HttpRequestRetryHandler() {
		@Override
		public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
			// we will try three times before getting connection
			if (executionCount >= MAX_REQUEST_RETRY_COUNTS) {
				// Do not retry if over max retry count
				return false;
			}
			if (exception instanceof NoHttpResponseException) {
				// Retry if the server dropped connection on us
				return true;
			}
			if (exception instanceof SSLHandshakeException) {
				// Do not retry on SSL handshake exception
				return false;
			}

			HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
			boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
			if (!idempotent) {
				// Retry if the request is considered idempotent
				return true;
			}
			return false;

		}
	};
	/**
	 * handler for response return response from server if request is
	 * successful,status code other wise
	 */
	private ResponseHandler<String> mResponseHandler = new ResponseHandler<String>() {
		public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String res = null ;
					try {
						res = new String(EntityUtils.toByteArray(entity), encoding);
					} catch (UnsupportedEncodingException ex) {
						ex.printStackTrace();
						res = new String(EntityUtils.toByteArray(entity));
					}
					entity.consumeContent();
					return res;
				}
			}
			return String.valueOf(statusCode);
		}
	};
	

	/**
	 * get default http client
	 * 
	 * @return
	 */
	public DefaultHttpClient getDefaultHttpClient() {
		BasicHttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIME_OUT);
		HttpConnectionParams.setSoTimeout(params, SOCKET_TIME_OUT);
		/*
		 * 中国移动cmwap代理 HttpHost proxy = new HttpHost("10.0.0.172", 80);
		 * params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		 */
		DefaultHttpClient client = new DefaultHttpClient(params);
		client.setHttpRequestRetryHandler(mReqRetryHandler);
		return client;
	}

	/**
	 * release http connection
	 * 
	 * @param hrb
	 *            请求对象
	 * @param httpclient
	 *            client对象
	 */
	public void abortConnection(final HttpRequestBase hrb, final HttpClient httpclient) {
		if (hrb != null) {
			hrb.abort();
		}
		if (httpclient != null) {
			httpclient.getConnectionManager().shutdown();
		}
	}

	/**
	 * <br>
	 * set the target url <br>
	 * you must invoke this method when you want to change request uri <br>
	 * create at 2012-5-17 上午10:25:45 <br>
	 * modify at 2012-5-17 上午10:25:45
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = utf8URLencode(url);
	}

	/**
	 * get the response as string,using method POST <br>
	 * create at 2012-5-17 上午10:26:54
	 * 
	 * @param params
	 *            request parameter
	 * @return response string,null if request failed
	 */
	public String getResponseAsStringPost(HashMap<String, String> paramsMap) {
		HttpPost request = null;
		HttpClient client = null;
		String responseStr = null;
		try {
			request = new HttpPost(url);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (String key : paramsMap.keySet()) {
				params.add(new BasicNameValuePair(key, paramsMap.get(key)));
			}
			HttpEntity entity = new UrlEncodedFormEntity(params);
			request.setEntity(entity);
			client = getDefaultHttpClient();
			responseStr = client.execute(request, mResponseHandler);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			abortConnection(request, client);
		}
		return responseStr;
	}

	/**
	 * <br>
	 * get the response as {@link HttpEntity},using method POST <br>
	 * recycle the http resource is your responsibility <br>
	 * create at 2012-5-17 上午10:26:54
	 * 
	 * @param params
	 *            request parameter
	 * @return response HttpEntity,null if request failed
	 */
	public HttpEntity getResponseAsEntityPost(HashMap<String, String> paramsMap) {
		HttpPost request = null;
		HttpClient client = null;
		try {
			request = new HttpPost(url);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (String key : paramsMap.keySet()) {
				params.add(new BasicNameValuePair(key, paramsMap.get(key)));
			}
			HttpEntity entity = new UrlEncodedFormEntity(params);
			request.setEntity(entity);
			client = getDefaultHttpClient();
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return response.getEntity();
			} else {
				abortConnection(request, client);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * get the response as string,using method GET <br>
	 * create at 2012-5-17 上午10:26:54
	 * 
	 * @param params
	 *            request parameter
	 * @return response string,null if request failed
	 */
	public String getResponseAsStringGET(HashMap<String, String> paramsMap) {
		HttpGet request = null;
		HttpClient client = null;
		String responseStr = null;
		try {
			String getUrl = makeGetURL(paramsMap);
			getUrl = utf8URLencode(getUrl);
			request = new HttpGet(getUrl);
			client = getDefaultHttpClient();
			responseStr = client.execute(request, mResponseHandler);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			abortConnection(request, client);
		}
		return responseStr;
	}

	/**
	 * <br>
	 * get the response as HttpEntity,using method GET <br>
	 * notice that:you must recycle the HttpEntity resource all by yourself <br>
	 * create at 2012-5-17 上午10:26:54
	 * 
	 * @param params
	 *            request parameter
	 * @return response HttpEntity,null if request failed
	 */
	public HttpEntity getResponseAsEntityGet(HashMap<String, String> paramsMap) {
		HttpGet request = null;
		HttpClient client = null;
		try {
			String getUrl = makeGetURL(paramsMap);
			getUrl = utf8URLencode(getUrl);
			request = new HttpGet(getUrl);
			client = getDefaultHttpClient();
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return response.getEntity();
			} else {
				abortConnection(request, client);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private String makeGetURL(HashMap<String, String> paramsMap) {
		if (paramsMap == null)
			return url;
		StringBuffer paramStr = new StringBuffer("");
		boolean hasQuestion = url.lastIndexOf("?") > 0 ? true : false;
		for (String key : paramsMap.keySet()) {
			paramStr.append("&").append(key).append("=").append(paramsMap.get(key));
		}
		if (!hasQuestion) {
			paramStr.replace(0, 1, "?");
		}
		return url + paramStr.toString();
	}

	/**
	 * utf8
	 * 
	 * @param text
	 * @return
	 */
	public static String utf8URLencode(String url) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < url.length(); i++) {
			char c = url.charAt(i);
			if ((c >= 0) && (c <= 255)) {
				result.append(c);
			} else {
				byte[] b = new byte[0];
				try {
					b = Character.toString(c).getBytes(CHARSET_UTF_8);
				} catch (Exception ex) {
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					result.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return result.toString();
	}

	/**
	 * 处理302跳转，获取apt包的下载地址
	 * 
	 * @param filePaht
	 *            :文件本地保存目录,url:间接地址
	 * @return 下载url
	 */
	public static String getRedirectionURL(String url) {
		int retryCount = 0;
		String redirectUrl = url;

		if (url.contains(".aspx") || url.contains(".ashx")) {
			while (retryCount < MAX_REQUEST_RETRY_COUNTS) {
				try {
					HttpParams params = new BasicHttpParams();
					// 设置连接超时和 Socket 超时，以及 Socket 缓存大小
					HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIME_OUT);
					HttpConnectionParams.setSoTimeout(params, SOCKET_TIME_OUT);
					HttpConnectionParams.setSocketBufferSize(params, BUFFER_SIZE * 4);
					// 设置重定向，缺省为 true
					HttpClientParams.setRedirecting(params, false);
					HttpClient client = new DefaultHttpClient(params);
					url = url.replaceAll(" ", "%20");
					HttpGet request = new HttpGet(url);
					HttpResponse response = client.execute(request);
					Header head = response.getFirstHeader("Location");
					if (head != null) {
						url = head.getValue();
						redirectUrl = url;
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					retryCount++;
					if (retryCount == MAX_REQUEST_RETRY_COUNTS){
						return null ;
					}
					try {
						Thread.sleep(RETRY_SLEEP_TIME);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		}

		return redirectUrl;
	}
	
	/**
	 * <p>通过url获取document对象</p>
	 * 
	 * <p>date: 2012-10-9 下午02:29:03
	 * @author pdw
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws FactoryConfigurationError
	 * @throws SAXException
	 * @return
	 */
	public Document getDocument() throws IOException, ParserConfigurationException, FactoryConfigurationError, SAXException {
		HttpEntity entity = getResponseAsEntityGet(null);
		if (entity == null)
			return null ;
		InputStream stream = entity.getContent();
		if (stream == null) {
			return null;
		}
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource inputSource = new InputSource(stream);
		Document document = builder.parse(inputSource);
		stream.close();
		return document;
	}

	/**
	 * 
	 * <p>如果原URL带有参数则补&key=value,否则补上?key=value</p>
	 * 
	 * <p>date: 2012-10-9 下午02:29:53
	 * @param sb
	 *            现有的URL的StringBuffer
	 * @param key
	 *            参数
	 * @param value
	 *            参数
	 */
	public static void appendAttrValue(StringBuffer sb, String key, String... values) {
		if (sb.indexOf("?" + key + "=") != -1 || sb.indexOf("&" + key + "=") != -1) {
			return;
		}

		for (String value : values) {
			if (sb.indexOf("?") == -1) {
				sb.append("?");
			} else {
				sb.append("&");
			}
			sb.append(key);
			sb.append("=");
			sb.append(value);
		}
	}
	
	/**
	 * <p>请求url,用作某种成功后的回调</p>
	 * <p>eg.某款app下载成功后需回调服务器的url标识成功下载</p>
	 * 
	 * <p>date: 2012-10-9 下午03:12:42
	 * @author pdw
	 * @return 是否回调成功
	 */
	public boolean httpFeedback() {
		if (this.url == null) {
			Log.e(TAG, "feed back error : url can't be null") ;
			return false ;
		}
		HttpGet request = null;
		HttpClient client = null;
		boolean success = false ;
		try {
			request = new HttpGet(this.url);
			client = getDefaultHttpClient();
			HttpResponse response = client.execute(request);
			success = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK ;
			HttpEntity httpEntity = response.getEntity();
			if (httpEntity != null)
				httpEntity.consumeContent();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			abortConnection(request, client);
		}
		return success ;
	}
}
