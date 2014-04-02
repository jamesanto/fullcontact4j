package com.fullcontact.api.libs.fullcontact4j.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;

import com.fullcontact.api.libs.fullcontact4j.FullContactException;
import com.fullcontact.api.libs.fullcontact4j.Utils;
import com.fullcontact.api.libs.fullcontact4j.builders.CardReaderUploadRequestBuilder;
import com.fullcontact.api.libs.fullcontact4j.config.Constants;
import com.fullcontact.api.libs.fullcontact4j.enums.CardReaderCasing;
import com.fullcontact.api.libs.fullcontact4j.enums.CardReaderSandboxStatus;
import com.fullcontact.api.libs.fullcontact4j.enums.ResponseFormat;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@SuppressWarnings("deprecation")
public class FullContactHttpRequest {

	private static HttpClient httpClient = null;

	/**
	 * customRequestProperties
	 * 
	 * If populated, these will be added as request properties on the Http
	 * connection
	 * 
	 * For example:
	 * 
	 * HashMap<String,String> headers = new HashMap<String,String>();
	 * headers.put("My-Header","My-Value");
	 * 
	 * FullContactHttpRequest.setCustomRequestProperties(headers);
	 * 
	 * Now <b>every</b> request will have the properties added to 'headers' in
	 * the connection
	 * 
	 * Note: User-Agent by default is already added To override, use
	 * {@link FullContactHttpRequest#setUserAgent}
	 * 
	 * 
	 * Important: As mentioned, setting these request parameters sets them for
	 * good and they will be present in every request.
	 * 
	 * 
	 * **/
	private static HashMap<String, String> customRequestProperties = new HashMap<String, String>();

	public static void setCustomRequestProperties(HashMap<String, String> requestProperties) throws NullPointerException {
		if (requestProperties == null)
			throw new NullPointerException("requestProperties");
		customRequestProperties = requestProperties;
	}

	public static String sendPersonRequest(String paramString) throws FullContactException {
		return sendRequest((Constants.API_URL_PERSON + paramString));
	}

	public static String sendNameNormalizationRequest(String paramString) throws FullContactException {
		return sendRequest((Constants.API_URL_NAME_NORMALIZATION + paramString));
	}

	public static String sendNameDeducerRequest(String paramString) throws FullContactException {
		return sendRequest((Constants.API_URL_NAME_DEDUCER + paramString));
	}

	public static String sendNameSimilarityRequest(String paramString) throws FullContactException {
		return sendRequest((Constants.API_URL_NAME_SIMILARITY + paramString));
	}

	public static String sendNameStatsRequest(String paramString) throws FullContactException {
		return sendRequest((Constants.API_URL_NAME_STATS + paramString));
	}

	public static String sendNameParserRequest(String paramString) throws FullContactException {
		return sendRequest((Constants.API_URL_NAME_PARSER + paramString));
	}

	public static String sendPersonEnhancedDataRequest(String paramString) throws FullContactException {
		return sendRequest((Constants.API_URL_PERSON_ENHANCED_DATA + paramString));
	}

	public static String sendLocationNormalizationRequest(String paramString) throws FullContactException {
		return sendRequest((Constants.API_URL_LOCATION_NORMALIZATION + paramString));
	}

	public static String sendLocationEnrichmentRequest(String paramString) throws FullContactException {
		return sendRequest((Constants.API_URL_LOCATION_ENRICHMENT + paramString));
	}

	private static String FC_USER_AGENT = "fullcontact4j/1.0";
	private static final String STR_USER_AGENT = "User-Agent";

	public static void setUserAgent(String str) {
		FC_USER_AGENT = str;
	}

