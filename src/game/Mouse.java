package game;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Replacement for LWJGL2's Mouse class using GLFW.
 */
public class Mouse {
    private static long window;
    private static double scroll;

    public static void init(long win) {
        window = win;
        glfwSetScrollCallback(window, (w, xoffset, yoffset) -> {
            scroll += yoffset;
        });
    }

    public static int getX() {
        double[] x = new double[1];
        double[] y = new double[1];
        glfwGetCursorPos(window, x, y);
        return (int) x[0];
    }

    public static int getY() {
        double[] x = new double[1];
        double[] y = new double[1];
        glfwGetCursorPos(window, x, y);
        return (int) y[0];
    }

    public static boolean isButtonDown(int button) {
        return glfwGetMouseButton(window, button) == GLFW_PRESS;
    }

    /**
     * Return the amount scrolled since the last call.  Values are similar to
     * the old getDWheel() behaviour.
     */
    public static int getDWheel() {
        int s = (int) scroll;
        scroll = 0;
        return s;
    }
}

