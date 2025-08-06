package game.creature;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;
import game.DebugMode;
import game.Vector;
import game.level.Level;
import game.Input;
import game.Texture;

public class Creature {
	public Vector pos, vel = Vector.zeroVector;
	public float angle;
	
	public float drawRadius = 0.5f;
	public float hitRadius = 0.4f;
	public float mass = 1;
	
	public int health;
	public float invincible = -0.01f;
	public boolean permInvincible = false;
	public boolean solid = true;
	
	public float[] color = null;
	public Texture texture = null;
	
	
	public Creature(int health, Texture texture) {
		this.health = health;
		this.texture = texture;
	}
	
	public Creature(int health, float[] color) {
		this.health = health;
		this.color = color;
	}
	
	
	public void update(float dt, Level level) {
		vel = vel.add(vel.scale(-20*dt));
		
		pos = pos.add(vel.scale(dt));
		
		while (angle >= 2*Math.PI) angle -= 2*Math.PI;
		while (angle <= -2*Math.PI) angle += 2*Math.PI;
		
		if (invincible >= 0)
			invincible -= dt;
		if (permInvincible) invincible = 0.01f;
	}
	
	public void collide(Creature other) {
		if (!solid || !other.solid) return; 
		
		float dist = (other.pos.sub(this.pos)).magnitude();
		float depth = other.hitRadius + this.hitRadius - dist;
		if (depth >= 0) {
			Vector normal = other.pos.sub(this.pos).direction();
			Vector relVel = this.vel.sub(other.vel);
			float relVelR = relVel.dot(normal);
			
			float m1 = this.mass;
			float m2 = other.mass;
			
			float impact = 1/(1/m1 + 1/m2) * relVelR;
			this.vel = this.vel.add(normal.scale(-impact/m1));
			other.vel = other.vel.add(normal.scale(impact/m2));
			
			float disp = 1/(1/m1 + 1/m2) * depth;
			this.pos = this.pos.add(normal.scale(-disp/m1));
			other.pos = other.pos.add(normal.scale(disp/m2));
			
			this.onTouch(other);
			other.onTouch(this);
		}
	}
	
	public void onTouch(Creature other) {}
	
	public void takeHit(int damage, Vector dir) {
		if (invincible > 0) return;
		
		vel = vel.add(dir.direction().scale(1/mass * 20));
		
		if (damage <= 0) return;
		
		health -= damage;
		invincible = 0.7f;
	}
	
	public void render() {
		glPushMatrix();
		glTranslatef(pos.x, pos.y, 0);
		glRotatef((float) (angle*180/Math.PI), 0, 0, 1);
		glScalef(drawRadius, drawRadius, 1);
		
		if (texture == null) {
			if (invincible > 0)
				glColor4f(color[0], color[1], color[2], 0.5f);
			else
				glColor3f(color[0], color[1], color[2]);
			
			glBegin(GL_TRIANGLE_FAN);
			glVertex2f(0, 0);
			for (float i = 0; i < 32; i += 1) {
				float a = (float) (i / 32 * 2*Math.PI);
				glVertex2f((float) Math.cos(a), (float) Math.sin(a));
			}
			glVertex2f(1, 0);
			glEnd();
			
			glColor3f(0, 0, 0);
			glBegin(GL_LINES);
			glVertex2f(0, 0);
			glVertex2f(1, 0);
			glEnd();
                } else {
                        texture.bind();
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
                }
		
		glPopMatrix();
		
                if (DebugMode.enabled && Input.isKeyDown(GLFW_KEY_6)) {
			glPushMatrix();
			glTranslatef(pos.x, pos.y, 0);
			glRotatef((float) (angle*180/Math.PI), 0, 0, 1);
			glScalef(hitRadius, hitRadius, 1);
			
			glColor3f(1, 1, 1);
			glBegin(GL_LINE_STRIP);
			for (float i = 0; i < 32; i += 1) {
				float a = (float) (i / 32 * 2*Math.PI);
				glVertex2f((float) Math.cos(a), (float) Math.sin(a));
			}
			glVertex2f(1, 0);
			glEnd();
			glPopMatrix();
		}
	}
}
