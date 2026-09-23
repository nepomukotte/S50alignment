package edu.gatech.s50polaralign.core;

import java.util.*;

/** Deterministic, allocation-light local-maxima detector suitable for JNI/GPU preprocessed frames. */
public final class StarDetector {
    public List<Star> detect(GrayFrame frame, int maxStars) {
        float[] p = frame.pixels(); int w = frame.width(), h = frame.height();
        double sum = 0, sum2 = 0;
        for (float v : p) { sum += v; sum2 += (double)v * v; }
        double mean = sum / p.length;
        double sigma = Math.sqrt(Math.max(0, sum2 / p.length - mean * mean));
        double threshold = mean + Math.max(8.0, 3.8 * sigma);
        List<Star> out = new ArrayList<>();
        for (int y=2; y<h-2; y++) for (int x=2; x<w-2; x++) {
            int i=y*w+x; float v=p[i]; if (v < threshold) continue;
            boolean peak=true;
            for (int dy=-1;dy<=1 && peak;dy++) for(int dx=-1;dx<=1;dx++)
                if ((dx!=0||dy!=0) && p[i+dy*w+dx] >= v) { peak=false; break; }
            if (!peak) continue;
            double weighted=0,cx=0,cy=0;
            for(int dy=-2;dy<=2;dy++) for(int dx=-2;dx<=2;dx++) {
                double q=Math.max(0,p[i+dy*w+dx]-mean); weighted+=q; cx+=(x+dx)*q; cy+=(y+dy)*q;
            }
            if(weighted>0) out.add(new Star(cx/weighted,cy/weighted,weighted));
        }
        out.sort(Comparator.comparingDouble(Star::flux).reversed());
        return out.subList(0, Math.min(maxStars,out.size()));
    }
}
