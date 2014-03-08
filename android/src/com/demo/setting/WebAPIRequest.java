package com.demo.setting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class WebAPIRequest {
	public static String convertStreamToString(InputStream is)
			throws IOException {

		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));

				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				is.close();
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	public Document performPost(String url, String body) {
		Document doc = null;

		try {
			HttpParams basicparams = new BasicHttpParams();
			URI uri = new URI(url);
			HttpPost method = new HttpPost(uri);
			int timeoutConnection = 60000;
			HttpConnectionParams.setConnectionTimeout(basicparams,
					timeoutConnection);
			DefaultHttpClient client = new DefaultHttpClient(basicparams);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			body = body.replaceAll("%20", " ");
			String[] parameters = body.split("&");
			for (int i = 0; i < parameters.length; i++) {
				String[] parameter = parameters[i].split("=");
				if (parameter.length >= 2) {
					nameValuePairs.add(new BasicNameValuePair(parameter[0],
							parameter[1]));
				}
			}
			method.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse res = client.execute(method);
			InputStream data = res.getEntity().getContent();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(data);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}

	public String performPostAsString(String url, String body) {
		String value = null;

		try {
			HttpParams basicparams = new BasicHttpParams();
			URI uri = new URI(url);
			HttpPost method = new HttpPost(uri);
			int timeoutConnection = 60000;
			HttpConnectionParams.setConnectionTimeout(basicparams,
					timeoutConnection);
			DefaultHttpClient client = new DefaultHttpClient(basicparams);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			body = body.replaceAll("%20", " ");
			String[] parameters = body.split("&");
			for (int i = 0; i < parameters.length; i++) {
				String nameOfParameter = parameters[i].substring(0,
						parameters[i].indexOf("="));
				String valueOfParameter = parameters[i].substring(parameters[i]
						.indexOf("=") + 1);
				// String[]parameter = parameters[i].split("=");
				// if(parameter.length>=2){
				nameValuePairs.add(new BasicNameValuePair(nameOfParameter,
						valueOfParameter));
				// }
			}
			method.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse res = client.execute(method);
			InputStream data = res.getEntity().getContent();
			value = convertStreamToString(data);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

	public static String uriEncoding(String uri) {
		String newValue = "";

		try {
			newValue = URLEncoder.encode(uri, "UTF-8");
			// System.out.println("urlstart[i]   "+urlstart[i] );
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return uri;
		}
		return newValue;
	}

	public static String performGet(String url) {
		System.out.println(url);
		/*
		 * org.apache.commons.httpclient.util.URIUtil
		 * URIUtil.encodeQuery(input);
		 */
		/*
		 * byte ptext[] = url.getBytes(); try { String value = new String(ptext,
		 * "UTF-8"); } catch (UnsupportedEncodingException e1) { // TODO
		 * Auto-generated catch block e1.printStackTrace(); }
		 */
		url = url.replaceAll("\\^", "");
		url = url.replace(" ", "%20");
		url = url.replace("\n", "%0A");
		url = url.replace("#", "%23");
		url = url.replace("(", "%28");
		url = url.replace(")", "%29");
		url = url.replace("<", "%3C");
		url = url.replace(">", "%3E");
		System.out.println("URL IS " + url);
		String strres = null;

		try {
			HttpParams basicparams = new BasicHttpParams();
			int timeoutConnection = 30000;
			HttpConnectionParams.setConnectionTimeout(basicparams,
					timeoutConnection);
			DefaultHttpClient client = new DefaultHttpClient(basicparams);
			// int timeoutSocket = 1000;
			// HttpConnectionParams.setSoTimeout(basicparams, timeoutSocket);
			// client.setParams(basicparams);
			URI uri = new URI(url);
			HttpGet method = new HttpGet(uri);
			HttpResponse res = client.execute(method);
			InputStream data = res.getEntity().getContent();
			strres = convertStreamToString(data);
		} catch (ClientProtocolException e) {
			strres = "Exception";
			System.err.println("WebAPIRequest.performGet()");
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			strres = "Exception";
			System.err.println("WebAPIRequest.performGet()");
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			strres = "Exception";
			System.err.println("WebAPIRequest.performGet()");
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			strres = "Exception";
			System.err.println("WebAPIRequest.performGet()");
		}
		System.out.println("RESPONCE......");
		System.out.println(strres);
		return strres;
	}

	public static String performPostwithjson(String URL, JSONObject jsonString) {
		System.out.println("Url is " + URL);
		System.out.println("JsonString is " + jsonString);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		String res = null;
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 300000); // Timeout
		// Limit
		HttpResponse response;

		try {
			HttpPost post = new HttpPost(URL);
			System.out.println(URL);
			System.out.println(jsonString + "json");
			String jsonstr = jsonString + "";
			StringEntity se = new StringEntity(jsonstr.toString());
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-type", "application/json");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			post.setEntity(se);
			response = client.execute(post);

			if (response != null) {
				InputStream in = response.getEntity().getContent(); // Get
																	// the
				// data
				// entity
				res = convertStreamToString(in);
				System.out.println("Res is :" + res);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			res = "Exception";
			System.out.println("exception on webapi post json");
		}
		return res;
	}

	public String performPostwithXML(String URL, String body) {
		System.out.println("Url is " + URL);
		System.out.println("JsonString is " + body);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		String res = null;
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 300000); // Timeout
																				// Limit
		HttpResponse response;

		try {
			HttpPost post = new HttpPost(URL);
			System.out.println(URL);
			System.out.println(body + "json");
			String jsonstr = body + "";
			StringEntity se = new StringEntity(jsonstr.toString());
			post.setHeader("Accept", "application/x-www-form-urlencoded");
			post.setHeader("Content-type", "application/x-www-form-urlencoded");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/x-www-form-urlencoded"));
			post.setEntity(se);
			response = client.execute(post);

			if (response != null) {
				InputStream in = response.getEntity().getContent(); // Get
				// the
				// data
				// entity
				res = convertStreamToString(in);
				System.out.println("Res is :" + res);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			res = "Exception";
			System.out.println("exception on webapi post json");
		}
		return res;
	}
}