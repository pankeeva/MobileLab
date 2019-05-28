package com.example.organizer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;


public class Renderer implements GLSurfaceView.Renderer
{
	private float[] mModelMatrix = new float[16];
	private float[] mViewMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];

	private final FloatBuffer mVertices;
	private int mMVPMatrixHandle;
	private int mColorHandle;
	private int mPositionHandle;

	private final int mBytesPerFloat = 4;
	private final int mStrideBytes = 3 * mBytesPerFloat;
	private final int mPositionOffset = 0;
	private final int mPositionDataSize = 3;


	public Renderer()
	{
		final float[] mVerticesData = {
				// X, Y, Z,
				2,2,0,
				0,0,0,
				2,1,0,
				0,-1,0,
				0,-1,0,
				0,0,0,
				-2,0,0,
				-2,1,0
		};

		mVertices = ByteBuffer.allocateDirect(mVerticesData.length * mBytesPerFloat)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();

		mVertices.put(mVerticesData).position(0);
	}
	
	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) 
	{
		GLES20.glClearColor(0.0f, 0.1f, 0.1f, 1.0f);

		final float eyeX = 0.0f;
		final float eyeY = 0.0f;
		final float eyeZ = 6.0f;

		final float lookX = 0.0f;
		final float lookY = 0.0f;
		final float lookZ = -5.0f;

		final float upX = 0.0f;
		final float upY = 1.0f;
		final float upZ = 0.0f;

		Matrix.setLookAtM(mViewMatrix, 0, eyeX,
				eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

		final String vertexShader =
			"uniform mat4 u_MVPMatrix;      \n"
		  + "attribute vec4 a_Position;     \n"
		  + "void main()                    \n"
		  + "{                              \n"
		  + "   gl_Position = u_MVPMatrix   \n"
		  + "               * a_Position;   \n"
		  + "}                              \n";
		
		final String fragmentShader =
			"precision mediump float;       \n"
          + "uniform vec4 u_Color;          \n"
		  + "void main()                    \n"
		  + "{                              \n"
		  + "   gl_FragColor = u_Color;     \n"
		  + "}                              \n";												

		int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

		if (vertexShaderHandle != 0) 
		{
			GLES20.glShaderSource(vertexShaderHandle, vertexShader);
			GLES20.glCompileShader(vertexShaderHandle);

			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

			if (compileStatus[0] == 0) 
			{				
				GLES20.glDeleteShader(vertexShaderHandle);
				vertexShaderHandle = 0;
			}
		}

		if (vertexShaderHandle == 0)
		{
			throw new RuntimeException("Error creating vertex shader.");
		}

		int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

		if (fragmentShaderHandle != 0) 
		{
			GLES20.glShaderSource(fragmentShaderHandle, fragmentShader);
			GLES20.glCompileShader(fragmentShaderHandle);

			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

			if (compileStatus[0] == 0) 
			{				
				GLES20.glDeleteShader(fragmentShaderHandle);
				fragmentShaderHandle = 0;
			}
		}

		if (fragmentShaderHandle == 0)
		{
			throw new RuntimeException("Error creating fragment shader.");
		}

		int programHandle = GLES20.glCreateProgram();
		
		if (programHandle != 0) 
		{
			GLES20.glAttachShader(programHandle, vertexShaderHandle);
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);
			GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
			GLES20.glLinkProgram(programHandle);

			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

			if (linkStatus[0] == 0) 
			{				
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
			}
		}
		
		if (programHandle == 0)
		{
			throw new RuntimeException("Error creating program.");
		}

        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
		mColorHandle = GLES20.glGetUniformLocation(programHandle, "u_Color");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");

        GLES20.glUseProgram(programHandle);        
	}	
	
	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) 
	{
		GLES20.glViewport(0, 0, width, height);

		final float ratio = (float) width / height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 10.0f;
		
		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
	}	

	@Override
	public void onDrawFrame(GL10 glUnused) 
	{
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        Matrix.setIdentityM(mModelMatrix, 0);
		drawTriangleStrip(mVertices, 8);
	}	

	private void drawTriangleStrip(final FloatBuffer aTriangleBuffer, int num_vertices)
	{
		aTriangleBuffer.position(mPositionOffset);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
        		mStrideBytes, aTriangleBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);        

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glUniform4f(mColorHandle, 0.5f, 1.0f, 1.0f, 1.0f);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, num_vertices);

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
        GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
		GLES20.glUniform4f(mColorHandle, 0.8f, 0.8f, 0.8f, 1.0f);
		GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, num_vertices);
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
	}
}
