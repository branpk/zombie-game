package game;

/**
 * The original project included an in-game command prompt for debugging
 * purposes which relied heavily on LWJGL2's input system.  For the
 * modernised version the feature has been stripped down to a stub so the
 * rest of the game can compile and run without the Slick library.
 */
public class CommandPrompt {
    public boolean isOpen = false;
    private final Game game;

    public CommandPrompt(Game game) {
        this.game = game;
    }

    public void open() {
        isOpen = true;
        game.paused = true;
    }

    public void update() {
        // feature removed
    }

    public void render() {
        // feature removed
    }

    public void save() {
        // no-op
    }
}

