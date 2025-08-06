package game.item;
import static org.lwjgl.opengl.GL11.*;
import game.Vector;
import game.creature.Creature;
import game.Texture;


public abstract class Item {
	public Vector pos;
	public float[] color;
	public Texture texture;
	public float radius = 0.25f;
	
	// TODO: require every item/creature to supply a texture and a color, in case the texture is null
	public Item(Vector pos, Texture texture) {
		this.pos = pos;
		this.texture = texture;
	}
	
	public Item(Vector pos, float[] color) {
		this.pos = pos;
		this.color = color;
	}
	
	public abstract boolean pickedUpBy(Creature c);
	
	public void render() {
		glPushMatrix();
		glTranslatef(pos.x, pos.y, 0);
		glScalef(radius, radius, 1);
		
		if (texture == null) {
			glColor3f(color[0], color[1], color[2]);
			
			glBegin(GL_TRIANGLE_FAN);
			glVertex2f(0, 0);
			for (float i = 0; i < 32; i += 1) {
				float a = (float) (i / 32 * 2*Math.PI);
				glVertex2f((float) Math.cos(a), (float) Math.sin(a));
			}
			glVertex2f(1, 0);
			glEnd();
		} else {
                        texture.bind();
                        glColor3f(1, 1, 1);
                        glBegin(GL_QUADS);
                        glTexCoord2f(0, 1);
                        glVertex2f(-1, -1);
                        glTexCoord2f(1, 1);
                        glVertex2f(1, -1);
                        glTexCoord2f(1, 0);
                        glVertex2f(1, 1);
                        glTexCoord2f(0, 0);
                        glVertex2f(-1, 1);
                        glEnd();
                        Texture.bindNone();
		}
		
		glPopMatrix();
	}
}
