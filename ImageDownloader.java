package mh.com.tools.http;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mh.com.tools.commontools.LibConfigs;
import mh.com.tools.commontools.LibConstants;
import mh.com.tools.commontools.LibLogHelper;
import mh.com.tools.utils.FileUtils;
import mh.com.tools.utils.StorageUtils;


public class ImageDownloader implements ImageLoader.ImageCache {

    private static final String TAG = "ImageDownloader";
    private static final boolean DEBUG = LibConfigs.DEBUG;

    //max cache size
    private static final int MAX_SIZE = 10;
    private static final int STROKE_URL_LEN = 6;

    private static volatile ImageDownloader mInstance;

    private RequestQueue mQueue;

    private ImageLoader mImageLoader;
    private LruCache<String, Bitmap> mImgCache;

    /**
     * @param cxt application context, not activity context
     * use singleton pattern, the whole app use only one queue
     */
    public static ImageDownloader getInstance(Context cxt) {
        if (mInstance == null) {
            synchronized (ImageDownloader.class) {
                if (mInstance == null) {
                    mInstance = new ImageDownloader(cxt);
                }
            }
        }
        return mInstance;
    }

    public ImageDownloader(Context cxt) {
        mQueue = Volley.newRequestQueue(cxt.getApplicationContext());

        mImageLoader = new ImageLoader(mQueue, this);
        mImgCache = new LruCache<>(MAX_SIZE);
    }

    public Request add(Request request) {
        if (request != null) {
            return mQueue.add(request);
        }
        return null;
    }

    public void cancelAll(Object tag) {
        mQueue.cancelAll(tag);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public LruCache<String, Bitmap> getImgCache() {
        return mImgCache;
    }

    /**
     * get image from cache or the default path
     * if the image doesn't exists, return null
     */
    @Override
    public Bitmap getBitmap(String url) {
        String imgName = convertUrlToImgName(url);
        if (mImgCache.get(imgName) != null) {
            return mImgCache.get(imgName);
        }
        if (hasImageFile(url)) {
            Bitmap bitmap = decodeFile(LibConstants.IMG_PATH + "/" + imgName);
            if (bitmap == null) {
                FileUtils.deleteFile(LibConstants.IMG_PATH, imgName);
            } else {
                mImgCache.put(imgName, bitmap);
            }
        }
        return null;
    }

    /**
     * get image from the path
     * if the image doesn't exists, return null
     */
    public Bitmap getBitmap(String url, String path) {
        String imgName = convertUrlToImgName(url);
        if (mImgCache.get(imgName) != null) {
            return mImgCache.get(imgName);
        }
        if (hasImageFile(url, path)) {
            Bitmap bitmap = decodeFile(path + "/" + imgName);
            if (bitmap == null) {
                FileUtils.deleteFile(path, imgName);
            } else {
                mImgCache.put(imgName, bitmap);
            }
        }
        return null;
    }

    /**
     * 使用volley下载图片之后url在头部会加上“#W0#H0”
     * 保存的时候需要去除，否则会出问题
     */
    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        if (DEBUG) {
            LibLogHelper.d(TAG, "putBitmap : " + url);
        }
        int len = url.length();
        if (len > STROKE_URL_LEN) {
            url = url.substring(STROKE_URL_LEN, len);
        }
        if (DEBUG) {
            LibLogHelper.d(TAG, "url : " + url);
        }

        String imgName = convertUrlToImgName(url);
        mImgCache.put(imgName, bitmap);
        saveBitmap(bitmap, imgName, LibConstants.IMG_PATH);
    }

    /**
     * convert a network image's url to it's name
     */
    public String convertUrlToImgName(String url) {
        // 只允许字母和数字
        String regEx = "[^a-zA-Z0-9]";
        // 清除掉所有特殊字
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(url);
        return m.replaceAll("").trim();
    }

    /**
     * @param url the network image url
     * to find whether the image is in the default path
     */
    public boolean hasImage(String url) {
        String imgName = convertUrlToImgName(url);
        if (DEBUG) {
            LibLogHelper.d(TAG, "mImgCache has image : " + (mImgCache.get(imgName) != null));
            LibLogHelper.d(TAG, "image file exists : " + new File(LibConstants.IMG_PATH, imgName).exists());
        }
        return mImgCache.get(imgName) != null || new File(LibConstants.IMG_PATH, imgName).exists();
    }

    /**
     * @param url the network image url
     * to find whether the image is in the default path
     */
    public boolean hasImageFile(String url) {
        String imgName = convertUrlToImgName(url);
        return new File(LibConstants.IMG_PATH, imgName).exists();
    }

    /**
     * @param url the network image url
     * to find whether the image is in the default path
     */
    public boolean hasImageFile(String url, String path) {
        String imgName = convertUrlToImgName(url);
        return new File(path, imgName).exists();
    }

    /**
     * @param url the network image url
     * @param path the image storage path
     * to find whether the image is in the path
     */
    public boolean hasImage(String url, String path) {
        String imgName = convertUrlToImgName(url);
        return mImgCache.get(url) != null || new File(path, imgName).exists();
    }

    private boolean saveBitmap(Bitmap bitmap, String imgName, String path) {
        if (DEBUG) {
            LibLogHelper.d(TAG, "saveBitmap, path : " + path);
        }
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            boolean makeDir = dirFile.mkdirs();
            if (!makeDir) {
                if (DEBUG) {
                    LibLogHelper.d(TAG, "make dir failed.");
                }
                return false;
            }
        }

        path = path + imgName;
        if (DEBUG) {
            LibLogHelper.d(TAG, "path : " + path);
        }
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream os = null;
        try {
            os = new FileOutputStream(path);
            return bitmap.compress(format, quality, os);
        } catch (FileNotFoundException e) {
            if (DEBUG) {
                LibLogHelper.w(TAG, "unexpected : ", e);
            }
        } finally {
            FileUtils.close(os);
        }

        return false;
    }

    private Bitmap decodeFile(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;

        if (!StorageUtils.isExternalStorageAvailable()) return null;
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            fis = new FileInputStream(path);
            bitmap = BitmapFactory.decodeStream(fis, null, opts);
        } catch (FileNotFoundException e) {
            if (DEBUG) {
                LibLogHelper.w(TAG, "unexpected : ", e);
            }
        } finally {
            FileUtils.close(fis);
        }

        return bitmap;
    }
}

