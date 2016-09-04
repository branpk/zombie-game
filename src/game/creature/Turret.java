package game.creature;
import game.Bullet;
import game.TextureManager;
import game.Vector;
import game.level.Level;


public class Turret extends Creature {
	private boolean detectedPlayer = false;
	private float shootTimer = -0.1f;
	public float bulletSpeed = 12;
	
	public Turret() {
		super(3, TextureManager.TURRET);
		mass = 10;
		hitRadius = 0.5f;
	}
	
	@Override
	public void update(float dt, Level level) {
		if (!detectedPlayer && !level.tileGrid.hitsWall(pos, level.player.pos) && level.player.pos.distance(pos) <= 10) {
			detectedPlayer = true;
		}
		
		if (detectedPlayer && level.player.health > 0 && !level.tileGrid.hitsWall(pos, level.player.pos)) {
			Vector facing = new Vector(angle);
			Vector targFace = level.player.pos.sub(pos).direction();
			int dir;
			if (targFace.dot(facing.perpCCW()) > 0) dir = 1;
			else dir = -1;
			
			float speed = 2*(1 - targFace.dot(facing) + 1);
			angle += speed*dir*dt;
			
			if (shootTimer <= 0 && targFace.dot(facing) > 0.8f) {
				facing = new Vector(angle);
				Vector bpos = pos.add(facing.scale(hitRadius + 0.1f));
				Bullet bullet = new Bullet(this, bpos, facing);
				bullet.speed = bulletSpeed;
				level.bullets.add(bullet);
				shootTimer = 1;
			}
		}
		
		if (shootTimer > 0) shootTimer -= dt;
		
		super.update(dt, level);
	}
}
