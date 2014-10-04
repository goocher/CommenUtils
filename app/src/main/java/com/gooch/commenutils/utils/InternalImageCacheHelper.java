package com.gooch.commenutils.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by goocher on 2014/10/4.
 */
public class InternalImageCacheHelper {
    //内存存储处理对象
    private MyLruCache myLruCache = null;

    //这两个常量用于表示图片保存在哪个目录下
    public static final int CACHE = 0;
    public static final int FILES = 1;
    private int type=CACHE;
    //用于修改存储位置，0代表cache目录，1代表file目录
    public void setType(int type){
        this.type=type;
    }
    private static Context context=null;

    //使用的时候，自己去注意线程同步安全问题，也就是在application对象中去实例这个
    private static InternalImageCacheHelper internalImageCacheHelper;

    public static InternalImageCacheHelper getImageCacheHelper(Context context) {
        InternalImageCacheHelper.context=context;
        if (InternalImageCacheHelper.internalImageCacheHelper == null) {
            internalImageCacheHelper = new InternalImageCacheHelper();
        }
        return InternalImageCacheHelper.internalImageCacheHelper;
    }

    private InternalImageCacheHelper() {
        //用于获取内部存储目录的上下文对象

        //获取到这个应用程序的总内存大小
        //Runtime.getRuntime()拿到运行时对象
        //运行时对象中可以获取这个程序运行时的最大内存maxMemory，以字节为单位。
        //获取到运行时的最大内存数，转化k为单位，然后再除以8，用于实例lurcache
        int memoryAmount = (int) (Runtime.getRuntime().maxMemory() / 1024 / 8);
        if (myLruCache == null) {
            //一般而言我们把最大内存的1/8作为图片处理空间
            myLruCache = new MyLruCache(memoryAmount);
        }
    }

    public void loadImage(String url, ImageView imageView) {
        Bitmap bitmap = null;
        //先在内存中寻找
        if (myLruCache != null) {
            //去myLruCache中寻找，用url作为key，也就是说，我们存入内存的时候也要用url作为key
            bitmap = myLruCache.get(url);
        }
        if (bitmap != null) {
            //找到了，就更新ui
            imageView.setImageBitmap(bitmap);
        } else {
            //内存中内找到，去sd卡中寻找
            String fileName = getMD5(url);
            File file =null;
            switch (type){
                case CACHE:
                    file=new File(context.getCacheDir(),fileName);
                    break;
                case FILES:
                    file=new File(context.getFilesDir(),fileName);
                    break;
            }

            if (file.exists()) {
                //从sd卡上找图片，加载到内存中用二次采样的方式
                bitmap =createThumbnail(file.getAbsolutePath(),imageView.getWidth(),imageView.getHeight());
            }
            //判断bitmap是否在sd卡上加载成功
            if (bitmap != null) {
                //在sd卡上加载了bitmap
                //更新ui
                imageView.setImageBitmap(bitmap);
                //放入内存中一份，留着下次用。
                myLruCache.put(url, bitmap);
            } else {
                //在sd卡上没加载成功
                //就去请求网络
                new ImageAsyncTask(imageView).execute(url);
            }

        }


    }

    class ImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView imageView = null;

        private int width=0;
        private int height=0;
        public ImageAsyncTask(ImageView imageView) {
            this.imageView = imageView;
            width=imageView.getWidth();
            height=imageView.getHeight();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            //请求网络数据
            byte[] data = loadByteFromURL(params[0]);
            //如果请求到了，应该放在sd卡上一份，放在内存中一份
            if (data != null) {
                String fileName = getMD5(params[0]);
                //保存到内部存储一份
                switch (type){
                    case CACHE:
                        otherWriter(fileName,data,context,CACHE);
                        break;
                    case FILES:
                        otherWriter(fileName,data,context,FILES);
                        break;
                }



                File file =null;
                switch (type){
                    case CACHE:
                        file=new File(context.getCacheDir(),fileName);
                        break;
                    case FILES:
                        file=new File(context.getFilesDir(),fileName);
                        break;
                }
                if(!file.exists()){
                    Log.i("file","文件不存在");
                }
                //获取保存到sd卡上的bitmap对象
                Bitmap bitmap =createThumbnail(file.getAbsolutePath(),width,height);
                //保存到内存一份
//                Log.i("params[0]",params[0].toString());
//                Log.i("bitmap",bitmap.toString());
                myLruCache.put(params[0],bitmap);

                return bitmap;
            }
//            BitmapFactory

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    class MyLruCache extends LruCache<String, Bitmap> {

        /**
         * @param maxSize for caches that do not override {@link #sizeOf}, this is
         *                the maximum number of entries in the cache. For all other caches,
         *                this is the maximum sum of the sizes of the entries in this cache.
         *                我们说lrucache是一个容器，这个参数描述这个容器有多大。
         */

        public MyLruCache(int maxSize) {
            super(maxSize);
        }

        //这个方法通过key来获取每一个对象的大小（计算时使用的大小）
        @Override
        protected int sizeOf(String key, Bitmap value) {
            //两个参数，key，value，就和map一样
            //lrucache使用键值对的形式来处理具体的算法
            //这个lrucache计算大小以k为单位
            return value.getByteCount() / 1024;
        }

        //这个方法在lrucache开始淘汰某个对象时回调
        //有东西移除分为两种情况，
        //1、既然是以键值对的形式存储，就可能key相同存入，key如果已经存在，就把之前的value移除
        //2、当lrucache满了，再往里放入key，value，虽然没有key重复，但是一定要有对象出来，给新的
        //进去的对象提供空间。也就是要去判断哪个是最不可能近期被使用的对象，把它移除。
        //当evicted为真的时候对应第二种情况
        @Override
        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
            super.entryRemoved(evicted, key, oldValue, newValue);
        }
    }

