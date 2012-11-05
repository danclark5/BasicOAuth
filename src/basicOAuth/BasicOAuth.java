package basicOAuth;

import java.io.IOException;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.Reader;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import basicOAuth.OAuthSession;


public class BasicOAuth {

	private static Properties properties;
	/**
	 * @param args
	 */
	public void test() {
		HttpClient http_client = new DefaultHttpClient();
		
		HttpGet http_get = new HttpGet("http://api.imgur.com/oauth/request_token");
		try {
			HttpResponse response = http_client.execute(http_get);
			HttpEntity entity = response.getEntity();
			System.out.println(response.getStatusLine());
			InputStream instream = entity.getContent();
			Reader r = new InputStreamReader(instream, "US-ASCII");
			int intch;
			while ((intch = r.read()) != -1) {
				char ch = (char) intch;
				System.out.print(ch);
			}
			//System.out.println(EntityUtils.toString(entity));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void readConfiguration(){
		properties = new Properties();
	    String fileName = "oAuth_app.config";
	    InputStream is;
		try {
			is = new FileInputStream(fileName);
			properties.load(is);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//test();
		//System.setProperty("jsse.enableSNIExtension","true");
		//System.out.println(System.getProperty("jsse.enableSNIExtension"));
		readConfiguration();
		OAuthSession test_session = new OAuthSession(properties.getProperty("consumer_key"),
				properties.getProperty("consumer_key"), "https://api.imgur.com/oauth/request_token",
				"https://api.imgur.com/oauth/authorize",
				"https://api.imgur.com/oauth/access_token");
		test_session.getRequestToken();

	}

}
