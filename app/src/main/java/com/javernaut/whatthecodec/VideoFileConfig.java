package com.javernaut.whatthecodec;

import android.graphics.Bitmap;
import android.os.ParcelFileDescriptor;

public class VideoFileConfig {

    // The field is handled by the native code
    private long nativePointer;

    private VideoFileConfig(int fileDescriptor) {
        nativeNew(fileDescriptor);
    }

    public native String getFileFormat();

    public native String getCodecName();

    public native int getWidth();

    public native int getHeight();

    public native void release();

    public native void fillWithPreview(Bitmap bitmap);

    public static VideoFileConfig create(ParcelFileDescriptor descriptor) {
        VideoFileConfig result = new VideoFileConfig(descriptor.detachFd());
        if (result.nativePointer == -1) {
            return null;
        }
        return result;
    }

    private native void nativeNew(int fileDescriptor);

    static {
        System.loadLibrary("avformat");
        System.loadLibrary("avcodec");
        System.loadLibrary("avutil");
        System.loadLibrary("swscale");
        System.loadLibrary("video-config");
    }
}