	public static String sendRequest(String apiUrl) throws FullContactException {
		StringBuffer buffer = new StringBuffer();
		HttpGet get = null;
		try {
			get = new HttpGet(apiUrl);
			HttpResponse response = null;
			response = getHttpClient().execute(get);
			BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String str;
			while ((str = in.readLine()) != null) {
				buffer.append(str);
			}
			in.close();
		} catch (Throwable ex) {
			throw new FullContactException("Fullcontact request failure", ex);
		} finally {
			get.releaseConnection();
			getHttpClient().getConnectionManager().closeExpiredConnections();
			getHttpClient().getConnectionManager().closeIdleConnections(5, TimeUnit.MILLISECONDS);
		}
		return buffer.toString();
	}
	
	public static String sendCardReaderViewRequest(String paramString) throws FullContactException {
		return sendRequest((Constants.API_URL_CARDREADER_VIEW_REQUESTS + paramString));
	}

	public static String sendCardReaderViewRequest(String requestId, String paramString) throws FullContactException {
		if (requestId == null) {
			return sendCardReaderViewRequest(paramString);
		}
		return sendRequest((MessageFormat.format(Constants.API_URL_CARDREADER_VIEW_REQUEST, requestId) + paramString));
	}

	@Deprecated
	public static String sendCardSharkViewRequest(String paramString) throws FullContactException {
		return sendCardReaderViewRequest(paramString);
	}

	@Deprecated
	public static String sendCardSharkViewRequest(String requestId, String paramString) throws FullContactException {
		return sendCardReaderViewRequest(requestId, paramString);
	}

	public static String sendEmailDisposableDomainRequest(String paramString) throws FullContactException {
		return sendRequest((Constants.API_URL_EMAIL_DISPOSABLE_DOMAIN + paramString));
	}

	public static String sendIconsListRequest(String paramString) throws FullContactException {
		return sendRequest((Constants.API_URL_ICON + paramString));
	}

	public static InputStream sendIconRequest(String typeId, int size, String style, String paramString) throws FullContactException {
		String url = MessageFormat.format(Constants.API_URL_ICON_TYPE_ID, typeId, size, style);
		try {
			URLConnection connection = new URL(url + paramString).openConnection();
			connection.setRequestProperty(STR_USER_AGENT, FC_USER_AGENT);
			return connection.getInputStream();
		} catch (IOException e) {
			throw new FullContactException(e.getMessage(), e);
		}
	}

	public static String postCardReaderRequest(String apiKey, CardReaderUploadRequestBuilder.CardReaderUploadRequest request)
			throws FullContactException {
		Map<String, String> queryParams = generateQueryParams(apiKey, request);
		JsonObject jsonObject = new JsonObject();
		try {
			jsonObject.addProperty("front", new String(encodeStreamAsBase64(request.getFrontImage())));
			if (request.getBackImage() != null)
				jsonObject.addProperty("back", new String(encodeStreamAsBase64(request.getBackImage())));
		} catch (Throwable throwable) {
			throw new FullContactException("Failed to encode inputstream content to Base64", throwable);
		}

		byte[] payload = jsonObject.toString().replace("\\r\\n", "").getBytes();
		return postWithGZip(apiKey, Constants.API_URL_CARDREADER_UPLOAD, queryParams, payload, "application/json");
	}

	private static byte[] encodeStreamAsBase64(InputStream is) throws IOException {
		return Base64.encodeBase64(Utils.getBytesFromInputStream(is));
	}

	public static HashMap<String, String> generateQueryParams(String apiKey, CardReaderUploadRequestBuilder.CardReaderUploadRequest request)
			throws FullContactException {
		HashMap<String, String> queryParams = new HashMap<String, String>();
		queryParams.put(Constants.PARAM_WEBHOOK_URL, request.getWebhookUrl());
		queryParams.put(Constants.PARAM_FORMAT, request.getFormat().toString().toLowerCase());
		queryParams.put(Constants.PARAM_VERIFIED, request.getFormat().toString().toLowerCase());
		if (request.getAccessToken() != null)
			queryParams.put(Constants.PARAM_ACCESS_TOKEN, request.getAccessToken());
		if (request.getURID() != null)
			queryParams.put(Constants.PARAM_URID, request.getURID());
		queryParams.putAll(request.getCustomParams());
		if (request.isVerifiedOnly())
			queryParams.put(Constants.PARAM_RETURNED_DATA, "verifiedOnly");
		queryParams.put(Constants.PARAM_VERIFIED, request.getVerification().toString().toLowerCase());
		if (request.getCasing() != CardReaderCasing.Default)
			queryParams.put(Constants.PARAM_CASING, request.getCasing().toString().toLowerCase());
		if (request.isSandbox()) {
			CardReaderSandboxStatus sandboxStatus = request.getSandboxStatus();
			if (sandboxStatus != null)
				queryParams.put(Constants.PARAM_SANDBOX, request.getSandboxStatus().toString());
			else
				throw new FullContactException("sandboxStatus is required if request isSandbox");
		}
		return queryParams;
	}

