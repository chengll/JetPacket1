package us.mifeng.jetpacket1.utils;

import android.content.res.AssetManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import us.mifeng.jetpacket1.model.Destination;

public class AppConfig {
    private static HashMap<String, Destination> sDestinationMap;


    public static HashMap<String, Destination> getsDestinationMap() {
        if (null == sDestinationMap) {
            String content=parseAssetsJson("nav.json");
            sDestinationMap= JSON.parseObject(content, new TypeReference<HashMap<String, Destination>>() {
            }.getType());
        }
        return sDestinationMap;
    }

    //定义方法获取assets下的json文件
    private  static  String parseAssetsJson(String fileName) {
        InputStream openƒ = null;
        BufferedReader buf = null;
        AssetManager assets = AppGlobals.getmApplilcation().getResources().getAssets();
        StringBuffer stringBuffer = new StringBuffer();
        try {
            openƒ = assets.open(fileName);
            buf = new BufferedReader(new InputStreamReader(openƒ));
            String line = null;
            while ((line = buf.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != buf) {
                try {
                    buf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != openƒ) {
                try {
                    openƒ.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuffer.toString();
    }

}
