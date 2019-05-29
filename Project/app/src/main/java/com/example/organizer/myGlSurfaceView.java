package com.example.organizer;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class myGlSurfaceView extends GLSurfaceView {

    com.example.organizer.Renderer myRender;

    public myGlSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);

        super.setEGLConfigChooser(8, 8,
                8, 8, 16, 0);
        myRender = new com.example.organizer.Renderer();
        setRenderer(myRender);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}
