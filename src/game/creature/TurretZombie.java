package game.creature;
import game.TextureManager;
import game.Vector;
import game.level.Level;

import java.util.ArrayList;
import java.util.List;


public class TurretZombie extends Zombie {
	public Turret head = new Turret();
	float run = -1;
	Vector runDir;
	float lastRun = 0;
	
	boolean spawnReq = false;
	Vector spawnPos;
	
	List<Creature> spawns = new ArrayList<>();
	
	
	public TurretZombie() {
		drawRadius *= 2;
		hitRadius = drawRadius - 0.1f;
		head.drawRadius = 0.45f * drawRadius;
		head.hitRadius = hitRadius;
		head.bulletSpeed = 18;
		texture = TextureManager.ZOMBIEBODY;
		walkSpeed = 60;
		mass = 100;
		health = 10;
	}
	
	@Override
	public void update(float dt, Level level) {
		head.pos = pos;
		head.invincible = invincible;
		if (level.tileGrid.hitsWall(pos, level.player.pos) || run > 0)
			head.angle = angle;

		head.update(dt, level);

		
		if (run > 0) {
			lastRun = (float) Math.random() * 5 + 1;
			
			Vector facing = new Vector(angle);
			int dir;
			if (runDir.dot(facing.perpCCW()) > 0) dir = 1;
			else dir = -1;
			
			angle += 3*dir*dt;
			if (run < 1)
				vel = vel.add(new Vector(angle).scale(walkSpeed*3*dt));
			
			run -= dt;
			
			creatureUpdate(dt, level);
		} else {
			super.update(dt, level);
		}
		
		if (spawnReq && spawnPos.distance(pos) > hitRadius) {
			spawnReq = false;
			Creature c;
			if (health <= 3) {
				c = new Turret();
			} else {
				c = new Zombie();
			}
			c.pos = spawnPos;
			c.angle = (float) (Math.random() * 2*Math.PI);
			level.spawnReqs.add(c);
			spawns.add(c);
		}
		
		lastRun -= dt;
		if (lastRun < 0 && detectedPlayer) {
			run = 1.5f;
			runDir = level.player.pos.sub(pos).direction();
		}
	}
	
	@Override
	public void takeHit(int damage, Vector dir) {
		int oldHealth = health;
		super.takeHit(damage, dir);
		if (oldHealth == health) return;
		
		if (health == 0) {
			for (Creature c : spawns) {
				c.health = 0;
			}
			return;
		}
		
		run = 1.5f;
		runDir = dir.negate().direction();
		
		spawnReq = true;
		spawnPos = pos;
		
		invincible = run;
	}
	
	@Override
	public void render() {
		head.pos = pos;
		head.invincible = invincible;
		
		super.render();
		head.render();
	}
}
