package game.item;

import game.TextureManager;
import game.Vector;
import game.creature.Creature;
import game.creature.Player;

public class Key extends Item {
	public Key(Vector pos) {
		super(pos, TextureManager.KEY);
		color = new float[] {1, 1, 0};
	}
	
	@Override
	public boolean pickedUpBy(Creature c) {
		try {
			Player p = (Player) c;
			p.hasKey = true;
			return true;
		} catch (ClassCastException e) {
			return false;
		}
	}
}
