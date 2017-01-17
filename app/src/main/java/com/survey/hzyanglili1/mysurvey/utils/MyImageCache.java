package com.survey.hzyanglili1.mysurvey.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;
import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * Created by hzyanglili1 on 2017/1/16.
 */

public class MyImageCache implements ImageLoader.ImageCache {

    /**
     * 内存缓存：把图片暂存在内存中（ 不是永久）.目的：为了提升程序的流畅度，同时节省用户的流量
     *
     * 本地缓存：把图片持久化（永久的保存在外部存储介质）。目的：在程序没有网络时，依然有数据显示
     *
     * 使用Volley缓存框架实现图片的3级缓存
     *
     * （1）.LruCache （内存缓存） L1
     *
     * android系统对每一个应用程序有一个固定大小的内存分配（16M或32M）,这些存储空间有一部分
     * 是专门用于存储图片，当程序不断往内存中加载图片操作时，达到系统为图片分配的内存峰值后，系统就会 报OOM错误，被系统强制退出
     *
     * 在Android3.0之前，程序中对图片的内存缓存都是开发者自行控制的，这种方法容易导致OOM.
     *
     * 因为实际开发的需求,Google在3.0引入LruCache类实现内存的管理
     *
     * LruCache是一个在内存中缓存数据的容器，特点：1、可以指定容器的容量，2、基于Lru（最近最少使用） 算法来实现容器的数据的管理
     *
     * （2）.SoftReference （内存缓存） L2
     *
     * （3）.外部存储介质 （外部缓存） L3
     */
    private static MyImageCache myImageCache;

    private static Context context;

    private LruCache<String, Bitmap> lruCache;// 一级缓存
    private HashMap<String, SoftReference<Bitmap>> softMap;// 存放二级缓存的Map集合

    public static MyImageCache getImageCache(Context context) {
        if (myImageCache == null) {
            myImageCache = new MyImageCache(context);
        }
        return myImageCache;
    }

    private MyImageCache(Context context) {
        this.context = context;
        // 存放二级缓存的map集合
        softMap = new HashMap<String, SoftReference<Bitmap>>();
        // 获得程序分配到的总内存
        long tatalMemory = Runtime.getRuntime().maxMemory();
        // 获得想要lruCache的内存大小（一般是总内存的1/4或1/8）
        int maxSize = (int) (tatalMemory / 8);
        // 声明一级缓存强引用对象
        lruCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            // 计算每个添加的容器中的图片的占用内存数（修改容器的计量单位）
            protected int sizeOf(String key, Bitmap value) {
                // 计算每张图片占用的内存大小
                int rowByte = value.getRowBytes();// 获取图片每一行的字节数
                int height = value.getHeight();// 获得图片的高度
                return rowByte * height;
            }

//            @Override
//            // 显示指明从容器中被移除的图片的释放方式（按需求重写，即可以不用重写）
//            protected void entryRemoved(boolean evicted, String key,
//                                        Bitmap oldValue, Bitmap newValue) {
//                super.entryRemoved(evicted, key, oldValue, newValue);
//
//                // evicted: true缓存因LruCache容量不够而踢出图片对象
//                // evicted: false缓存因调用put<K,V>或remove(K)方法移除图片对象
//                if (evicted) {// true 则把oldValue存入软引用
//                    // 实现二级缓存（L2）
//                    SoftReference<Bitmap> reference = new SoftReference<Bitmap>(
//                            oldValue);
//                    softMap.put(key, reference);
//                }
//
//            }
        };
    }

    /**
     * 读取缓存图片对象
     *
     * @param url
     *            获取图片的链接，即key
     */
    @Override
    public Bitmap getBitmap(String url) {
        // 先读取一级缓存的图片
        Bitmap bitmapL1 = lruCache.get(url);
        if (bitmapL1 != null) {// 表示一级缓存中有数据
            return bitmapL1;
        }
        // 一级没有数据时，再读取二级缓存的图片
        SoftReference<Bitmap> soft = softMap.get(url);
        if (soft != null) {
            Bitmap bitmapL2 = soft.get();
            if (bitmapL2 != null) {// 表示二级缓存中有数据
                // 重新把使用的图片存入强引用中
                lruCache.put(url, bitmapL2);
                return bitmapL2;
            }
        }
        // 读取三级缓存的图片

        File cacheFile = getCacheFile(context);
        return readCache(url, cacheFile);

    }

    /**
     * 缓存图片
     *
     * @param url
     *            当前图片对应的url(用于作唯一标识Key)
     * @param bitmap
     *            是Volley通过ImageLoader加载的图片
     */
    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        // 实现一级缓存（L1）
        lruCache.put(url, bitmap);

        // 实现三级缓存（L3）
        File cacheFile = getCacheFile(context);
        WriteCache(bitmap, cacheFile, url);
    }

    /**
     * 将图片数据写入缓存，即保存到本地缓存
     *
     * @param bitmap
     * @param cacheFile
     * @param str
     */
    private void WriteCache(Bitmap bitmap, File cacheFile, String url) {
        String[] str = url.split("/");

        Bitmap newBitmap = MySurveyApplication.compressImage(bitmap);

        try {
            FileOutputStream fos = new FileOutputStream(
                    cacheFile.getAbsolutePath() + File.separator
                            + str[str.length - 1]);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取本地缓存的图片
     *
     * @param url
     * @param cacheFile
     * @return Bitmap
     */
    private Bitmap readCache(String url, File cacheFile) {
        String[] str = url.split("/");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(cacheFile.getAbsolutePath()
                    + File.separator + str[str.length - 1]);

            //return MySurveyApplication.decodeSampledBitmapFromFile(imgPath, 1080 / 2, 1920 / 2);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }

    /**
     * 获得Cache文件
     */
    private File getCacheFile(Context context) {
        File cacheFile = null;

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {// 判断SD卡是否挂载
            // 在SD卡上创建缓存文件夹
            File sdCache = context.getExternalCacheDir();
            cacheFile = sdCache;
        } else {
            // 在手机自带的存储空间创建缓存文件夹
            File internalCache = context.getCacheDir();
            cacheFile = internalCache;
        }
        return cacheFile;
    }

}
