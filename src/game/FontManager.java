package game;
import org.newdawn.slick.Font;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.TextureImpl;


public class FontManager {
	public static Font DISPLAY, COMMAND_PROMPT;
	
	public static void loadFonts() {
		DISPLAY = load("Arial", 20);
		COMMAND_PROMPT = load("Arial", 13);
		
		TextureImpl.bindNone();
		System.out.println("Loaded fonts.");
	}
	
	public static Font load(String fontname, int size) {
		java.awt.Font awtFont = new java.awt.Font(fontname, java.awt.Font.PLAIN, size);
		Font font = new TrueTypeFont(awtFont, true);
		return font;
	}
}
