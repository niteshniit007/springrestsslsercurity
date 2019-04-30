package com.example.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringrestsslsercurityApplicationTests {

	// REST api endpoint
	private static final String REST_API_ENDPOINT = "https://localhost:8443/logged_info";

	static {
		// for localhost testing only
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {

			public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
				return hostname.equals("localhost");
			}
		});
	}

	// @Test
	public void testService() {
		System.setProperty("javax.net.ssl.keyStore", "g://ssl//clientkeystore.p12");
		System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
		System.setProperty("javax.net.ssl.trustStore", "g://ssl//clienttruststore.p12");
		System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
		RestTemplate restTemplate = new RestTemplate();
		System.out.println(">>>>>>>>>>>>>>>" + restTemplate.getForObject(REST_API_ENDPOINT, String.class));

	}

	@Test
	public void testService2() {
		try {
			TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

			KeyStore clientStore = KeyStore.getInstance("JKS");
			clientStore.load(new FileInputStream(new File("g://ssl//clientkeystore.p12")), "changeit".toCharArray());

			KeyStore trustKeyStore = KeyStore.getInstance("JKS");
			FileInputStream trustKeyStoreFile = new FileInputStream(new File("g://ssl//clienttruststore.p12"));
			trustKeyStore.load(trustKeyStoreFile, "changeit".toCharArray());
			
			//System.out.println(trustKeyStore.getCertificate("servercert"));
			SSLContext sslContext = SSLContexts.custom().
					loadKeyMaterial(clientStore, "changeit".toCharArray()).
					loadTrustMaterial(trustKeyStore, acceptingTrustStrategy).build();

			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			});
			CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();

			HttpGet httpget = new HttpGet("https://localhost:8443/logged_info");
			
			/*
			 * String auth = "nitesh1" + ":" + "f19c048c-bbe4-42bd-a2c5-6f0f824bea3a";
			 * byte[] encodedAuth =
			 * Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
			 * String authHeader = "Basic " + new String(encodedAuth);
			 * httpget.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
			 */
			 
			try {
				HttpResponse response = httpClient.execute(httpget);
				String apiOutput = EntityUtils.toString(response.getEntity());
				System.out.println(">>>>>>>>>>>>11111111111 " + apiOutput);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
