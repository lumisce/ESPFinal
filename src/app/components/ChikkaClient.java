package app.components;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

@Component
public class ChikkaClient {
	
	private ChikkaInterface service;
	private boolean status = false;
	
	public boolean sendMessage(String phone, String message) {
		
		String id = String.valueOf(UUID.randomUUID()).replaceAll("-","");
		String clientId = "3fa6977cacb5be50089f40a8049b77dc732b41aa076c04e29782f7ce55cadf4c";
		String key =      "d8ec96a83b9c9ee26133d5fb4f387bd1afad30e183045557ff81e685d2a2382d";
		Call<Reply> call = service.send("SEND", phone, "292901782", id, message, clientId, key);
		status = false;
		call.enqueue(new Callback<Reply>() {
			@Override
			public void onResponse(Call<Reply> arg0, Response<Reply> arg1) {
				status = true;
			}
			@Override
			public void onFailure(Call<Reply> arg0, Throwable arg1) {
			}

		});
		return status;
	
	}
	
	@PostConstruct
	private void initialize() {
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

//		java.net.Proxy proxy = new Proxy(Proxy.Type.HTTP,  new InetSocketAddress("proxy.admu.edu.ph", 3128));

		OkHttpClient client = new OkHttpClient.Builder()
//						.proxy(proxy)
						.addInterceptor(interceptor)
						.build();
		
		GsonBuilder builder = new GsonBuilder(); 
		Gson gson = builder.create();
		
		Retrofit retrofit = new Retrofit.Builder()
				.client(client)
				.baseUrl("http://localhost/")
				.addConverterFactory(GsonConverterFactory.create(gson))
				.build();
		
		service = retrofit.create(ChikkaInterface.class);
	}
	private interface ChikkaInterface {
		@FormUrlEncoded
		@POST("https://post.chikka.com/smsapi/request")
		Call<Reply> send(
				@Field("message_type") String msgType, 
				@Field("mobile_number") String mobileNum, 
				@Field("shortcode") String shortcode, 
				@Field("message_id") String msgId, 
				@Field("message") String msg, 
				@Field("client_id") String clientId, 
				@Field("secret_key") String secretKey);
	}
	
	public static class Reply {
		public String status;
		public String message;
		
		public Reply() {
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
	}
}
