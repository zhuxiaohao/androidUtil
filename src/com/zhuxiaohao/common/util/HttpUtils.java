package com.zhuxiaohao.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import android.os.AsyncTask;
import android.util.Log;

import com.zhuxiaohao.common.constant.HttpConstants;
import com.zhuxiaohao.common.entity.HttpRequest;
import com.zhuxiaohao.common.entity.HttpResponse;
import com.zhuxiaohao.common.service.HttpCache;

/**
 * 
 * ClassName: HttpUtils <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2014年11月20日 下午4:55:17 <br/>
 * Http网络工具类<br/>
 * 主要包括httpGet、httpPost以及http参数相关方法<br/>
 * 以httpGet为例： static HttpResponse<br/>
 * httpGet(HttpRequest request) <br/>
 * static HttpResponse<br/>
 * httpGet(java.lang.StringhttpUrl) <br/>
 * static String httpGetString(String httpUrl)<br/>
 * 包含以上三个方法，默认使用gzip压缩，使用bufferedReader提高读取速度。
 * HttpRequest中可以设置url、timeout、userAgent等其他http参数
 * HttpResponse中可以获取返回内容、http响应码、http过期时间(Cache-Control的max-age和expires)等
 * 前两个方法可以进行高级参数设置及丰富内容返回，第三个方法可以简单的传入url获取返回内容，httpPost类似。
 * 更详细的设置可以直接使用HttpURLConnection或apache的HttpClient。
 * 
 * @author chenhao
 * @version
 * @since JDK 1.6
 */
public class HttpUtils {

	private static final String TAG = "HttpUtils";
	/** url和对位分离器 **/
	public static final String URL_AND_PARA_SEPARATOR = "?";
	/** 参数分隔符 **/
	public static final String PARAMETERS_SEPARATOR = "&";
	/** 路径分隔符 **/
	public static final String PATHS_SEPARATOR = "/";
	/** 等号 **/
	public static final String EQUAL_SIGN = "=";
	/** get **/
	public static final int METHOD_GET = 1;
	/** post **/
	public static final int METHOD_POST = 2;
	/** url */
	public static final String BASE_URL = "";

	private HttpUtils() {
		throw new AssertionError();
	}

