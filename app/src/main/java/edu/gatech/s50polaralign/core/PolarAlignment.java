package edu.gatech.s50polaralign.core;

import java.util.*;

/** Fits the mount-axis circle from solved sky centers and reports the move to the celestial pole. */
public final class PolarAlignment {
    public record SkyPoint(double eastDeg,double northDeg,long timeMillis) {}
    public record Result(double eastErrorDeg,double northErrorDeg,double totalErrorDeg,double confidence) {
        public String instruction(){
            String az=eastErrorDeg>=0?"right":"left", alt=northErrorDeg>=0?"up":"down";
            return String.format(Locale.US,"Move azimuth %s %.2f°, altitude %s %.2f°",az,Math.abs(eastErrorDeg),alt,Math.abs(northErrorDeg));
        }
    }
    public Result fit(List<SkyPoint> p){
        if(p.size()<3) throw new IllegalArgumentException("Capture at least three separated positions");
        double sxx=0,sxy=0,sx=0,syy=0,sy=0,sxz=0,syz=0,sz=0;
        for(SkyPoint q:p){double x=q.eastDeg(),y=q.northDeg(),z=-(x*x+y*y);sxx+=x*x;sxy+=x*y;sx+=x;syy+=y*y;sy+=y;sxz+=x*z;syz+=y*z;sz+=z;}
        double[][] a={{sxx,sxy,sx},{sxy,syy,sy},{sx,sy,p.size()}}; double[] b={sxz,syz,sz};
        double[] v=solve3(a,b); double ce=-v[0]/2,cn=-v[1]/2;
        double residual=0,r=0; for(SkyPoint q:p) r+=Math.hypot(q.eastDeg()-ce,q.northDeg()-cn); r/=p.size();
        for(SkyPoint q:p) residual+=sq(Math.hypot(q.eastDeg()-ce,q.northDeg()-cn)-r); residual=Math.sqrt(residual/p.size());
        return new Result(-ce,-cn,Math.hypot(ce,cn),Math.max(0,Math.min(1,1-residual/Math.max(.01,r))));
    }
    private static double[] solve3(double[][]a,double[]b){
        for(int i=0;i<3;i++){int k=i;for(int j=i+1;j<3;j++)if(Math.abs(a[j][i])>Math.abs(a[k][i]))k=j;double[]t=a[i];a[i]=a[k];a[k]=t;double z=b[i];b[i]=b[k];b[k]=z;if(Math.abs(a[i][i])<1e-12)throw new IllegalArgumentException("positions do not span an arc");for(int j=i+1;j<3;j++){double f=a[j][i]/a[i][i];for(int c=i;c<3;c++)a[j][c]-=f*a[i][c];b[j]-=f*b[i];}}
        double[]x=new double[3];for(int i=2;i>=0;i--){double s=b[i];for(int j=i+1;j<3;j++)s-=a[i][j]*x[j];x[i]=s/a[i][i];}return x;
    }
    private static double sq(double x){return x*x;}
}
