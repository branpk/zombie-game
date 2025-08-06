package game;

import static org.lwjgl.glfw.GLFW.*;

/** Maps game actions to GLFW key codes. */
public class Controls {
        public static final int
                left = GLFW_KEY_A,
                right = GLFW_KEY_S,
                down = GLFW_KEY_R,
                up = GLFW_KEY_W;

        /* Alternate scheme:
        public static final int
                left = GLFW_KEY_A,
                right = GLFW_KEY_D,
                down = GLFW_KEY_S,
                up = GLFW_KEY_W;*/
}
