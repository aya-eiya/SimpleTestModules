package jp.eiya.aya.test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;

public class SimpleTestWebServer {
	
	private final class SimpleHttpHandler implements HttpHandler {
		final String response;
		public SimpleHttpHandler(String _response){
			response = _response;
		}
		@Override
		public void handle(HttpExchange exc) throws IOException {;
			exc.sendResponseHeaders(200, response.length());
			OutputStream os = exc.getResponseBody();
			os.write(response.getBytes());
	        os.close();
		}
	}
	private final class HttpServerSet {
		private HttpServer httpServer;
		private ExecutorService executorService;
		public HttpServerSet(HttpServer _httpServer,ExecutorService _executorService){
			httpServer = _httpServer;
			executorService = _executorService;
			httpServer.setExecutor(executorService);
			httpServer.start();
		}
		
		public void stop(){
			httpServer.stop(1);
			executorService.shutdownNow();
		}

		public void createContext(String path,
				SimpleHttpHandler simpleHttpHandler) {
			httpServer.createContext(path,simpleHttpHandler);
		}
	}
	private final HashMap<String,HttpServerSet> httpServerMap = new HashMap<String, HttpServerSet>();
	private final HashMap<String,HttpServerSet> httpsServerMap= new HashMap<String, HttpServerSet>();
	
	private final HttpServerSet start(URL url,HashMap<String,HttpServerSet> map) throws Exception {
		String baseURI = url.toString().substring(0,url.toString().length()-url.getPath().length());
		HttpServerSet httpServerSet = map.get(baseURI);
		if(httpServerSet == null) {
			httpServerSet = new HttpServerSet(
					HttpServer.create(new InetSocketAddress(url.getHost(),url.getPort()),url.getPort()),
					Executors.newFixedThreadPool(1)
			);
			map.put(baseURI, httpServerSet);
		}
		return httpServerSet;
	}
	
	private final HttpServerSet start(URL url) throws Exception{
		if(url.getProtocol().equals("http")){
			return start(url,httpServerMap);
		}else{
			return start(url,httpsServerMap);
		}
	}
	

	public void stop() throws Exception{
		for(String uri:httpServerMap.keySet()){
			httpServerMap.get(uri).stop();
		}
		for(String uri:httpsServerMap.keySet()){
			httpsServerMap.get(uri).stop();
		}
	}
	
	public final void setHandler(String path,String response) throws Exception {
		if( path == null || path.isEmpty() ){
			throw new InvalidParameterException("Path must not be blank or null.");
		}
		URL url = URI.create(path).toURL();
		start(url).createContext(
				url.getPath(),
				new SimpleHttpHandler(response)
		);
	}

}
