package edu.gatech.s50polaralign;

import android.test.InstrumentationTestCase;
import edu.gatech.s50polaralign.core.PolarAlignment;
import java.util.*;

public final class PolarWorkflowInstrumentedTest extends InstrumentationTestCase {
    public void testThreeFrameWorkflow(){
        List<PolarAlignment.SkyPoint> p=new ArrayList<>();double ce=.18,cn=-.12,r=2;
        for(double a:new double[]{-.8,.1,1.0})p.add(new PolarAlignment.SkyPoint(ce+r*Math.cos(a),cn+r*Math.sin(a),0));
        PolarAlignment.Result q=new PolarAlignment().fit(p);
        assertEquals(-.18,q.eastErrorDeg(),1e-8);assertEquals(.12,q.northErrorDeg(),1e-8);
    }
}
