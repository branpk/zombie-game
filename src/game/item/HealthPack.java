package game.item;

import game.TextureManager;
import game.Vector;
import game.creature.Creature;
import game.creature.Player;

public class HealthPack extends Item {
	public HealthPack(Vector pos) {
		super(pos, TextureManager.HEALTHPACK);
		color = new float[] {1, 0, 0};
	}
	
	@Override
	public boolean pickedUpBy(Creature c) {
		if (c instanceof Player) {
			c.health += 1;
			return true;
		} else {
			return false;
		}
	}
}