	@Deprecated
	public static String postCardRequest(Map<String, String> queryParams, InputStream frontStream, InputStream backStream)
			throws FullContactException {
		String apiKey = queryParams.get(Constants.PARAM_API_KEY);
		String webhookUrl = queryParams.get(Constants.PARAM_WEBHOOK_URL);
		String format = queryParams.containsKey(Constants.PARAM_FORMAT) ? queryParams.get(Constants.PARAM_FORMAT) : null;
		ResponseFormat responseFormat = ResponseFormat.JSON;
		if (format != null && format.toLowerCase() == "xml")
			responseFormat = ResponseFormat.XML;

		CardReaderUploadRequestBuilder builder = new CardReaderUploadRequestBuilder();
		builder.setWebhookUrl(webhookUrl).setFormat(responseFormat).setFrontImage(frontStream).setBackImage(backStream);
		return postCardReaderRequest(apiKey, builder.build());
	}

	public static String postBatchRequest(Map<String, String> queryParams, List<String> queries) throws FullContactException {
		JsonObject jsonObject = new JsonObject();
		try {
			jsonObject.add("requests", new Gson().toJsonTree(queries));
		} catch (Throwable throwable) {
			throw new FullContactException("Failed to encode inputstream content to Base64", throwable);
		}
		byte[] payload = jsonObject.toString().replace("\\r\\n", "").getBytes();
		return postWithGZip(queryParams.get(Constants.PARAM_API_KEY), Constants.API_URL_BATCH_PROCESS, queryParams, payload, "application/json");
	}

	private static String postWithGZip(String apiKey, String baseUrl, Map<String, String> params, byte[] data, String contentType)
			throws FullContactException {
		try {
			HttpURLConnection connection = createHttpConnectionForQuery(baseUrl, params);
			addConnectionProperties(contentType, connection);
			connection.setRequestProperty(Constants.API_KEY_HEADER_NAME, apiKey);
			writeDataForConnection(data, connection);
			return readResponse(connection);
		} catch (Throwable throwable) {
			throw new FullContactException("Failed to execute API Request", throwable);
		}
	}

	private static Boolean _shouldCompressTested = false;
	private static Boolean _shouldCompress = true;

	private static Boolean shouldCompress(byte[] data) {
		if (!_shouldCompressTested) {
			GZIPOutputStream gzipOutputStream = null;
			try {
				ByteArrayOutputStream output = new ByteArrayOutputStream(data.length);
				gzipOutputStream = new GZIPOutputStream(output);
				gzipOutputStream.write(data);
				gzipOutputStream.finish();
				gzipOutputStream.flush(); // Throws exception on Android KitKat
			} catch (Exception ex) {
				_shouldCompress = false;
			} finally {
				try {
					gzipOutputStream.close();
				} catch (Exception ignored) {
				}
				_shouldCompressTested = true;
			}
		}
		return _shouldCompress;
	}

