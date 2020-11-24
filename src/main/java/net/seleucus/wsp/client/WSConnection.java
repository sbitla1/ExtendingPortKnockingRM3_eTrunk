package net.seleucus.wsp.client;

import org.apache.commons.codec.digest.DigestUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Locale;

public class WSConnection {
	
	protected static String[] ACTION_CAN_BE_TAKEN = {
		"...",
		"The URL is neither HTTP nor HTTPS: No action will be taken",
		"Malformed URL: No action will be taken", 
		"I/O Exception: No action will be taken", 
		"Starting to send the above HTTP request",
		"Starting to send the above HTTPS request"
	};

	protected static String[] RESPONSE_MESSAGES = {
		"...",
		"I/O Exception: While attempting to connect",
		"Response received"
	};

	private int action, response;
			
	private URLConnection connection = null;
	
	public WSConnection(final String knock) {
		
		action = 0;
		response = 0;
		
		try {
			
			URL knockURL = new URL(knock);

			if (knockURL.getProtocol().toLowerCase().equals("https")) {
				action = 5;
			    trustAllHosts();
				HttpsURLConnection https = (HttpsURLConnection) knockURL.openConnection();
				https.setHostnameVerifier(new HostnameVerifier() {
					/**
					 * Always verify the host - dont check for certificate
					 */
					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});
				connection = https;
			} else {
				action = 4;
				connection = knockURL.openConnection();
			}
			
			setRequestProperties();
			
		} catch (MalformedURLException e) {
			
			action = 2;
			
		} catch (IOException e) {
			
			action = 3;
			
		} 
	}

	/**
	 * Trust every server - dont check for any certificate
	 */
	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[] {};
			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain,
					String authType) {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain,
					String authType) {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setRequestProperties() {

		connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		connection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
		connection.setRequestProperty("Accept-Language", "en-US,en;q=0.8,el;q=0.6");
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
		
	}

	public void sendRequest() {
		
		if(action == 4 || action == 5) {
			try {

				connection.connect();
				response = 2;

			} catch (IOException e) {
				// "I/O Exception: While attempting to connect"
				response = 1;

			}
		}
	}

	public String responseCode() {

		if(action == 4) {
			try {

				return "" + ((HttpURLConnection) connection).getResponseCode();

			} catch (IOException e) {
				
				return "---";

			}
			
		} else if(action == 5) {
			
			try {

				return "" + ((HttpsURLConnection) connection).getResponseCode();

			} catch (IOException e) {
				
				// e.printStackTrace();
				return "***";

			}			
			
		} else {
			
			return "000";
			
		}

	}

	public String getActionToBeTaken() {
		return ACTION_CAN_BE_TAKEN[action];
	}

	public String responseMessage() {
		return RESPONSE_MESSAGES[response];
	}

	public boolean isHttps() {
		return (connection instanceof HttpsURLConnection);
	}

	public String getCertificateAlgorithm() throws SSLPeerUnverifiedException {
		Certificate[] certs = ((HttpsURLConnection) connection).getServerCertificates();
		return certs[0].getPublicKey().getAlgorithm();
	}

	public String getCertificateFingerprint()
			throws SSLPeerUnverifiedException, CertificateEncodingException {
		Certificate[] certs = ((HttpsURLConnection) connection).getServerCertificates();
		return formatWithColons(DigestUtils.sha1Hex(certs[0].getEncoded()).toUpperCase(Locale.ENGLISH));
	}

	public String getCertSHA1Hash() {
		StringBuffer hashCode = new StringBuffer("SSL Check - ");

		if (action == 5) {
			try {
				hashCode.append(getCertificateAlgorithm());
				hashCode.append(" key fingerprint is (SHA1) ");
				hashCode.append(getCertificateFingerprint());
				hashCode.append(".");
			} catch (SSLPeerUnverifiedException e) {
				hashCode.append("No Certificate Hash Values");
			} catch (CertificateEncodingException e) {
				hashCode.append("Certificate Encoding Exception");
			} catch (IllegalStateException e) {
				hashCode.append("Illegal State Exception");
			}
		}

		return hashCode.toString();
	}

	private static String formatWithColons(String input) {
		
		StringBuilder sb = new StringBuilder();
		char[] inputCharArray = input.toCharArray();
		
		for(int count = 0; count < inputCharArray.length; count++) {
			
			if(count % 2 == 0 && count != 0) {
				sb.append(':');
			}
			sb.append(inputCharArray[count]);
		}
		
		return sb.toString();
		
	}
	
}
