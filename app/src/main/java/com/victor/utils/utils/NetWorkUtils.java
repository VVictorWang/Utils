package com.victor.utils.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetWorkUtils {


    /*
       * 获取给定url值的string型数据
       * @param myurl
       * */
    public static String sendRequst(String myurl) {
        HttpURLConnection connection = null;
        StringBuilder builder = new StringBuilder();
        try {
            URL url = new URL(myurl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(8000);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (Exception p) {
            p.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

        }
        return builder.toString();
    }

    //判断系统网络是否可用
    public static boolean isNetWorkAvailabe(Context context) {
        if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) {
                Log.d("网络:", "不能得到系统网络服务");
            } else {
                NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
                if (infos != null) {
                    for (int i = 0; i < infos.length; i++) {
                        if (infos[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }
        Log.d("网络:", "网络不可用");
        return false;
    }

}