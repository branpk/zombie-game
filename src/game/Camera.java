package game;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.Display;

public class Camera {
	public static Vector focus = Vector.zeroVector;
	public static float pixelsPerUnit = 1;
	
	public static void lookThrough() {
		glLoadIdentity();
		glTranslatef(Display.getWidth()/2, Display.getHeight()/2, 0);
		glScalef(pixelsPerUnit, pixelsPerUnit, 1);
		glTranslatef(-focus.x, -focus.y, 0);
	}
	
	public static Vector screenToWorld(int sx, int sy) {
		sx -= Display.getWidth()/2;
		sy -= Display.getHeight()/2;
		Vector pos = new Vector(sx, sy).scale(1/pixelsPerUnit);
		return pos.add(focus);
	}
	
	public static Vector getLowerLeft() {
		float left = focus.x - (Display.getWidth()/2)/pixelsPerUnit;
		float down = focus.y - (Display.getHeight()/2)/pixelsPerUnit;
		return new Vector(left, down);
	}
	
	public static Vector getUpperRight() {
		float right = focus.x + (Display.getWidth()/2)/pixelsPerUnit;
		float up = focus.y + (Display.getHeight()/2)/pixelsPerUnit;
		return new Vector(right, up);
	}
}
