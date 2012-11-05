package basicOAuth;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import org.apache.http.NameValuePair;

public class OAuthSession {
	private String consumer_key;
	private String consumer_secret;
	private String request_end_point_uri;
	private String requestToken;
	private String authorizationEndPointUri;
	private String accessEndPointUri;
	
	private String signature_base_string;
	private List <NameValuePair> oauth_parameters;
	
	 private static Comparator<NameValuePair> COMPARATOR = new Comparator<NameValuePair>()
			     {
			         public int compare(NameValuePair o1, NameValuePair o2)
			         {
			             return o1.getName().compareTo(o2.getName()); 
			         }
			     };

	public OAuthSession(String key, String secret, String requestUri, String authorizationUri, String accessUri){
		consumer_key = key;
		consumer_secret = secret;
		request_end_point_uri = requestUri;
		authorizationEndPointUri = authorizationUri;
		accessEndPointUri = accessUri;
	}
	
	private void enableSNIConnections (HttpClient client){
		System.out.println("- Enabling SNI Connections");
		SSLSocketFactory socket_factory;
		SSLContext ssl_context;
		try {
			ssl_context = SSLContext.getInstance("TLS");
			ssl_context.init(null, null, null); 
			socket_factory = new SSLSocketFactory(
					ssl_context,
				    SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			Scheme sch = new Scheme("https", 443, socket_factory);
			client.getConnectionManager().getSchemeRegistry().register(sch);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void buildOAuthParam()
	{
		System.out.println("- Building OAuth parameters");
		oauth_parameters = new ArrayList <NameValuePair>();
		oauth_parameters.add(new BasicNameValuePair("oauth_consumer_key", consumer_key));
		oauth_parameters.add(new BasicNameValuePair("oauth_signature_method", "HMAC-SHA1"));
		String current_timestamp = String.valueOf(System.currentTimeMillis()/1000);
		oauth_parameters.add(new BasicNameValuePair("oauth_timestamp",current_timestamp));
		Random rndom = new Random();
		String nonce = Long.toString(rndom.nextLong(), 36);
		oauth_parameters.add(new BasicNameValuePair("oauth_nonce",nonce));
		
		Collections.sort(oauth_parameters, COMPARATOR);
	}
	
	private void buildOAuthSignature(HttpPost http_post)
	{
		System.out.println("- Building OAuth signature");
		ListIterator<NameValuePair> signatureBaseStringIterator = oauth_parameters.listIterator();
		String signature_base_string = "";
		String param_string = "";
		System.out.println("- Build plaintext OAuth params");
		while(signatureBaseStringIterator.hasNext()) {
			NameValuePair param = signatureBaseStringIterator.next();
			param_string += param.getName() + "=" + param.getValue();
			System.out.println("-- " + param.getName() + "=" + param.getValue());
			if (signatureBaseStringIterator.hasNext()){
				param_string += "&";
			}
		}	
		try {
			System.out.println("-- Concatenate element into full base string");
			signature_base_string = http_post.getMethod() + "&" + 
					URLEncoder.encode(http_post.getURI().toString(), "UTF-8") + "&" +
					URLEncoder.encode(param_string, "UTF-8");
			System.out.println("--- base string = " + signature_base_string);
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} 
		try {
			System.out.println("-- Encode base string");
		    Mac mac = Mac.getInstance("HmacSHA1");
		    String digest_key = URLEncoder.encode(consumer_secret + "&", "UTF-8");
		    SecretKeySpec signatureSecret = new SecretKeySpec(digest_key.getBytes(),"HmacSHA1");
		    mac.init(signatureSecret);
		    
		    byte[] digest = mac.doFinal(signature_base_string.getBytes());
		    System.out.println("--- hex signature: " +  new String(new Hex().encode(digest)));
		    byte[] base64Bytes = new Base64().encode(digest);
		    System.out.println("--- base64 signature: " + new String(new Base64().encode(digest)));
		    String oauth_encoded_signature = new String(base64Bytes);
		    oauth_parameters.add(new BasicNameValuePair("oauth_signature", oauth_encoded_signature));
		} catch (Exception e) {
		    System.out.println(e.getMessage());
		}
	}
	
	private void executeRequest(HttpClient http_client, HttpPost http_post){
		System.out.println("Execute request");
		HttpResponse response;
		try {
			response = http_client.execute(http_post);
			HttpEntity entity = response.getEntity();
			InputStream instream = entity.getContent();
			Reader r = new InputStreamReader(instream, "US-ASCII");
			int intch;
			while ((intch = r.read()) != -1) {
				char ch = (char) intch;
				System.out.print(ch);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			http_client.getConnectionManager().shutdown();
		}
	}
	
	private void buildRequest(String uri, String key, String secret){
		System.out.println("Building request");
		HttpClient http_client = new DefaultHttpClient();
		buildOAuthParam();
		enableSNIConnections(http_client);
		HttpPost http_post = new HttpPost(uri);
		buildOAuthSignature(http_post);

		http_post.setEntity(new UrlEncodedFormEntity(oauth_parameters, Consts.UTF_8));
		executeRequest(http_client, http_post);
	}
	
	public void getRequestToken(){
		buildRequest(request_end_point_uri,consumer_key,consumer_secret);
	}
}
