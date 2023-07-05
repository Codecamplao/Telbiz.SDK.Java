package com.telbiz.sdk;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import com.telbiz.model.TokenEndPointRequest;
import com.telbiz.model.UrlDefault;
import com.telbiz.model.CommonResponse;
import com.telbiz.model.ResultResponse;
import com.telbiz.model.PKResponse;


public class Telbiz implements ITelbiz {
	private String clientid;
	private String secret;

	public Telbiz(String clientid, String secret) {
		this.clientid = clientid;
		this.secret = secret;
	}
	@Override
	public ResultResponse SendSMS(String header, String phone, String message) {
		CommonResponse resp = new CommonResponse();
		PKResponse pkresp = new PKResponse();
		ResultResponse result = new ResultResponse();
		try {
			String src = header;
			
			// Get Token with client ID and Secret
			TokenInterface IToken = new TokenInterface();
			var reqToken = new TokenEndPointRequest();
			reqToken.setClientID(clientid);
			reqToken.setGrantType("client_credentials");
			reqToken.setPassword("");
			reqToken.setRedirectUri("");
			reqToken.setScope("Telbiz_API_SCOPE profile openid");
			reqToken.setSecret(secret);
			reqToken.setUsername("");

			var access_token = IToken.GetAccessToken(reqToken);
			if (!access_token.Success) {
				resp.Code = "INVALID_CLIENT";
				resp.Success = false;
				resp.Message = "INVALID_CLIENT";
				resp.Detail = access_token.Detail;
				result.Response = resp;
				return result;
			}

			String jsonData = "";

			String bodyStr = "{\r\n    " + "\"title\": \"" + src + "\",\r\n    " + "\"phone\": \"" + phone
					+ "\",\r\n    " + "\"message\":\"" + message + "\"\r\n}";

			Connection.Response execute = Jsoup.connect(UrlDefault.UrlEndpoint + UrlDefault.SendSMS)
					.header("Content-Type", "application/json").header("Accept", "application/json")
					.header("Authorization", "bearer " + access_token.AccessToken).followRedirects(true)
					.ignoreHttpErrors(true).ignoreContentType(true).sslSocketFactory(socketFactory())
					.method(Connection.Method.POST).requestBody(bodyStr).maxBodySize(1_000_000 * 30) // 30 mb ~
					.timeout(0) // infinite timeout
					.execute();
			jsonData = execute.body();

			if (jsonData != "") {
				JSONObject Jobject = new JSONObject(jsonData);
				if (Jobject != null) {
					if(execute.statusCode() == 200) {
						var resultRes = Jobject.getJSONObject("response");
						if (resultRes != null) {
							resp.setCode(resultRes.getString("code"));
							resp.setSuccess(resultRes.getBoolean("success"));
							resp.setMessage(resultRes.getString("message"));
							resp.setDetail(resultRes.getString("detail"));
						}
						var resultKey = Jobject.getJSONObject("pkresponse");
						if(resultKey != null) {
							pkresp.setPartitionKey(resultKey.getString("partitionkey"));
							pkresp.setRangeKey(resultKey.getString("rangekey"));
						}
					}else if(execute.statusCode() == 400) {
						var resultRes = Jobject.getJSONObject("response");
						if (resultRes != null) {
							resp.setCode(resultRes.getString("code"));
							resp.setSuccess(resultRes.getBoolean("success"));
							resp.setMessage(resultRes.getString("message"));
							resp.setDetail(resultRes.getString("detail"));
						}
					}
					else if(execute.statusCode() == 500) {
						resp.setCode("500");
						resp.setSuccess(false);
						resp.setMessage(Jobject.getString("type"));
						resp.setDetail(Jobject.getString("type") + " " + Jobject.getString("title")+ " " + Jobject.getString("status")+ " " + Jobject.getString("traceId"));
					}else {
						resp.setCode(String.valueOf(execute.statusCode()));
						resp.setSuccess(false);
						resp.setMessage("");
						resp.setDetail("");
					}
				}
			}
			result.Response = resp;
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public ResultResponse Topup(String phone, int amount) {
		CommonResponse resp = new CommonResponse();
		PKResponse pkresp = new PKResponse();
		ResultResponse result = new ResultResponse();
		try {
			// Get Token with client ID and Secret
			TokenInterface IToken = new TokenInterface();
			var reqToken = new TokenEndPointRequest();
			reqToken.setClientID(clientid);
			reqToken.setGrantType("client_credentials");
			reqToken.setPassword("");
			reqToken.setRedirectUri("");
			reqToken.setScope("Telbiz_API_SCOPE profile openid");
			reqToken.setSecret(secret);
			reqToken.setUsername("");

			var access_token = IToken.GetAccessToken(reqToken);
			if (!access_token.Success) {
				resp.Code = "INVALID_CLIENT";
				resp.Success = false;
				resp.Message = "INVALID_CLIENT";
				resp.Detail = access_token.Detail;
				result.Response = resp;
				return result;
			}

			String jsonData = "";
			String bodyStr = "{\r\n    " + "\"phone\": \"" + phone + "\",\r\n    " + "\"amount\":\"" + amount
					+ "\"\r\n}";

			Connection.Response execute = Jsoup.connect(UrlDefault.UrlEndpoint + UrlDefault.Topup)
					.header("Content-Type", "application/json").header("Accept", "application/json")
					.header("Authorization", "bearer " + access_token.AccessToken).followRedirects(true)
					.ignoreHttpErrors(true).ignoreContentType(true).sslSocketFactory(socketFactory())
					.method(Connection.Method.POST).requestBody(bodyStr).maxBodySize(1_000_000 * 30) // 30 mb ~
					.timeout(0) // infinite timeout
					.execute();
			jsonData = execute.body();

			if (jsonData != "") {
				JSONObject Jobject = new JSONObject(jsonData);
				if(execute.statusCode() == 200) {
					var resultRes = Jobject.getJSONObject("response");
					if (resultRes != null) {
						resp.setCode(resultRes.getString("code"));
						resp.setSuccess(resultRes.getBoolean("success"));
						resp.setMessage(resultRes.getString("message"));
						resp.setDetail(resultRes.getString("detail"));
					}
					var resultKey = Jobject.getJSONObject("pkresponse");
					if(resultKey != null) {
						pkresp.setPartitionKey(resultKey.getString("partitionkey"));
						pkresp.setRangeKey(resultKey.getString("rangekey"));
					}
				}else if(execute.statusCode() == 400) {
					var resultRes = Jobject.getJSONObject("response");
					if (resultRes != null) {
						resp.setCode(resultRes.getString("code"));
						resp.setSuccess(resultRes.getBoolean("success"));
						resp.setMessage(resultRes.getString("message"));
						resp.setDetail(resultRes.getString("detail"));
					}
				}
				else if(execute.statusCode() == 500) {
					resp.setCode("500");
					resp.setSuccess(false);
					resp.setMessage(Jobject.getString("type"));
					resp.setDetail(Jobject.getString("type") + " " + Jobject.getString("title")+ " " + Jobject.getString("status")+ " " + Jobject.getString("traceId"));
				}else {
					resp.setCode(String.valueOf(execute.statusCode()));
					resp.setSuccess(false);
					resp.setMessage("");
					resp.setDetail("");
				}
			}

			return result;
		} catch (Exception e) {

		}
		return result;
	}

	// Ignore SSL when call API
	private static SSLSocketFactory socketFactory() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws CertificateException {
				// TODO Auto-generated method stub

			}
		} };

		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sslContext.getSocketFactory();
	}

}
