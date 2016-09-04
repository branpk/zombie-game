package game.creature;
import game.TextureManager;
import game.Vector;
import game.level.AStarPathSearch;
import game.level.Level;

import java.util.List;


public class Zombie extends Creature {
	protected boolean detectedPlayer = false;
	protected float walkSpeed = 40;
	
	public Zombie() {
		super(3, TextureManager.ZOMBIE);
	}
	
	@Override
	public void update(float dt, Level level) {
		if (!detectedPlayer && !level.tileGrid.hitsWall(pos, level.player.pos) && level.player.pos.distance(pos) <= 10) {
			detectedPlayer = true;
		}
		
		if (detectedPlayer && level.player.health > 0) {
			int[] dest = level.tileGrid.getTile(level.player.pos);
			List<int[]> path = new AStarPathSearch(level.tileGrid, pos, dest).calculatePath();
			
			Vector target;
			if (path != null && path.size() > 1) {
				int[] targ1 = path.get(path.size() - 1);
				int[] targ2 = path.get(path.size() - 2);
				target = new Vector(targ1[1]+targ2[1], targ1[0]+targ2[0]).scale(0.5f);
			} else {
				target = level.player.pos;
			}
			
			Vector facing = new Vector(angle);
			int dir;
			if (target.sub(pos).dot(facing.perpCCW()) > 0) dir = 1;
			else dir = -1;
			
			float speed = 2*(1 - target.sub(pos).direction().dot(facing) + 1);
			angle += speed*dir*dt;//target.sub(pos).angle();
			vel = vel.add(new Vector(angle).scale(walkSpeed*dt));
		}
		
		super.update(dt, level);
	}
	
	protected void creatureUpdate(float dt, Level level) {
		super.update(dt, level);
	}
	
	@Override
	public void onTouch(Creature other) {
		super.onTouch(other);
		
		if (other instanceof Player) {
			other.takeHit(1, other.pos.sub(pos).direction());
		}
	}
}
