package com.javernaut.whatthecodec;

public class VideoFileConfig {

    // The field is handled by the native code
    private long nativePointer;

    private VideoFileConfig(String filePath) {
        nativeNew(filePath);
    }

    public String getFileFormat() {
        return nativeGetFileFormat(nativePointer);
    }

    public String getCodecName() {
        return nativeGetCodecName(nativePointer);
    }

    public int getWidth() {
        return nativeGetWidth(nativePointer);
    }

    public int getHeight() {
        return nativeGetHeight(nativePointer);
    }

    public void release() {
        release(nativePointer);
    }

    public static VideoFileConfig create(String filePath) {
        VideoFileConfig result = new VideoFileConfig(filePath);
        if (result.nativePointer == -1) {
            return null;
        }
        return result;
    }

    private native void nativeNew(String filePath);

    private static native String nativeGetFileFormat(long nativePointer);

    private static native String nativeGetCodecName(long nativePointer);

    private static native int nativeGetWidth(long nativePointer);

    private static native int nativeGetHeight(long nativePointer);

    private static native void release(long nativePointer);

    static {
        System.loadLibrary("avformat");
        System.loadLibrary("avcodec");
        System.loadLibrary("avutil");
        System.loadLibrary("native-lib");
    }
}
