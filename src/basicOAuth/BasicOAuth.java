package basicOAuth;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


public class BasicOAuth {

	/**
	 * @param args
	 */
	public static void test() {
		HttpClient http_client = new DefaultHttpClient();
		
		// --------Trick the library

		SSLSocketFactory sf;
		try {
			SSLContext sslcontext = SSLContext.getInstance("TLS"); 
			sslcontext.init(null, null, null); 
			sf = new SSLSocketFactory(
					sslcontext,
				    SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			Scheme sch = new Scheme("https", 443, sf);
			http_client.getConnectionManager().getSchemeRegistry().register(sch);
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (KeyManagementException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			// -------- END Trick the library
			
		HttpGet http_get = new HttpGet("https://api.imgur.com/oauth/request_token");
		System.out.println("test");
		try {
			HttpResponse response = http_client.execute(http_get);
			System.out.println("test");
			HttpEntity entity = response.getEntity();
			System.out.println(response.getStatusLine());
			InputStream instream = entity.getContent();
			Reader r = new InputStreamReader(instream, "US-ASCII");
			System.out.print(entity);
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		test();


	}

}