    private static final char hexDigsits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    //封装的，获取md5的方法
    public static String getMD5(String inStr) {
        //加密需要的字节数组，于是首先拿到内容的字节数组
        byte[] inStrBytes = inStr.getBytes();

        //消息摘要对象,指定为MD5方式
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            //把摘要对象中的数据设置为我们的内容
            messageDigest.update(inStrBytes);
            //拿到MD5算法的结果
            byte[] messageDbytes = messageDigest.digest();
            //我们把结果转化为16进制的格式
            char[] str = new char[messageDbytes.length * 2];
            //循环是使用的角标
            int k = 0;
            for (int i = 0; i < messageDbytes.length; i++) {
                byte temp = messageDbytes[i];
                //把一个字节分裂成两个部分，分别转化为十六进制的字符
                str[k++] = hexDigsits[temp >>> 4 & 0xf];
                str[k++] = hexDigsits[temp & 0xf];
            }
            return new String(str);


        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return null;
    }





    public static byte[] loadByteFromURL(String url) {
        HttpURLConnection httpConn = null;
        BufferedInputStream bis = null;
        try {
            URL urlObj = new URL(url);
            httpConn = (HttpURLConnection) urlObj.openConnection();
            httpConn.setRequestMethod("GET");

            httpConn.setDoInput(true);
            httpConn.setConnectTimeout(5000);
            httpConn.connect();
            if (httpConn.getResponseCode() == 200) {
                bis = new BufferedInputStream(httpConn.getInputStream());
                return streamToByte(bis);
            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return null;
    }

    public static byte[] streamToByte(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int c = 0;
        byte[] buffer = new byte[8 * 1024];
        try {
            while ((c = is.read(buffer)) != -1) {
                baos.write(buffer, 0, c);
                baos.flush();

            }

            return baos.toByteArray();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }

            } catch (Exception e2) {
                // TODO: handle exception
                e2.printStackTrace();
            }


        }


        return null;
    }
    public static Bitmap createThumbnail(String filePath, int width, int height) {
        //options它是一个图片采样的参数
        BitmapFactory.Options options = new BitmapFactory.Options();

        //只采样边界
        options.inJustDecodeBounds = true;


        //把设置好的采样属性应用到具体的采样过程上
        BitmapFactory.decodeFile(filePath,options);

        //取出图片的宽高
        int oldWidth = options.outWidth;
        int oldHeight = options.outHeight;

        //计算宽高的比例值
        int ratioWidth = oldWidth / width;
        int ratioHeight = oldHeight / height;

        //把比例值设置给采样的参数。
        //可能会使得图片发模糊，但是节约内存
//        options.inSampleSize=ratioHeight>ratioWidth?ratioHeight:ratioWidth;
        //不会造成图片模糊，但是消耗内存
        options.inSampleSize = ratioHeight < ratioWidth ? ratioHeight : ratioWidth;

        //为第二次采样做准备
        options.inJustDecodeBounds=false;
        //设置像素点的格式
        options.inPreferredConfig= Bitmap.Config.RGB_565;

        //把第二次采样的结果返回。
        return  BitmapFactory.decodeFile(filePath,options);
    }
    // 第二种文件保存方式
    public static void otherWriter(String fileName, byte[] content,
                                   Context context, int type) {
        // 判断文件名
        if (fileName == null || "".equals(fileName)) {
            return;
        }
        FileOutputStream outputStream = null;
        File file = null;
        switch (type) {
            case CACHE:
                file = new File(context.getCacheDir(), fileName);
                break;
            case FILES:
                file = new File(context.getFilesDir(), fileName);
                break;

            default:
                break;
        }
        // 对文件进行操作
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(content);
            outputStream.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}
