package game.creature;
import static org.lwjgl.opengl.GL11.*;
import game.TextureManager;
import game.Texture;
import game.Vector;
import game.level.Level;


public class ZombieTurret extends Creature {
	boolean detectedPlayer = false;
	float headAngle;
	
	public ZombieTurret() {
		super(10, TextureManager.TURRETBODY);
		drawRadius *= 2;
		hitRadius = drawRadius - 0.1f;
		mass = 100;
	}

	@Override
	public void update(float dt, Level level) {
		if (!detectedPlayer && !level.tileGrid.hitsWall(pos, level.player.pos) && level.player.pos.distance(pos) <= 10) {
			detectedPlayer = true;
		}
		if (detectedPlayer) {
			Vector target = level.player.pos;
			
			Vector facing = new Vector(headAngle);
			int dir;
			if (target.sub(pos).dot(facing.perpCCW()) > 0) dir = 1;
			else dir = -1;
			
			float speed = 2*(1 - target.sub(pos).direction().dot(facing) + 1);
			headAngle += speed*dir*dt;
			
		} else {
			headAngle = angle;
		}
		
		super.update(dt, level);
	}
	
	@Override
	public void render() {
		super.render();
		
		glPushMatrix();
		glTranslatef(pos.x, pos.y, 0);
		glRotatef((float) (headAngle*180/Math.PI), 0, 0, 1);
		float headRadius = 0.5f;
		glScalef(headRadius, headRadius, 1);
		
		TextureManager.ZOMBIEHEAD.bind();
		glColor4f(1, 1, 1, invincible > 0 ? 0.5f : 1);
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
		
		glPopMatrix();
	}
}
