package edu.gatech.s50polaralign;

import android.graphics.*;
import edu.gatech.s50polaralign.core.GrayFrame;
import java.io.*;
import java.net.*;

/** Fetches a user-configured JPEG snapshot; it intentionally assumes no undocumented S50 endpoint. */
public final class S50SnapshotClient {
    public GrayFrame fetch(String url) throws IOException {
        HttpURLConnection c=(HttpURLConnection)new URL(url).openConnection();
        c.setConnectTimeout(1500); c.setReadTimeout(2500); c.setUseCaches(false);
        try(InputStream in=c.getInputStream()){
            Bitmap b=BitmapFactory.decodeStream(in); if(b==null)throw new IOException("Not a JPEG/PNG frame");
            int w=b.getWidth(),h=b.getHeight(); int[] rgb=new int[w*h];float[] gray=new float[w*h];b.getPixels(rgb,0,w,0,0,w,h);
            for(int i=0;i<rgb.length;i++)gray[i]=(.2126f*Color.red(rgb[i])+.7152f*Color.green(rgb[i])+.0722f*Color.blue(rgb[i]));
            return new GrayFrame(w,h,gray,System.currentTimeMillis());
        } finally { c.disconnect(); }
    }
}
