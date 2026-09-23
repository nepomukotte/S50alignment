package edu.gatech.s50polaralign.core;

import java.util.*;

/** Offline geometric matcher. Catalog coordinates are a local tangent plane in degrees. */
public final class TriangleMatcher {
    public List<SimilaritySolver.Match> match(List<Star> image,List<Star> catalog,double toleranceFraction){
        int ni=Math.min(18,image.size()),nc=Math.min(80,catalog.size());
        Candidate best=null;
        for(int i=0;i<ni-2;i++)for(int j=i+1;j<ni-1;j++)for(int k=j+1;k<ni;k++){
            double[] si=signature(image.get(i),image.get(j),image.get(k));
            for(int a=0;a<nc-2;a++)for(int b=a+1;b<nc-1;b++)for(int c=b+1;c<nc;c++){
                double[] sc=signature(catalog.get(a),catalog.get(b),catalog.get(c));
                double e=Math.abs(si[0]-sc[0])+Math.abs(si[1]-sc[1]);if(e>toleranceFraction)continue;
                for(int[]perm:PERMS){
                    List<SimilaritySolver.Match> seed=List.of(new SimilaritySolver.Match(image.get(i),catalog.get(new int[]{a,b,c}[perm[0]])),new SimilaritySolver.Match(image.get(j),catalog.get(new int[]{a,b,c}[perm[1]])),new SimilaritySolver.Match(image.get(k),catalog.get(new int[]{a,b,c}[perm[2]])));
                    SimilaritySolver.Solution s;try{s=new SimilaritySolver().solve(seed,0,0);}catch(RuntimeException ex){continue;}
                    List<SimilaritySolver.Match> all=collect(image,catalog,s,Math.max(.015,s.scaleDegPerPixel()*2.5));
                    if(best==null||all.size()>best.matches.size())best=new Candidate(all);
                }
            }
        }
        if(best==null||best.matches.size()<3)throw new IllegalArgumentException("No catalog triangle matched");
        return best.matches;
    }
    private List<SimilaritySolver.Match> collect(List<Star> im,List<Star> cat,SimilaritySolver.Solution s,double max){
        double co=Math.cos(s.rotationRad()),sn=Math.sin(s.rotationRad()),scale=s.scaleDegPerPixel();List<SimilaritySolver.Match> out=new ArrayList<>();Set<Star>used=new HashSet<>();
        for(Star p:im){double u=s.centerRaDeg()+scale*(co*p.x()-sn*p.y()),v=s.centerDecDeg()+scale*(sn*p.x()+co*p.y());Star hit=null;double bd=max;for(Star q:cat){double d=Math.hypot(q.x()-u,q.y()-v);if(d<bd&&!used.contains(q)){bd=d;hit=q;}}if(hit!=null){used.add(hit);out.add(new SimilaritySolver.Match(p,hit));}}
        return out;
    }
    private double[] signature(Star a,Star b,Star c){double[]d={dist(a,b),dist(a,c),dist(b,c)};Arrays.sort(d);return new double[]{d[0]/d[2],d[1]/d[2]};}
    private double dist(Star a,Star b){return Math.hypot(a.x()-b.x(),a.y()-b.y());}
    private record Candidate(List<SimilaritySolver.Match> matches){}
    private static final int[][]PERMS={{0,1,2},{0,2,1},{1,0,2},{1,2,0},{2,0,1},{2,1,0}};
}
