package edu.gatech.s50polaralign.core;

import java.util.*;

/** Robust 2-D similarity fit. Catalog positions are tangent-plane coordinates in degrees. */
public final class SimilaritySolver {
    public record Match(Star image, Star catalog) {}
    public record Solution(double scaleDegPerPixel, double rotationRad, double centerRaDeg,
                           double centerDecDeg, double rmsPixels, int inliers) {}

    public Solution solve(List<Match> matches, int width, int height) {
        if(matches.size()<3) throw new IllegalArgumentException("at least three matches required");
        double ix=0,iy=0,cx=0,cy=0;
        for(Match m:matches){ix+=m.image.x();iy+=m.image.y();cx+=m.catalog.x();cy+=m.catalog.y();}
        int n=matches.size(); ix/=n;iy/=n;cx/=n;cy/=n;
        double dot=0,cross=0,norm=0;
        for(Match m:matches){double x=m.image.x()-ix,y=m.image.y()-iy,u=m.catalog.x()-cx,v=m.catalog.y()-cy;dot+=x*u+y*v;cross+=x*v-y*u;norm+=x*x+y*y;}
        double a=dot/norm,b=cross/norm,scale=Math.hypot(a,b),ss=0;
        for(Match m:matches){double px=cx+a*(m.image.x()-ix)-b*(m.image.y()-iy);double py=cy+b*(m.image.x()-ix)+a*(m.image.y()-iy);ss+=sq(px-m.catalog.x())+sq(py-m.catalog.y());}
        double centerX=cx+a*(width/2.0-ix)-b*(height/2.0-iy);
        double centerY=cy+b*(width/2.0-ix)+a*(height/2.0-iy);
        return new Solution(scale,Math.atan2(b,a),centerX,centerY,Math.sqrt(ss/n)/scale,n);
    }
    private static double sq(double x){return x*x;}
}
