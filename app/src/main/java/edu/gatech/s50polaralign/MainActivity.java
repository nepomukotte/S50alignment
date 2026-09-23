package edu.gatech.s50polaralign;

import android.app.*;
import android.os.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import edu.gatech.s50polaralign.core.*;
import java.util.*;

public final class MainActivity extends Activity {
    private LinearLayout root; private TextView status; private final List<PolarAlignment.SkyPoint> points=new ArrayList<>();
    @Override public void onCreate(Bundle b){super.onCreate(b);render();}
    private TextView text(String value,int sp){TextView v=new TextView(this);v.setText(value);v.setTextColor(Color.rgb(220,238,248));v.setTextSize(sp);v.setPadding(0,10,0,10);return v;}
    private void render(){
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(36,48,36,30);root.setBackgroundColor(Color.rgb(7,17,29));
        root.addView(text("S50 POLAR ALIGN",13));root.addView(text("Offline polar alignment",28));
        root.addView(text("Rotate the mount through three separated RA positions. Each solved frame traces the mount axis; the app then reports the altitude and azimuth correction.",16));
        status=text("Ready · offline solver · no AI",18);root.addView(status);
        Button demo=new Button(this);demo.setText("RUN SIMULATED 3-FRAME ALIGNMENT");demo.setOnClickListener(v->runDemo());root.addView(demo);
        root.addView(text("S50 live capture is deliberately adapter-based until a stable, documented JPEG endpoint is confirmed. Imported or configured snapshot frames never leave the phone.",14));
        setContentView(root);
    }
    private void runDemo(){
        points.clear(); double ce=.18,cn=-.12,r=2.0; long t=System.currentTimeMillis();
        for(double a:new double[]{-.8,.1,1.0}) points.add(new PolarAlignment.SkyPoint(ce+r*Math.cos(a),cn+r*Math.sin(a),t+=1000));
        PolarAlignment.Result q=new PolarAlignment().fit(points);
        status.setText(String.format(Locale.US,"Solved 3/3 frames\nAxis error %.2f° · confidence %.0f%%\n%s",q.totalErrorDeg(),100*q.confidence(),q.instruction()));
    }
}
