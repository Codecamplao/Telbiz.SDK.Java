package com.telbiz.sdk;
import com.telbiz.model.ResultResponse;


public interface ITelbiz {
	ResultResponse SendSMS(String header, String phone, String message);
	ResultResponse Topup(String phone, int amount);
}
