package basicOAuth;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.net.URLEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import org.apache.http.NameValuePair;

public class OAuthSession {
	private String consumerKey;
	private String consumerSecret;
	private String requestEndPointUri;
	private String requestToken;
	private String authorizationEndPointUri;
	private String accessEndPointUri;
	
	public OAuthSession(String key, String secret, String requestUri, String authorizationUri, String accessUri){
		consumerKey = key;
		consumerSecret = secret;
		requestEndPointUri = requestUri;
		authorizationEndPointUri = authorizationUri;
		accessEndPointUri = accessUri;
	}
	
	private void buildRequest(String uri, String key, String secret){
		HttpClient httpClient = new DefaultHttpClient();		
		HttpPost httpPost = new HttpPost(uri);

		List <NameValuePair> oAuthParameters = new ArrayList <NameValuePair>();
		oAuthParameters.add(new BasicNameValuePair("oauth_consumer_key", consumerKey));
		oAuthParameters.add(new BasicNameValuePair("oauth_signature_method", "HMAC-SHA1"));
        
        //Create the signature, need the http method, and URL
		//Build base string
		System.out.println("Build the signature base string");
		
		ListIterator<NameValuePair> signatureBaseStringIterator = oAuthParameters.listIterator();
		String signatureBaseString = httpPost.getMethod() + "&" + httpPost.getURI();
		while(signatureBaseStringIterator.hasNext()) {
			NameValuePair param = signatureBaseStringIterator.next();
			signatureBaseString += param.getName() + "=" + param.getValue();
			if (signatureBaseStringIterator.hasNext()){
				signatureBaseString += "&";
			}
		}	
		System.out.println(signatureBaseString);
		try {
			System.out.println(URLEncoder.encode(signatureBaseString, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//Now build the signature
		System.out.println("Build the signature");
		try {
		    Mac mac = Mac.getInstance("HmacSHA1");
		    SecretKeySpec signatureSecret = new SecretKeySpec(consumerSecret.getBytes(),"HmacSHA1");
		    mac.init(signatureSecret);
		    byte[] digest = mac.doFinal(signatureBaseString.getBytes());
		    String oAuthSignature = new String(digest);
		    System.out.println(oAuthSignature);  
		    oAuthParameters.add(new BasicNameValuePair("oauth_signature", oAuthSignature));
		} catch (Exception e) {
		    System.out.println(e.getMessage());
		}

        httpPost.setEntity(new UrlEncodedFormEntity(oAuthParameters, Consts.UTF_8));
		
		HttpResponse response;
		try {
			response = httpClient.execute(httpPost);
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
			httpClient.getConnectionManager().shutdown();
		}
	}
	
	public void getRequestToken(){
		buildRequest(requestEndPointUri,consumerKey,consumerSecret);
		
	}
	

}
