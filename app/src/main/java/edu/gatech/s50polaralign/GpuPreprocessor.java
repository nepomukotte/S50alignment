package edu.gatech.s50polaralign;

import android.opengl.GLES31;

/** Optional GLES 3.1 compute stage. Detection/solving always has a CPU fallback. No ML is used. */
public final class GpuPreprocessor {
    public static final String SHADER = "#version 310 es\n"+
            "layout(local_size_x=16,local_size_y=16) in;\n"+
            "layout(rgba8,binding=0) readonly uniform highp image2D src;\n"+
            "layout(r16f,binding=1) writeonly uniform highp image2D dst;\n"+
            "void main(){ivec2 p=ivec2(gl_GlobalInvocationID.xy);vec3 c=imageLoad(src,p).rgb;"+
            "imageStore(dst,p,vec4(dot(c,vec3(.2126,.7152,.0722))));}";

    public boolean available() {
        String version=GLES31.glGetString(GLES31.GL_VERSION);
        return version != null && version.contains("OpenGL ES 3.");
    }
}
