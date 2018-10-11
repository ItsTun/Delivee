package com.example.tunhanmyae.delivee.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.tunhanmyae.delivee.Model.User;
import com.example.tunhanmyae.delivee.Remote.APIServe;
import com.example.tunhanmyae.delivee.Remote.RetrofitClient;

import retrofit2.Retrofit;

public class Common {
    public static User currentUser;
    public static final String DELETE="Delete";
    public static final String USER_Key="User";
    public static final String PWD_KEY="Password";
    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public  static APIServe getFCMService()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIServe.class);

    }


    public static String convertCodeToStatus(String code)
    {
        if (code.equals("0"))
        {
            return "Placed";
        }
        else if(code.equals("1"))
        {
            return "On my way!";
        }
        else
            return  "Shipped";
    }
    public static boolean isConnectedToInterner(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null)
        {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null)
            {
                for(int i=0;i<info.length;i++)
                {
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;

    }
}
