package game;

import static org.lwjgl.glfw.GLFW.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Simple input helper that tracks key presses using GLFW callbacks.  This
 * mimics a small portion of the old LWJGL2 Keyboard class used by the game.
 */
public class Input {
    private static final Set<Integer> pressed = new HashSet<>();
    private static long window;

    public static void init(long win) {
        window = win;
        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                pressed.add(key);
            }
        });
    }

    /** Poll GLFW events and reset the pressed set each frame. */
    public static void update() {
        pressed.clear();
        glfwPollEvents();
    }

    /** Returns true if the given key was pressed this frame. */
    public static boolean wasPressed(int key) {
        return pressed.contains(key);
    }

    /** Returns true if the given key is currently held down. */
    public static boolean isKeyDown(int key) {
        return glfwGetKey(window, key) == GLFW_PRESS;
    }
}

