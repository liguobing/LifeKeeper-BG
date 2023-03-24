package com.lixyz.lifekeeper.controller;

import com.google.gson.Gson;
import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.weather.WeatherBean;
import io.swagger.annotations.Api;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@Api(tags = "天气接口")
public class WeatherController {

    @GetMapping("GetWeatherByLongitudeAndLatitude")
    public Result getWeatherByLongitudeAndLatitude(String longitude, String latitude) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().retryOnConnectionFailure(false)
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .build();
            String url = "https://devapi.qweather.com/v7/weather/now?key=4e1895b1140845c59c16d1157dabc42f&location=" + longitude + "," + latitude;
            System.out.println(url);
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            Gson gson = new Gson();
            WeatherBean weatherBean = gson.fromJson(response.body().string(), WeatherBean.class);
            return new Result(true, null, null, weatherBean.getNow().getIcon());
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, "出错啦", e, null);
        }
    }
}
