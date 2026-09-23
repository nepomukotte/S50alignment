import edu.gatech.s50polaralign.core.*;
import java.util.*;

public final class CoreWorkflowTest {
    static void near(double got,double want,double eps,String what){if(Math.abs(got-want)>eps)throw new AssertionError(what+": "+got+" != "+want);}
    public static void main(String[] args){
        float[] px=new float[80*60]; Arrays.fill(px,10); int[][] stars={{12,14},{38,22},{64,45},{25,48}};
        for(int[]s:stars)for(int dy=-2;dy<=2;dy++)for(int dx=-2;dx<=2;dx++)px[(s[1]+dy)*80+s[0]+dx]+=(float)(180*Math.exp(-(dx*dx+dy*dy)/2.0));
        List<Star> found=new StarDetector().detect(new GrayFrame(80,60,px,0),20);
        if(found.size()!=4)throw new AssertionError("expected 4 stars, got "+found.size());
        List<SimilaritySolver.Match> m=new ArrayList<>(); double a=.02,b=.003,tx=120,ty=45;
        for(Star s:found)m.add(new SimilaritySolver.Match(s,new Star(tx+a*s.x()-b*s.y(),ty+b*s.x()+a*s.y(),s.flux())));
        SimilaritySolver.Solution sol=new SimilaritySolver().solve(m,80,60);near(sol.scaleDegPerPixel(),Math.hypot(a,b),1e-8,"scale");near(sol.rmsPixels(),0,1e-7,"WCS RMS");
        List<Star> catalog=new ArrayList<>();for(Star s:found)catalog.add(new Star(tx+a*s.x()-b*s.y(),ty+b*s.x()+a*s.y(),s.flux()));catalog.add(new Star(99,88,1));
        List<SimilaritySolver.Match> blind=new TriangleMatcher().match(found,catalog,.025);if(blind.size()!=4)throw new AssertionError("triangle matcher found "+blind.size());
        double ce=.18,cn=-.12,r=2;List<PolarAlignment.SkyPoint> p=new ArrayList<>();for(double q:new double[]{-.8,.1,1.0,1.8})p.add(new PolarAlignment.SkyPoint(ce+r*Math.cos(q),cn+r*Math.sin(q),0));
        PolarAlignment.Result result=new PolarAlignment().fit(p);near(result.eastErrorDeg(),-ce,1e-9,"east correction");near(result.northErrorDeg(),-cn,1e-9,"north correction");
        System.out.println("PASS: detected stars, matched offline catalog, solved tangent plane, fitted polar axis; error="+result.totalErrorDeg());
    }
}
