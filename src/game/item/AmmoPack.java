package game.item;

import game.TextureManager;
import game.Vector;
import game.creature.Creature;
import game.creature.Player;

public class AmmoPack extends Item {
	public AmmoPack(Vector pos) {
		super(pos, TextureManager.AMMOPACK);
		color = new float[] {0.3f, 0.3f, 1};
	}
	
	@Override
	public boolean pickedUpBy(Creature c) {
		try {
			Player p = (Player) c;
			p.ammo += 5;
			return true;
		} catch (ClassCastException e) {
			return false;
		}
	}
}
