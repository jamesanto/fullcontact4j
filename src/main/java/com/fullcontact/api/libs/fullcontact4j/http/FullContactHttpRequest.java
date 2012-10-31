package com.fullcontact.api.libs.fullcontact4j.http;

import com.fullcontact.api.libs.fullcontact4j.FullContactException;
import com.fullcontact.api.libs.fullcontact4j.config.Constants;
import com.google.gson.JsonObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.util.URIUtil;

import java.io.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class FullContactHttpRequest {

    public static String sendPersonRequest(String paramString)
            throws FullContactException {
        return sendRequest((Constants.API_URL_PERSON + paramString));
    }

    public static String sendNameNormalizationRequest(String paramString)
            throws FullContactException {
        return sendRequest((Constants.API_URL_NAME_NORMALIZATION + paramString));
    }

    public static String sendNameDeducerRequest(String paramString)
            throws FullContactException {
        return sendRequest((Constants.API_URL_NAME_DEDUCER + paramString));
    }

    public static String sendNameSimilarityRequest(String paramString)
            throws FullContactException {
        return sendRequest((Constants.API_URL_NAME_SIMILARITY + paramString));
    }

    public static String sendNameStatsRequest(String paramString)
            throws FullContactException {
        return sendRequest((Constants.API_URL_NAME_STATS + paramString));
    }

    public static String sendNameParserRequest(String paramString)
            throws FullContactException {
        return sendRequest((Constants.API_URL_NAME_PARSER + paramString));
    }

    public static String sendPersonEnhancedDataRequest(String paramString)
            throws FullContactException {
        return sendRequest((Constants.API_URL_PERSON_ENHANCED_DATA + paramString));
    }

    public static String sendLocationNormalizationRequest(String paramString)
            throws FullContactException {
        return sendRequest((Constants.API_URL_LOCATION_NORMALIZATION + paramString));
    }

    public static String sendLocationEnrichmentRequest(String paramString)
            throws FullContactException {
        return sendRequest((Constants.API_URL_LOCATION_ENRICHMENT + paramString));
    }

    public static String sendRequest(String apiUrl)
            throws FullContactException {
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(apiUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    url.openStream(), Constants.UTF_8_CHARSET));
            String str;
            while ((str = in.readLine()) != null) {
                buffer.append(str);
            }
            in.close();
        } catch (MalformedURLException e) {
            throw new FullContactException(e.getMessage());
        } catch (IOException e) {
            throw new FullContactException(e.getMessage());
        }
        return buffer.toString();
    }

    public static String postCardResponse(String paramString, Map<String, String> parameters) {
        String url = Constants.API_URL_CARDSHARK_UPLOAD;// + paramString;
//        String url = "http://requestb.in/14qwmle1";// + paramString;
        Map<String, String> headers = new HashMap<String, String>();
        int connectionTimeout = 6000;
        int responseTimeout = 60000;
        return postResponseAsJson(url, parameters, headers, connectionTimeout, responseTimeout);
    }

    public static String postResponseAsJson(String url, Map<String, String> parameters, Map<String, String> headers, int connectTimeout, int responseTimeout) {
        try {
            HttpClient client = new HttpClient();
            client.getParams().setSoTimeout(responseTimeout);
            client.getParams().setConnectionManagerTimeout(connectTimeout);
            PostMethod method = new PostMethod(url);

            for (String headerName : headers.keySet()) {
                String headerValue = headers.get(headerName);
                method.setRequestHeader(headerName, headerValue);
            }

            JsonObject jo = new JsonObject();
            for (String paramName : parameters.keySet()) {
                jo.addProperty(paramName, parameters.get(paramName));
            }
            String jsonString = jo.toString();
            System.out.println(jsonString + "\n======================\n\n\n\n");

            NameValuePair[] nvps = new NameValuePair[3];
            nvps[0] = new NameValuePair("data", jsonString);
            nvps[1] = new NameValuePair("webhookUrl", "http%3A%2F%2Frequestb.in%2F14qwmle1");
            nvps[2] = new NameValuePair("apiKey", "your_api_key"); //TODO: put API KEY
            method.setRequestBody(nvps);

//            RequestEntity requestEntity = new StringRequestEntity(jsonString, "application/json", "UTF-8");
//            method.setRequestEntity(requestEntity);

            int responseCode = client.executeMethod(method);
            String response = method.getResponseBodyAsString();
            method.releaseConnection();

            return response;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + " - " + url, e);
        }
    }

    public static String postResponse(String url, Map<String, String> parameters, Map<String, String> headers, int connectTimeout, int responseTimeout) {
        try {
            HttpClient client = new HttpClient();
            client.getParams().setSoTimeout(responseTimeout);
            client.getParams().setConnectionManagerTimeout(connectTimeout);
            PostMethod method = new PostMethod(url);

            for (String headerName : headers.keySet()) {
                String headerValue = headers.get(headerName);
                method.setRequestHeader(headerName, headerValue);
            }

            NameValuePair[] content = new NameValuePair[parameters.size()];
            int index = 0;
            for (String paramName : parameters.keySet()) {
                content[index++] = new NameValuePair(paramName, parameters.get(paramName));
            }

            method.setRequestBody(content);
            int responseCode = client.executeMethod(method);
            String response = method.getResponseBodyAsString();
            method.releaseConnection();

            return response;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + " - " + url, e);
        }
    }
}