	public static HttpURLConnection createHttpConnectionForQuery(String baseUrl, Map<String, String> params) throws IOException {
		String qs = toQueryString(params);
		String fullUrl = baseUrl;
		if (qs.length() > 0) {
			if (!fullUrl.endsWith("?")) {
				fullUrl += "?";
			}
			fullUrl += qs;
		}
		URL url = new URL(fullUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("Connection", "Close");
		return (HttpURLConnection) url.openConnection();
	}

	private static void addConnectionProperties(String contentType, HttpURLConnection connection) throws ProtocolException {
		connection.setRequestMethod("POST");
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setConnectTimeout(60000);
		connection.setReadTimeout(120000);
		connection.setRequestProperty(STR_USER_AGENT, FC_USER_AGENT);
		for (Map.Entry<String, String> requestProperty : customRequestProperties.entrySet()) {
			connection.setRequestProperty(requestProperty.getKey(), requestProperty.getValue());
		}
		connection.setRequestProperty("Content-Type", contentType);
	}

	public static String readResponse(HttpURLConnection connection) throws IOException {
		InputStream inputStream = connection.getInputStream();
		// If the client supports compressing *any* data, then we know we are
		// compressing it
		// and we should expect compressed data coming back
		if (connectionIsGzipped(connection) && shouldCompress("compress".getBytes())) {
			try {
				inputStream = new GZIPInputStream(inputStream);
			} catch (Exception e) { /*
									 * If we were wrong, just read it as a
									 * normal IS
									 */
			}
		}
		return readInputStream(inputStream, connection);
	}

	private static boolean connectionIsGzipped(HttpURLConnection connection) {
		String encoding = connection.getHeaderField("Content-Encoding");
		return encoding != null && "gzip".equals(encoding.toLowerCase());
	}

	private static String readInputStream(InputStream stream, HttpURLConnection connection) throws IOException {
		BufferedReader bufferedReader = null;
		StringBuilder sb = new StringBuilder();
		try {
			InputStreamReader reader = new InputStreamReader(stream);
			bufferedReader = new BufferedReader(reader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} finally {
			bufferedReader.close();
			connection.disconnect();
		}
		return sb.toString();
	}

	public static void writeDataForConnection(byte[] data, HttpURLConnection connection) throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		if (shouldCompress(data)) {
			GZIPOutputStream wr = new GZIPOutputStream(byteStream);
			wr.write(data);
			wr.finish();
			wr.flush();
			connection.setRequestProperty("Accept-Encoding", "gzip");
			connection.setRequestProperty("Content-Encoding", "gzip");
		} else {
			byteStream.write(data, 0, data.length);
		}
		connection.setRequestProperty("Content-Length", "" + Integer.toString(byteStream.size()));
		OutputStream out = connection.getOutputStream();
		byteStream.writeTo(out);
		out.flush();
		out.close();
		byteStream.close();
	}

	private static String toQueryString(Map<String, String> params) throws UnsupportedEncodingException {
		String qs = "";
		for (String paramName : params.keySet()) {
			String paramValue = params.get(paramName);
			if (qs.length() > 0) {
				qs += "&";
			}
			qs += (paramName + "=" + URLEncoder.encode(paramValue, "UTF-8"));
		}
		return qs;
	}

	public synchronized static void setHttpClient(HttpClient httpClient) {
		if (httpClient == null) {
			throw new IllegalArgumentException("HttpClient cannot be null");
		}
		FullContactHttpRequest.httpClient = httpClient;
	}

//	public static void setHttpClientConnectionManager(HttpClientConnectionManager httpClientConnectionManager) {
//		if (httpClientConnectionManager == null) {
//			throw new IllegalArgumentException("HttpClientConnectionManager cannot be null");
//		}
//
//		FullContactHttpRequest.connectionManager = httpClientConnectionManager;
//	}

	public static HttpClient getHttpClient() {
		if (httpClient == null) {
			Header header = new BasicHeader(HttpHeaders.CONNECTION, "Close");
			List<Header> defaultHeaders = new ArrayList<Header>();
			defaultHeaders.add(header);
			httpClient = HttpClientBuilder.create().setDefaultHeaders(defaultHeaders).setConnectionManager(new PoolingHttpClientConnectionManager()).build();
		}
		return httpClient;
	}
}