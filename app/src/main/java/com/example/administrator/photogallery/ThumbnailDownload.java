package com.example.administrator.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 *继承于HandlerThread线程类，运行于独立的线程中（非UI线程，这是于Handler类的主要区别：Handler寄生于创建它的线程，可以是UI线程）
 *
 * @param <T> 将在某个方法中使用泛型T
 *
 */
public class ThumbnailDownload<T> extends HandlerThread {

    private static final String TAG = "ThumbnailDownloader";
    private Boolean mHasQuit = false;

    private static final int MESSAGE_DOWNLOAD = 0;
    private Handler mRequestHandler;
    private Handler mResponseHandler;
    private ConcurrentMap<T,String> mRequestMap = new ConcurrentHashMap<>();

    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;
    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }


    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
        mThumbnailDownloadListener = listener;
    }

    public ThumbnailDownload(Handler responseHandler) {
        super(TAG);

        mResponseHandler = responseHandler;
    }

    @Override
    protected void onLooperPrepared() {

        mRequestHandler = new Handler() {

            /**
             * 在这里执行图片的下载操作
             * @param msg
             */
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }



    /**
     * 该方法用于发起一个请求
     * @param target 泛型参数T ：将用于保存ViewHolder对象该对象会被ReccyclerView回收，因此需要保证其中的url及时更新
     * @param url 参数T 配对的url
     */
    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a URL: " + url);

        if (url == null) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
                    .sendToTarget();
        }
    }

    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
        mRequestMap.clear();
    }

    /**
     * 私有方法处理下载请求
     * @param target
     */
    private void handleRequest(final T target) {
        try {
            final String url = mRequestMap.get(target);
            if (url == null) {
                return;
            }
            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");

            //一旦下载完成，使用ResponseHandler在主线程中执行该Runnable对象
            mResponseHandler.post(new Runnable() {
                public void run() {
                    if (mRequestMap.get(target) != url ||
                            mHasQuit) {
                        return;
                    }
                    mRequestMap.remove(target);
                    mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);//某种套路/模式？？
                }
            });
        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }




}
