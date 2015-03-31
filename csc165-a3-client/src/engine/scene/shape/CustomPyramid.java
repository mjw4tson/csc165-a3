package engine.scene.shape;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import engine.event.CrashEvent;
import sage.event.IEventListener;
import sage.event.IGameEvent;
import sage.scene.TriMesh;

/**
 * This class represents a Custom Pyramid object. 
 * @author Kevin
 *
 */
public class CustomPyramid extends TriMesh{

	private static float[]	vrts		= new float[] { -.4f, 1, -1, .4f, 1, -1, .4f, 1, 1, -.4f, 1, 1, -1, -1, 1, -1, -1, -1, 1, -1, -1, 1, -1, 1,
			.4f, 1, 1};
	private static float[]	cl			= new float[] { 0, .3f, .7f, 0, 0, .4f, .8f, 0, 0, .5f, .9f, 0, 0, .3f, .7f, 0, 0, .4f, .8f, 0, 0, .4f, .8f,
			0, 0, .4f, .8f, 0, 0, .4f, .8f, 0, 0, .4f, .8f, 0 };
	private static int[]	triangles	= new int[] { 0, 1, 2, 0, 2, 3, 0, 3, 4, 0, 4, 1, 1, 4, 2, 4, 3, 2 };


	FloatBuffer colorBuf;
	FloatBuffer colorBuf2;
	/**
	 * The constructor for the CustomPyramid, sets the vertices, triangles, and color.
	 */
	public CustomPyramid() {
		int i;

		FloatBuffer vertBuf = com.jogamp.common.nio.Buffers.newDirectFloatBuffer(vrts);
		IntBuffer triangleBuf = com.jogamp.common.nio.Buffers.newDirectIntBuffer(triangles);
		colorBuf = com.jogamp.common.nio.Buffers.newDirectFloatBuffer(cl);
		this.setVertexBuffer(vertBuf);
		this.setColorBuffer(colorBuf);
		this.setIndexBuffer(triangleBuf);
	}

}
