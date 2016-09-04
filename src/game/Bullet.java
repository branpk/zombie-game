package game;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;
import game.creature.Creature;
import game.creature.Turret;
import game.creature.TurretZombie;


public class Bullet {
	public Creature shooter;
	public Vector pos, dir;
	public float speed = 18;
	
	public Bullet(Creature shooter, Vector pos, Vector dir) {
		this.shooter = shooter;
		this.pos = pos;
		this.dir = dir.direction();
	}
	
	public void update(float dt) {
		pos = pos.add(dir.scale(speed * dt));
	}
	
	public boolean collide(Creature c) {
		if (!c.solid) return false;
		
		if (c.pos.distance(pos) <= c.hitRadius) {
			if (shooter instanceof Turret) {
				if (c instanceof Turret || c instanceof TurretZombie) return true;
			}
			int damage = shooter.health > 0 ? 1 : 0;
			c.takeHit(damage, dir);
			return true;
		}
		return false;
	}
	
	public void render() {
		float radius = 0.07f;
		
		glPushMatrix();
		glTranslatef(pos.x, pos.y, 0);
		glScalef(radius, radius, 1);
		
		glColor3f(0, 0, 0);
		
		glBegin(GL_TRIANGLE_FAN);
		glVertex2f(0, 0);
		for (float i = 0; i < 32; i += 1) {
			float a = (float) (i / 32 * 2*Math.PI);
			glVertex2f((float) Math.cos(a), (float) Math.sin(a));
		}
		glVertex2f(1, 0);
		glEnd();
		
		glPopMatrix();
	}
}
