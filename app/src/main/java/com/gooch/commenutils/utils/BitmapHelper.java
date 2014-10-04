package com.gooch.commenutils.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by goocher on 2014/10/4.
 */
public class BitmapHelper {
    public static Bitmap createThumbnail(Resources res, int id, int width, int height) {
        //options它是一个图片采样的参数
        BitmapFactory.Options options = new BitmapFactory.Options();

        //只采样边界
        options.inJustDecodeBounds = true;

        //把设置好的采样属性应用到具体的采样过程上
        BitmapFactory.decodeResource(res, id, options);

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
        return BitmapFactory.decodeResource(res,id,options);
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
}
