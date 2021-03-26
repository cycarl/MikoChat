package com.xana.mikochat.factory.net;

import android.text.TextUtils;

import com.xana.mikochat.common.Common;
import com.xana.mikochat.factory.Factory;
import com.xana.mikochat.factory.persistence.Account;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求封装
 */
public class Network {

    private static Network instance;
    private Retrofit retrofit;

    static {
        instance = new Network();
    }
    private Network(){}

    public static Retrofit getRetrofit(){
        if(instance.retrofit!=null)
            return instance.retrofit;

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request req = chain.request();
                        Request.Builder builder = req.newBuilder();
                        if(!TextUtils.isEmpty(Account.getToken())){
                            /* 注入一个token */
                            builder.addHeader("token", Account.getToken());
                        }
                        Request request = builder.addHeader("Content-type", "application/json").build();

                        return chain.proceed(request);
                    }
                })
                .build();
        Retrofit.Builder builder = new Retrofit.Builder();

        instance.retrofit =  builder.baseUrl(Common.Constance.API_URL)
                .client(client)
                // 设置 json解析器
                .addConverterFactory(GsonConverterFactory.create(Factory.getGson()))
                .build();
        return instance.retrofit;
    }

    /**
     * 返回一个网络请求代理
     * @return
     */
    public static RemoteService remote(){
        return Network.getRetrofit().create(RemoteService.class);
    }

}
