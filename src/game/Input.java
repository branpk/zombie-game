package game;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.input.Keyboard;


public class Input {
	private static Set<Integer> keysPressed = new HashSet<>();
	
	public static void update() {
		keysPressed.clear();
		
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState() == true) {
				keysPressed.add(Keyboard.getEventKey());
			}
		}
	}
	
	public static boolean wasPressed(int key) {
		return keysPressed.contains(key);
	}
}
