import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;

import jp.eiya.aya.test.SimpleTestServer;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class SampleHttpTest {
	
	private static final SimpleTestServer server = new SimpleTestServer();
	
	@BeforeClass
	public static void setHandlar() throws Exception {
		server.setHandler("http://localhost:8080/test1","Hello Test Server!");
		server.setHandler("http://localhost:8080/test2","this is test2");
	}
	
	@AfterClass
	// Don't forget to do this.
	public static void stopServer() throws Exception{
		server.stop();
	}

	@Test
	public void test1() throws Exception {
		String respose = getResponse("http://localhost:8080/test1");
		assertEquals("Hello Test Server!",respose);
	}
	@Test
	public void test2() throws Exception {
		String respose = getResponse("http://localhost:8080/test2");
		assertEquals("this is test2",respose);
	}
	
	@Test
	public void test3() throws Exception {
		server.setHandler("http://localhost:8080/test3","this is test3");
		String respose = getResponse("http://localhost:8080/test3");
		assertEquals("this is test3",respose);
	}

	private String getResponse(String path) throws Exception {
		URI uri = URI.create(path);
		HttpURLConnection con = (HttpURLConnection)uri.toURL().openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String respose = reader.readLine();
		reader.close();
		return respose;
	}

}
