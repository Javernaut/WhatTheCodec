package com.javernaut.whatthecodec;

public class VideoFileConfig {

    private final long nativePointer;

    private VideoFileConfig(long nativePointer) {
        this.nativePointer = nativePointer;
    }

    public String getFileFormat() {
        return nativeGetFileFormat(nativePointer);
    }

    public void release() {
        release(nativePointer);
    }

    public static VideoFileConfig create(String filePath) {
        long nativePointer = nativeCreate(filePath);
        if (nativePointer == -1) {
            return null;
        }

        return new VideoFileConfig(nativePointer);
    }

    private static native long nativeCreate(String filePath);

    private static native String nativeGetFileFormat(long nativePointer);

    private static native void release(long nativePointer);

    static {
        System.loadLibrary("avformat");
        System.loadLibrary("avcodec");
        System.loadLibrary("avutil");
        System.loadLibrary("native-lib");
    }
}
