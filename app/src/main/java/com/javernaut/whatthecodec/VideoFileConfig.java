package com.javernaut.whatthecodec;

public class VideoFileConfig {

    // The field is handled by the native code
    private long nativePointer;

    private VideoFileConfig(String filePath) {
        nativeNew(filePath);
    }

    public native String getFileFormat();

    public native String getCodecName();

    public native int getWidth();

    public native int getHeight();

    public native void release();

    public static VideoFileConfig create(String filePath) {
        VideoFileConfig result = new VideoFileConfig(filePath);
        if (result.nativePointer == -1) {
            return null;
        }
        return result;
    }

    private native void nativeNew(String filePath);

    static {
        System.loadLibrary("avformat");
        System.loadLibrary("avcodec");
        System.loadLibrary("avutil");
        System.loadLibrary("native-lib");
    }
}
