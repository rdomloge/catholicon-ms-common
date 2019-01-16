package com.domloge.catholicon.ms.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
public class Loader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Loader.class);
	
	@Value("${BASE_URL:http://bdbl.org.uk}")
	private String BASE;
	
	private HttpContext ctx;
	
	private CloseableHttpClient client = HttpClients.createDefault();

	@PostConstruct
	public void setup() {
		CookieStore cookieStore = new BasicCookieStore();
		BasicClientCookie cookie1 = new BasicClientCookie("BDBLUID", "24");
		cookie1.setDomain("bdbl.org.uk");
		cookie1.setPath("/");
		BasicClientCookie cookie2 = new BasicClientCookie("testcookie", "true");
		cookie2.setDomain("bdbl.org.uk");
		cookie2.setPath("/");
		cookieStore.addCookie(cookie1);
		cookieStore.addCookie(cookie2);
		
		ctx = HttpClientContext.create();
		ctx.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
	}
	
	private String streamToString(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		InputStreamReader isr = new InputStreamReader(is);
		char[] cbuf = new char[128];
		int read = -1;
		try {
			while( (read = isr.read(cbuf)) != -1) {
				sb.append(cbuf, 0, read);
			}
		} 
		finally {
			isr.close();
			is.close();
		}
		return sb.toString();
	}
	
	public String load(String url) throws ScraperException {
		StopWatch stopWatch = new StopWatch("Loader");
		stopWatch.start();
		
		try {
			String fullUrl = BASE+url;
			LOGGER.info(fullUrl);
			HttpGet get = new HttpGet(fullUrl);
			
			ResponseHandler<String> handler = new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse resp) throws ClientProtocolException, IOException {
					Header cacheHeader = resp.getFirstHeader("X-Cached");
					if(null != cacheHeader) {
						LOGGER.debug("Response was "+cacheHeader.getValue());
					}
					return streamToString(resp.getEntity().getContent());
				}
			};
	
			try {
				return client.execute(get, handler, ctx);
			} 
			catch (IOException e) {
				throw new ScraperException(e);
			} 
			finally {
				get.releaseConnection();
			}
		}
		finally {
			stopWatch.stop();
			LOGGER.debug("Load took "+stopWatch.getTotalTimeMillis()+"ms: "+url);
		}
	}

}
