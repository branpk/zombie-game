package game;

/**
 * Loads fonts used throughout the game.  The original code relied on the
 * Slick library; this version uses a minimal AWT backed implementation.
 */
public class FontManager {
    public static Font DISPLAY, COMMAND_PROMPT;

    public static void loadFonts() {
        DISPLAY = new Font("Arial", 20);
        COMMAND_PROMPT = new Font("Arial", 13);
        System.out.println("Loaded fonts.");
    }
}

