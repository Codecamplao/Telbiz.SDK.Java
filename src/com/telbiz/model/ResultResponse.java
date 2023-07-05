package com.telbiz.model;

public class ResultResponse {
	public CommonResponse getResponse() {
		return Response;
	}
	public void setResponse(CommonResponse response) {
		Response = response;
	}
	public PKResponse getKey() {
		return Key;
	}
	public void setKey(PKResponse key) {
		Key = key;
	}
	public CommonResponse Response ;
	public PKResponse Key;
}
