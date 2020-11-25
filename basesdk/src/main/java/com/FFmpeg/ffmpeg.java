package com.FFmpeg;


public class ffmpeg {

    public ffmpeg() {

    }

    static {
        try {
            System.loadLibrary("ffmpeg");
            System.loadLibrary("myffmpeg");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    public native int Init(int width, int height);

    public native int Destroy();

    public native int DecoderNal(byte[] in, int insize, byte[] out);

}