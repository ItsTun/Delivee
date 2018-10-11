package com.example.tunhanmyae.delivee.Remote;

import android.telecom.Call;

import com.example.tunhanmyae.delivee.Model.MyResponse;
import com.example.tunhanmyae.delivee.Model.Sender;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface APIServe {
    @Headers(
            {
                    "content-type:application/json",
                    "Authorization:key=AAAA0iw_r7o:APA91bG38ijW_7inEj7YUpsaUl3abpGkqtqpvP3sWvYNHnXCtXyV-5jPODDF1AYFl7uULu5ZDMFFMNV3cGGF0HAPpGZvx7yGIMt8HQLeJFAiRH4_pb8_ajBThD4qeZu860eKsQ6ZTXzRBesKfhk5NS8mz8GN6wr0-A"


            }
    )
    @POST("fcm/send")
    retrofit2.Call<MyResponse> sendNotification(@Body Sender body);

}