	/**
	 * 同步获取数据
	 * <ul>
	 * <li>使用gzip压缩默认</li>
	 * <li>使用bufferedReader来提高阅读速度</li>
	 * </ul>
	 * 
	 * @param request
	 * @return url的反应,如果为空代表http错误
	 */
	public static HttpResponse httpGet(HttpRequest request) {
		if (request == null) {
			return null;
		}
		BufferedReader input = null;
		HttpURLConnection con = null;
		try {
			URL url = new URL(request.getUrl());
			try {
				HttpResponse response = new HttpResponse(request.getUrl());
				// 默认的gzip编码
				con = (HttpURLConnection) url.openConnection();
				setURLConnection(request, con);
				input = new BufferedReader(new InputStreamReader(con.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String s;
				while ((s = input.readLine()) != null) {
					sb.append(s).append("\n");
				}
				response.setResponseBody(sb.toString());
				setHttpResponse(con, response);
				return response;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} finally {
			// 关闭缓冲
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// 断开连接所以他们持有的释放资源
			// 可能是关闭或重用
			if (con != null) {
				con.disconnect();
			}
		}

		return null;
	}

	/**
	 * 同步获取数据
	 * 
	 * @param httpUrl
	 * @return url的反应,如果为空代表http错误
	 * @see HttpUtils#httpGet(HttpRequest)
	 */
	public static HttpResponse httpGet(String httpUrl) {
		return httpGet(new HttpRequest(httpUrl));
	}

	/**
	 * 同步获取数据
	 * 
	 * @param request
	 * @return url的内容,如果为空代表http错误
	 * @see HttpUtils#httpGet(HttpRequest)
	 */
	public static String httpGetString(HttpRequest request) {
		HttpResponse response = httpGet(request);
		return response == null ? null : response.getResponseBody();
	}

	/**
	 * 同步获取数据
	 * 
	 * @param httpUrl
	 * @return url的内容,如果为空代表http错误
	 * @see HttpUtils#httpGet(HttpRequest)
	 */
	public static String httpGetString(String httpUrl) {
		HttpResponse response = httpGet(new HttpRequest(httpUrl));
		return response == null ? null : response.getResponseBody();
	}

	/**
	 * 异步获取数据
	 * <ul>
	 * <li>它来自网络的数据异步的.</li>
	 * <li>如果你想获得数据同步,使用 {@link #httpGet(HttpRequest)}or
	 * {@link #httpGetString(HttpRequest)}</li>
	 * </ul>
	 * 
	 * @param url
	 * @param 监听器监听器可以做HttpGet之前或之后
	 *            。这可以为空,如果你不想做点什么
	 */
	public static void httpGet(String url, HttpListener listener) {
		new HttpStringAsyncTask(listener).execute(url);
	}

	/**
	 * 异步获取数据
	 * <ul>
	 * <li>它获取数据或网络异步的.</li>
	 * <li>如果你想获得数据同步,使用 {@link HttpCache#httpGet(HttpRequest)} or
	 * {@link HttpCache#httpGetString(HttpRequest)}</li>
	 * </ul>
	 * @param request
	 * @param 监听器监听器可以做HttpGet之前或之后这可以为空,如果你不想做点什么
	 */
	public static void httpGet(HttpRequest request, HttpListener listener) {
		new HttpRequestAsyncTask(listener).execute(request);
	}

	/**
	 * http 请求
	 * <ul>
	 * <li>使用gzip压缩默认</li>
	 * <li>使用bufferedReader来提高阅读速度</li>
	 * </ul>
	 * @param httpUrl
	 * @param paras
	 * @return url的反应,如果为空代表http错误
	 */
	public static HttpResponse httpPost(HttpRequest request) {
		if (request == null) {
			return null;
		}
		BufferedReader input = null;
		HttpURLConnection con = null;
		try {
			URL url = new URL(request.getUrl());
			try {
				HttpResponse response = new HttpResponse(request.getUrl());
				// default gzip encode
				con = (HttpURLConnection) url.openConnection();
				setURLConnection(request, con);
				con.setRequestMethod("POST");
				con.setDoOutput(true);
				String paras = request.getParas();
				if (!StringUtils.isEmpty(paras)) {
					con.getOutputStream().write(paras.getBytes());
				}
				input = new BufferedReader(new InputStreamReader(con.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String s;
				while ((s = input.readLine()) != null) {
					sb.append(s).append("\n");
				}
				response.setResponseBody(sb.toString());
				setHttpResponse(con, response);
				return response;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} finally {
			// 关闭缓冲
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// 断开连接所以他们持有的释放资源
			// 可能是关闭或重用
			if (con != null) {
				con.disconnect();
			}
		}

		return null;
	}

	/**
	 * http 请求
	 * 
	 * @param httpUrl
	 * @return url的反应,如果为空代表http错误
	 * @see HttpUtils#httpPost(HttpRequest)
	 */
	public static HttpResponse httpPost(String httpUrl) {
		return httpPost(new HttpRequest(httpUrl));
	}

	/**
	 * http 请求
	 * 
	 * @param httpUrl
	 * @return the content of the url, if null represents http error
	 * @see HttpUtils#httpPost(HttpRequest)
	 */
	public static String httpPostString(String httpUrl) {
		HttpResponse response = httpPost(new HttpRequest(httpUrl));
		return response == null ? null : response.getResponseBody();
	}

	/**
	 * http post
	 * 
	 * @param httpUrl
	 * @param parasMap帕拉斯图,关键是帕拉的名字,值是对位的价值。将字符串转换为
	 * @return url的内容,如果为空代表http错误
	 * @see HttpUtils#httpPost(HttpRequest)
	 */
	public static String httpPostString(String httpUrl, Map<String, String> parasMap) {
		HttpResponse response = httpPost(new HttpRequest(httpUrl, parasMap));
		return response == null ? null : response.getResponseBody();
	}

	/**
	 * 连接url和参数
	 * 
	 * <pre>
	 * getUrlWithParas(null, {(a, b)})                        =   "?a=b";
	 * getUrlWithParas("baidu.com", {})                       =   "baidu.com";
	 * getUrlWithParas("baidu.com", {(a, b), (i, j)})         =   "baidu.com?a=b&i=j";
	 * getUrlWithParas("baidu.com", {(a, b), (i, j), (c, d)}) =   "baidu.com?a=b&i=j&c=d";
	 * </pre>
	 * 
	 * @param url
	 *            url
	 * @param parasMap参数,关键参数的名字,值是对位的价值
	 * @return 如果url是null,过程是空字符串
	 */
	public static String getUrlWithParas(String url, Map<String, String> parasMap) {
		StringBuilder urlWithParas = new StringBuilder(StringUtils.isEmpty(url) ? "" : url);
		String paras = joinParas(parasMap);
		if (!StringUtils.isEmpty(paras)) {
			urlWithParas.append(URL_AND_PARA_SEPARATOR).append(paras);
		}
		return urlWithParas.toString();
	}

	/**
	 * 连接url和编码的参数
	 * 
	 * @param url
	 * @param parasMap
	 * @return
	 * @see #getUrlWithParas(String, Map)
	 * @see StringUtils#utf8Encode(String)
	 */
	public static String getUrlWithValueEncodeParas(String url, Map<String, String> parasMap) {
		StringBuilder urlWithParas = new StringBuilder(StringUtils.isEmpty(url) ? "" : url);
		String paras = joinParasWithEncodedValue(parasMap);
		if (!StringUtils.isEmpty(paras)) {
			urlWithParas.append(URL_AND_PARA_SEPARATOR).append(paras);
		}
		return urlWithParas.toString();
	}

	/**
	 * 加入参数
	 * @param parasMap参数,关键是参数的名字,值是对位的价值
	 * @return join key and value with {@link #EQUAL_SIGN}, join keys with {@link #PARAMETERS_SEPARATOR}
	 */
	public static String joinParas(Map<String, String> parasMap) {
		if (parasMap == null || parasMap.size() == 0) {
			return null;
		}

		StringBuilder paras = new StringBuilder();
		Iterator<Map.Entry<String, String>> ite = parasMap.entrySet().iterator();
		while (ite.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) ite.next();
			paras.append(entry.getKey()).append(EQUAL_SIGN).append(entry.getValue());
			if (ite.hasNext()) {
				paras.append(PARAMETERS_SEPARATOR);
			}
		}
		return paras.toString();
	}

	/**
	 * 加入参数编码值
	 * @param parasMap
	 * @return
	 * @see #joinParas(Map)
	 * @see StringUtils#utf8Encode(String)
	 */
	public static String joinParasWithEncodedValue(Map<String, String> parasMap) {
		StringBuilder paras = new StringBuilder("");
		if (parasMap != null && parasMap.size() > 0) {
			Iterator<Map.Entry<String, String>> ite = parasMap.entrySet().iterator();
			try {
				while (ite.hasNext()) {
					Map.Entry<String, String> entry = (Map.Entry<String, String>) ite.next();
					paras.append(entry.getKey()).append(EQUAL_SIGN).append(StringUtils.utf8Encode(entry.getValue()));
					if (ite.hasNext()) {
						paras.append(PARAMETERS_SEPARATOR);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return paras.toString();
	}

	/**
	 * 一对一键和值附加到url
	 * 
	 * @param url
	 * @param paraKey
	 * @param paraValue
	 * @return
	 */
	public static String appendParaToUrl(String url, String paraKey, String paraValue) {
		if (StringUtils.isEmpty(url)) {
			return url;
		}

		StringBuilder sb = new StringBuilder(url);
		if (!url.contains(URL_AND_PARA_SEPARATOR)) {
			sb.append(URL_AND_PARA_SEPARATOR);
		} else {
			sb.append(PARAMETERS_SEPARATOR);
		}
		return sb.append(paraKey).append(EQUAL_SIGN).append(paraValue).toString();
	}

	private static final SimpleDateFormat GMT_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);

	/**
	 * 解析gmt时间长
	 * @param gmtTime likes Thu, 11 Apr 2013 10:20:30 GMT
	 * @return -1 represents exception otherwise time in milliseconds
	 */
	public static long parseGmtTime(String gmtTime) {
		try {
			return GMT_FORMAT.parse(gmtTime).getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 设置HttpRequest HttpURLConnection
	 * 
	 * @param request
	 *            source request
	 * @param urlConnection
	 *            destin url connection
	 */
	private static void setURLConnection(HttpRequest request, HttpURLConnection urlConnection) {
		if (request == null || urlConnection == null) {
			return;
		}

		setURLConnection(request.getRequestProperties(), urlConnection);
		if (request.getConnectTimeout() >= 0) {
			urlConnection.setConnectTimeout(request.getConnectTimeout());
		}
		if (request.getReadTimeout() >= 0) {
			urlConnection.setReadTimeout(request.getReadTimeout());
		}
	}

	/**
	 * 设置HttpURLConnection属性
	 * 
	 * @param requestProperties
	 * @param urlConnection
	 */
	public static void setURLConnection(Map<String, String> requestProperties, HttpURLConnection urlConnection) {
		if (MapUtils.isEmpty(requestProperties) || urlConnection == null) {
			return;
		}

		for (Map.Entry<String, String> entry : requestProperties.entrySet()) {
			if (!StringUtils.isEmpty(entry.getKey())) {
				urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * 设置HttpURLConnection HttpResponse
	 * 
	 * @param urlConnection源url连接
	 * @param response命运的反应
	 */
	private static void setHttpResponse(HttpURLConnection urlConnection, HttpResponse response) {
		if (response == null || urlConnection == null) {
			return;
		}
		try {
			response.setResponseCode(urlConnection.getResponseCode());
		} catch (IOException e) {
			response.setResponseCode(-1);
		}
		response.setResponseHeader(HttpConstants.EXPIRES, urlConnection.getHeaderField("Expires"));
		response.setResponseHeader(HttpConstants.CACHE_CONTROL, urlConnection.getHeaderField("Cache-Control"));
	}

	/**
	 * AsyncTask字符串url获取数据
	 * 
	 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a>
	 *         2013-11-15
	 */
	private static class HttpStringAsyncTask extends AsyncTask<String, Void, HttpResponse> {
		private HttpListener listener;
		public HttpStringAsyncTask(HttpListener listener) {
			this.listener = listener;
		}
		protected HttpResponse doInBackground(String... url) {
			if (ArrayUtils.isEmpty(url)) {
				return null;
			}
			return httpGet(url[0]);
		}
		protected void onPreExecute() {
			if (listener != null) {
				listener.onPreGet();
			}
		}
		protected void onPostExecute(HttpResponse httpResponse) {
			if (listener != null) {
				listener.onPostGet(httpResponse);
			}
		}
	}

	/**
	 * HttpRequest AsyncTask来获取数据
	 * 
	 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a>
	 *         2013-11-15
	 */
	private static class HttpRequestAsyncTask extends AsyncTask<HttpRequest, Void, HttpResponse> {

		private HttpListener listener;

		public HttpRequestAsyncTask(HttpListener listener) {
			this.listener = listener;
		}

		protected HttpResponse doInBackground(HttpRequest... httpRequest) {
			if (ArrayUtils.isEmpty(httpRequest)) {
				return null;
			}
			return httpGet(httpRequest[0]);
		}

		protected void onPreExecute() {
			if (listener != null) {
				listener.onPreGet();
			}
		}

		protected void onPostExecute(HttpResponse httpResponse) {
			if (listener != null) {
				listener.onPostGet(httpResponse);
			}
		}
	}

	/**
	 * HttpListener,可以做一些HttpGet之前或之后
	 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a>
	 *         2013-11-15
	 */
	public static abstract class HttpListener {

		/**
		 * Runs on the UI thread before httpGet.<br/>
		 * <ul>
		 * <li>this can be null if you not want to do something</li>
		 * </ul>
		 */
		protected void onPreGet() {
		}

		/**
		 * Runs on the UI thread after httpGet. The httpResponse is returned by
		 * httpGet.
		 * <ul>
		 * <li>this can be null if you not want to do something</li>
		 * </ul>
		 * 
		 * @param httpResponse
		 *            get by the url
		 */
		protected void onPostGet(HttpResponse httpResponse) {
		}
	}

	private static final int TIMEOUT_IN_MILLIONS = 5000;

	public interface CallBack {
		void onRequestComplete(String result);
	}

	/**
	 * 异步的Get请求
	 * @param urlStr
	 * @param callBack
	 */
	public static void doGetAsyn(final String urlStr, final CallBack callBack) {
		new Thread() {
			public void run() {
				try {
					String result = doGet(urlStr);
					if (callBack != null) {
						callBack.onRequestComplete(result);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			};
		}.start();
	}

	/**
	 * 异步的Post请求
	 * @param urlStr
	 * @param params
	 * @param callBack
	 * @throws Exception
	 */
	public static void doPostAsyn(final String urlStr, final String params, final CallBack callBack) throws Exception {
		new Thread() {
			public void run() {
				try {
					String result = doPost(urlStr, params);
					if (callBack != null) {
						callBack.onRequestComplete(result);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			};
		}.start();

	}

	/**
	 * Get请求，获得返回数据
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String urlStr) {
		URL url = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			if (conn.getResponseCode() == 200) {
				is = conn.getInputStream();
				baos = new ByteArrayOutputStream();
				int len = -1;
				byte[] buf = new byte[128];

				while ((len = is.read(buf)) != -1) {
					baos.write(buf, 0, len);
				}
				baos.flush();
				return baos.toString();
			} else {
				throw new RuntimeException(" responseCode is not 200 ... ");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
			}
			try {
				if (baos != null)
					baos.close();
			} catch (IOException e) {
			}
			conn.disconnect();
		}

		return null;

	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * @param url     发送请求的 URL
	 * @param param   请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 * @throws Exception
	 */
	public static String doPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");
			conn.setUseCaches(false);
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);

			if (param != null && !param.trim().equals("")) {
				// 获取URLConnection对象对应的输出流
				out = new PrintWriter(conn.getOutputStream());
				// 发送请求参数
				out.print(param);
				// flush输出流的缓冲
				out.flush();
			}
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 获取输入流
	 * @param uri   网络地址
	 * @param params   请求参数
	 * @param method      请求方式
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static InputStream getStream(String uri, ArrayList<BasicNameValuePair> params, int method) throws ClientProtocolException, IOException {
		InputStream in = null;
		HttpEntity entity = getEntity(uri, params, method);
		if (entity != null)
			in = entity.getContent();
		Log.i(TAG, "getStream(String uri,ArrayList<BasicNameValuePair> params,int method)");
		return in;
	}

	/**
	 * 获取字节流
	 * @param uri    请求地址
	 * @param params    请求参数
	 * @param method    请求方式
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static byte[] getBytes(String uri, ArrayList<BasicNameValuePair> params, int method) throws ClientProtocolException, IOException {
		byte[] bytes = null;
		HttpEntity entity = getEntity(uri, params, method);
		if (entity != null)
			bytes = EntityUtils.toByteArray(entity);
		Log.i(TAG, "getBytes(String uri,ArrayList<BasicNameValuePair> params,int method)");
		return bytes;
	}

	/**
	 * 获取返回的实体字符串
	 * @param uri         请求地址
	 * @param params   请求参数
	 * @param method     请求方式
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String toString(String uri, ArrayList<BasicNameValuePair> params, int method) throws ClientProtocolException, IOException {

		Log.i(TAG, "toString(String uri,ArrayList<BasicNameValuePair> params,int method)");
		HttpEntity entity = getEntity(uri, params, method);
		if (entity != null)
			return EntityUtils.toString(entity);
		return null;
	}

	/**
	 * 获取返回实体
	 * @param uri    请求地址
	 * @param params   请求参数
	 * @param method  请求方式
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static HttpEntity getEntity(String uri, ArrayList<BasicNameValuePair> params, int method) throws ClientProtocolException, IOException {
		HttpEntity entity = null;
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
		HttpUriRequest request = null;
		switch (method) {
		case METHOD_GET:
			StringBuilder sb = new StringBuilder(uri);
			if (params != null && !params.isEmpty()) {
				sb.append('?');
				for (BasicNameValuePair pair : params) {
					sb.append(pair.getName()).append('=').append(pair.getValue()).append('&');
				}
				sb.deleteCharAt(sb.length() - 1);
			}
			request = new HttpGet(sb.toString());
			break;
		case METHOD_POST:
			request = new HttpPost(uri);
			if (params != null && !params.isEmpty()) {
				UrlEncodedFormEntity requestEntity = new UrlEncodedFormEntity(params);
				((HttpPost) request).setEntity(requestEntity);
			}
			break;
		}
		try {
			org.apache.http.HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				entity = response.getEntity();
			}
			Log.i(TAG, "getEntity(String uri,ArrayList<BasicNameValuePair> params,int method)");

		} catch (ClientProtocolException e) {
			Log.e("HttpConnectionUtil", e.getMessage(), e);
		} catch (InterruptedIOException e) {
			Log.e("http", "请求超时");
			// TODO: handle exception
		} catch (Exception e) {
			Log.e("HttpConnectionUtil", e.getMessage(), e);
		}

		return entity;
	}

	/**
	 * 获取返回实体长度
	 * @param entity 需要获取长度的实体
	 * @return
	 */
	public static long getLength(HttpEntity entity) {
		Log.i(TAG, "getLength(HttpEntity entity)");
		if (entity != null) {
			return entity.getContentLength();
		}
		return -1;
	}

	/**
	 * 通过实体获取输入流
	 * @param entity
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static InputStream getStream(HttpEntity entity) throws IllegalStateException, IOException {
		Log.i(TAG, "getStream(HttpEntity entity)");
		if (entity != null) {
			return entity.getContent();
		}
		return null;
	}

	public static InputStream getStream(String url) throws IllegalStateException, IOException {
		Log.i(TAG, "getStream(HttpEntity entity)");
		if (url != null) {
			HttpEntity entity = getEntity(url, null, METHOD_GET);
			if (entity != null) {
				return entity.getContent();
			}
		}
		return null;
	}

	/**
	 * 获取JSon数据
	 * @param 参数个数NameValuePair请求属性
	 * @param uri请求API
	 * @return JSon数据或“”。
	 */
	public static String getHttpRequestString(List<NameValuePair> params, String uri) {
		String strResult = "";
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
			// 相应超时
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
			HttpPost post = new HttpPost(uri);
			HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
			post.setEntity(entity);
			// 获得HttpResponse对象
			org.apache.http.HttpResponse httpResponse = (org.apache.http.HttpResponse) client.execute(post);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 取得返回的数据
				strResult = EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (ConnectTimeoutException e) {
			// DialogUtils.showToast(this.activity, "连接超时", 2000);
		} catch (InterruptedIOException e) {
			// DialogUtils.showToast(this.activity, "响应超时", 2000);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strResult;
	}

	/**
	 * 获取JSon数据
	 * @param 参数个数  NameValuePair请求属性
	 * @param uri  请求API
	 * @return JSon数据或“”。
	 */
	public static String getJsonString(List<NameValuePair> params, String uri) {
		String strResult = "";
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
			// 相应超时
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
			HttpPost post = new HttpPost(uri);
			post.addHeader("Accept-Encoding", "gzip");
			HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
			post.setEntity(entity);
			// 获得HttpResponse对象
			org.apache.http.HttpResponse httpResponse = client.execute(post);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 取得返回的数据
				// strResult = EntityUtils.toString(httpResponse.getEntity());
				strResult = HttpUtils.readHttpResponse(httpResponse);
			}
		} catch (ConnectTimeoutException e) {
			// DialogUtils.showToast(this.activity, "连接超时", 2000);
			return strResult;
		} catch (InterruptedIOException e) {
			return strResult;
			// DialogUtils.showToast(this.activity, "响应超时", 2000);
		} catch (IOException e) {
			e.printStackTrace();
			return strResult;
		}
		return strResult;
	}

	/**
	 * 获取新浪微博数据
	 * @param url   请求地址
	 * @return
	 */
	public static String getWeiboString(String url) {
		HttpGet httpRequest = new HttpGet(url);
		String strResult = "";
		try {
			/* 发送请求并等待响应 */
			org.apache.http.HttpResponse httpResponse = getNewHttpClient().execute(httpRequest);
			/* 若状态码为200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// strResult = EntityUtils.toString(httpResponse.getEntity());
				/* 去没有用的字符 */
				// strResult = eregi_replace(“(\r\n|\r|\n|\n\r)”, “”,
				// strResult);
				strResult = readHttpResponse((org.apache.http.HttpResponse) httpResponse);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strResult;
	}

	/**
	 * 解析HttpResponse些微GZip压缩
	 * @param response
	 * @return
	 */
	public static String readHttpResponse(org.apache.http.HttpResponse response) {
		String result = "";
		HttpEntity entity = response.getEntity();
		InputStream inputStream;
		try {
			inputStream = entity.getContent();
			ByteArrayOutputStream content = new ByteArrayOutputStream();

			Header header = response.getFirstHeader("Content-Encoding");
			if (header != null && header.getValue().toLowerCase().indexOf("gzip") > -1) {
				inputStream = new GZIPInputStream(inputStream);
			}

			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = inputStream.read(sBuffer)) != -1) {
				content.write(sBuffer, 0, readBytes);
			}
			result = new String(content.toByteArray());
			return result;
		} catch (IllegalStateException e) {
		} catch (IOException e) {
		}
		return result;
	}

	/**
	 * 新浪微博专用
	 * @return
	 */
	public static HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}
}
