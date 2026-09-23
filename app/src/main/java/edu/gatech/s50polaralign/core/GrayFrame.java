package edu.gatech.s50polaralign.core;

public record GrayFrame(int width, int height, float[] pixels, long capturedAtMillis) {
    public GrayFrame {
        if (width < 1 || height < 1 || pixels.length != width * height) throw new IllegalArgumentException("invalid frame");
    }
}
