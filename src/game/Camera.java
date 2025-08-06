package game;

import static org.lwjgl.opengl.GL11.*;

public class Camera {
	public static Vector focus = Vector.zeroVector;
	public static float pixelsPerUnit = 1;
	
	public static void lookThrough() {
		glLoadIdentity();
                glTranslatef(Window.width/2, Window.height/2, 0);
		glScalef(pixelsPerUnit, pixelsPerUnit, 1);
		glTranslatef(-focus.x, -focus.y, 0);
	}
	
	public static Vector screenToWorld(int sx, int sy) {
                sx -= Window.width/2;
                sy -= Window.height/2;
		Vector pos = new Vector(sx, sy).scale(1/pixelsPerUnit);
		return pos.add(focus);
	}
	
	public static Vector getLowerLeft() {
                float left = focus.x - (Window.width/2)/pixelsPerUnit;
                float down = focus.y - (Window.height/2)/pixelsPerUnit;
		return new Vector(left, down);
	}
	
	public static Vector getUpperRight() {
                float right = focus.x + (Window.width/2)/pixelsPerUnit;
                float up = focus.y + (Window.height/2)/pixelsPerUnit;
		return new Vector(right, up);
	}
}
