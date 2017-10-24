package com.victor.utils.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 自定义的BitmapUtils,实现三级缓存
 */
public class BitmapUtils {
    private NetCacheUtils mNetCacheUtils;  //网络缓存
    private LocalCacheUtils mLocalCacheUtils; //本地缓存
    private MemoryCacheUtils mMemoryCacheUtils; //内存缓存

    public BitmapUtils() {
        mLocalCacheUtils = new LocalCacheUtils();
        mMemoryCacheUtils = new MemoryCacheUtils();
        mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils, mMemoryCacheUtils);
    }

    public void disPlay(ImageView ivPic, String url) {
        Bitmap bitmap;
        //内存缓存
        bitmap = mMemoryCacheUtils.getBitmapFromMemory(url);
        if (bitmap != null) {
            ivPic.setImageBitmap(bitmap);
            return;
        }
        //本地缓存
        bitmap = mLocalCacheUtils.getBitmapFromLocal(url);
        if (bitmap != null) {
            ivPic.setImageBitmap(bitmap);
            //从本地获取图片后,保存至内存中
            mMemoryCacheUtils.setBitmapToMemory(url, bitmap);
            return;
        }
        mNetCacheUtils.getBitmapFromNet(ivPic, url);
    }
    /**
     * 三级缓存之网络缓存
     */
    class NetCacheUtils {
        //利用本地缓存和内存缓存实现网络缓存
        private LocalCacheUtils mLocalCacheUtils;
        private MemoryCacheUtils mMemoryCacheUtils;

        public NetCacheUtils(LocalCacheUtils localCacheUtils, MemoryCacheUtils memoryCacheUtils) {
            mLocalCacheUtils = localCacheUtils;
            mMemoryCacheUtils = memoryCacheUtils;
        }
        /**
         * 从网络下载图片
         *
         * @param myimage 显示图片的imageview
         * @param url     下载图片的网络地址
         */
        public void getBitmapFromNet(ImageView myimage, String url) {
            new BitmapTask().execute(myimage, url);//启动AsyncTask

        }
        /**
         * AsyncTask就是对handler和线程池的封装
         * 第一个泛型:参数类型
         * 第二个泛型:更新进度的泛型
         * 第三个泛型:onPostExecute的返回结果
         */
        class BitmapTask extends AsyncTask<Object, Void, Bitmap> {

            private ImageView ivPic;
            private String url;

            /**
             * 后台耗时操作,存在于子线程中
             *
             * @param params
             * @return
             */
            @Override
            protected Bitmap doInBackground(Object[] params) {
                ivPic = (ImageView) params[0];
                url = (String) params[1];

                return downLoadBitmap(url);
            }

            /**
             * 更新进度,在主线程中
             *
             * @param values
             */
            @Override
            protected void onProgressUpdate(Void[] values) {
                super.onProgressUpdate(values);
            }

            /**
             * 耗时方法结束后执行该方法,主线程中
             *
             * @param result
             */
            @Override
            protected void onPostExecute(Bitmap result) {
                if (result != null) {
                    ivPic.setImageBitmap(result);
                    System.out.println("从网络缓存图片啦.....");
                    //从网络获取图片后,保存至本地缓存
                    mLocalCacheUtils.setBitmapToLocal(url, result);
                    //保存至内存中
                    mMemoryCacheUtils.setBitmapToMemory(url, result);

                }
            }
        }
        /**
         * 利用httpconnection从网络下载图片
         *
         * @param url
         * @return
         */
        private Bitmap downLoadBitmap(String url) {
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    //图片压缩
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;//宽高压缩为原来的1/2
                    options.inPreferredConfig = Bitmap.Config.ARGB_4444;
                    Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream(), null, options);
                    return bitmap;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }

            return null;
        }
    }
    /**
     * 三级缓存之本地缓存
     */

    class LocalCacheUtils {
        public final String CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WerbNews";

        /**
         * 从本地读取图片
         *
         * @param url
         */
        public Bitmap getBitmapFromLocal(String url) {
            String fileName = null;//把图片的url当做文件名,
            try {
                fileName = "images" + url;
                File file = new File(CACHE_PATH, fileName);

                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * 从网络获取图片后,保存至本地缓存
         *
         * @param url
         * @param bitmap
         */
        public void setBitmapToLocal(String url, Bitmap bitmap) {
            try {
                String fileName = "images" + url;//把图片的url当做文件名
                File file = new File(CACHE_PATH, fileName);

                //通过得到文件的父文件,判断父文件是否存在
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                //把图片保存至本地
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    /**
     * 三级缓存之内存缓存
     */
    class MemoryCacheUtils {

        private LruCache<String, Bitmap> mMemoryCache;

        public MemoryCacheUtils() {
            long maxMemory = Runtime.getRuntime().maxMemory() / 8;//得到手机最大允许内存的1/8,即超过指定内存,则开始回收
            //需要传入允许的内存最大值,虚拟机默认内存16M,真机不一定相同
            mMemoryCache = new LruCache<String, Bitmap>((int) maxMemory) {
                //用于计算每个条目的大小
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    int byteCount = value.getByteCount();
                    return byteCount;
                }
            };

        }
        /**
         * 从内存中读图片
         *
         * @param url
         */
        public Bitmap getBitmapFromMemory(String url) {
            Bitmap bitmap = mMemoryCache.get(url);
            return bitmap;
        }
        /**
         * 往内存中写图片
         * @param url
         * @param bitmap
         */
        public void setBitmapToMemory(String url, Bitmap bitmap) {
            mMemoryCache.put(url, bitmap);
        }
    }

}