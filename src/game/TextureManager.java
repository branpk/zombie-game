package game;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;


public class TextureManager {
	public static Texture 
		PLAYER, ZOMBIE, TURRET, ZOMBIEBODY, TURRETBODY, ZOMBIEHEAD,
		HEALTHPACK, AMMOPACK, KEY, DOOR;
	
	public static void loadTextures() {
		PLAYER = load("player.png", "PNG");
		ZOMBIE = load("zombie.png", "PNG");
		TURRET = load("turret.png", "PNG");
		ZOMBIEBODY = load("zombiebody.png", "PNG");
		TURRETBODY = load("turretbody.png", "PNG");
		ZOMBIEHEAD = load("zombiehead.png", "PNG");
		
		HEALTHPACK = load("healthpack.png", "PNG");
		AMMOPACK = load("ammopack.png", "PNG");
		KEY = load("key.png", "PNG");
		DOOR = load("door.png", "PNG");

		System.out.println("Loaded textures.");
	}
	
	private static Texture load(String name, String format) {
		try {
			String file = "res/textures/" + name;
			return TextureLoader.getTexture(format, ResourceLoader.getResourceAsStream(file));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
