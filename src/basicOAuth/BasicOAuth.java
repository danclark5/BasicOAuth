package basicOAuth;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


public class BasicOAuth {

	/**
	 * @param args
	 */
	public static void test() {
		HttpClient http_client = new DefaultHttpClient();
		HttpGet http_get = new HttpGet("http://google.com");
		try {
			HttpResponse response = http_client.execute(http_get);
			HttpEntity entity = response.getEntity();
			System.out.println(entity);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		test();


	}

}
