package com.malalaoshi.android.qr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by kang on 16/11/7.
 */

public class QRCodeUtil {
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    public static Bitmap createQRCode(String contentsToEncode, int dimension) throws WriterException {

        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
            //容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        }
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(contentsToEncode,
                    BarcodeFormat.QR_CODE, dimension, dimension, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        // 下面这里按照二维码的算法，逐个生成二维码的图片，
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static Bitmap createQRCodeWithLogo(Context context, String contentsToEncode, int dimension, int drawableId) throws WriterException{
        Bitmap bmp= BitmapFactory.decodeResource(context.getResources(), drawableId);
        return QRCodeUtil.createQRCodeWithLogo(contentsToEncode,dimension,bmp);
    }
    /**
     * 生成带logo的二维码，logo默认为二维码的1/5
     */
    public static Bitmap createQRCodeWithLogo(String contentsToEncode, int dimension, Bitmap logo) throws WriterException  {
        int logoHeight = dimension / 14;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
            //容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        }
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(contentsToEncode,
                    BarcodeFormat.QR_CODE, dimension, dimension, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }

        // 下面这里按照二维码的算法，逐个生成二维码的图片，
        int width = result.getWidth();
        int height = result.getHeight();

        int halfW = width / 2;
        int halfH = height / 2;

        Matrix m = new Matrix();
        float sx = (float) 2 * logoHeight / logo.getWidth();
        float sy = (float) 2 * logoHeight / logo.getHeight();
        m.setScale(sx, sy);
        //设置缩放信息
        //将logo图片按martix设置的信息缩放
        logo = Bitmap.createBitmap(logo, 0, 0,
                logo.getWidth(), logo.getHeight(), m, false);

        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                if (x > halfW - logoHeight && x < halfW + logoHeight
                        && y > halfH - logoHeight
                        && y < halfH + logoHeight) {
                    //该位置用于存放图片信息
                    //记录图片每个像素信息
                    pixels[offset + x] = logo.getPixel(x - halfW
                            + logoHeight, y - halfH + logoHeight);
                } else {
                    pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

}
