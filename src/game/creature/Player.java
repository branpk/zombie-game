package game.creature;
import game.Bullet;
import game.Camera;
import game.Controls;
import game.DebugMode;
import game.TextureManager;
import game.Vector;
import game.level.Level;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import static org.lwjgl.opengl.GL11.*;


public class Player extends Creature {
	public boolean hasKey = false;
	public int ammo = 10;

	private boolean mouseWasDown = true;
	private boolean mouseRWasDown = true;
	
	private boolean knifeSwinging = false;
	private float knifeSwingT;
	private float knifeSwingPos;
	
	public Player() {
		super(10, TextureManager.PLAYER);
	}
	
	@Override
	public void update(float dt, Level level) {
		Vector mousePos = Camera.screenToWorld(Mouse.getX(), Mouse.getY());
		angle = mousePos.sub(pos).angle();
		Vector facing = new Vector(angle);
		
		Vector moveDir = new Vector(0, 0);
		if (Keyboard.isKeyDown(Controls.left)) 
			moveDir = moveDir.add(new Vector(-1, 0));
		if (Keyboard.isKeyDown(Controls.right)) 
			moveDir = moveDir.add(new Vector(1, 0));
		if (Keyboard.isKeyDown(Controls.down)) 
			moveDir = moveDir.add(new Vector(0, -1));
		if (Keyboard.isKeyDown(Controls.up)) 
			moveDir = moveDir.add(new Vector(0, 1));
		moveDir = moveDir.direction();
		float speed = 100 * (moveDir.dot(facing) + 3)/4;
		if (DebugMode.enabled && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) speed *= 3;
		vel = vel.add(moveDir.scale(speed*dt));
		
		if (Mouse.isButtonDown(0) && !mouseWasDown && ammo > 0) {
			Vector bpos = pos.add(facing.scale(hitRadius + 0.1f));
			level.bullets.add(new Bullet(this, bpos, facing));
			ammo -= 1;
		}
		mouseWasDown = Mouse.isButtonDown(0);
		
		if (Mouse.isButtonDown(1) && !mouseRWasDown && !knifeSwinging) {
			knifeSwinging = true;
			knifeSwingT = 0;
		}
		mouseRWasDown = Mouse.isButtonDown(1);
		if (knifeSwinging) {
			if (knifeSwingT >= 1) knifeSwinging = false;
			
			knifeSwingPos = 0.7f*(1 - 2*Math.abs(knifeSwingT - 0.5f));
			Vector knifePos = pos.add(facing.scale(hitRadius + knifeSwingPos));
			
			for (Creature c : level.creatures) {
				if (c != this && c.pos.distance(knifePos) <= c.hitRadius) {
					c.takeHit(1, facing);
				}
			}

			knifeSwingT += dt/0.3f;
		}
		
		super.update(dt, level);
	}
	
	@Override
	public void render() {
		if (knifeSwinging) {
			glPushMatrix();
			glTranslatef(pos.x, pos.y, 0);
			glRotatef((float) (angle*180/Math.PI), 0, 0, 1);
			glTranslatef(hitRadius, 0, 0);
			
			float col = 0.2f;
			glColor3f(col, col, col);
			glBegin(GL_TRIANGLES);
			glVertex2f(-0.2f, -0.05f);
			glVertex2f(-0.2f, 0.05f);
			glVertex2f(knifeSwingPos, 0);
			glEnd();
			
			glPopMatrix();
		}
		
		super.render();
	}
}
