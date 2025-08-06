package game;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

/**
 * Application entry point.  Ported to LWJGL3 using GLFW for window creation
 * and input handling.
 */
public class Main {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 500;

    private static String[] levelOrder = {"1", "2", "3", "b"};
    private static Game game;

    private static boolean instructions = true;
    private static Timer instTimer;

    private static void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        Window.width = WIDTH;
        Window.height = HEIGHT;
        Window.handle = glfwCreateWindow(WIDTH, HEIGHT, "Not McCrario", 0, 0);
        if (Window.handle == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create GLFW window");
        }

        glfwMakeContextCurrent(Window.handle);
        glfwSwapInterval(1);
        GL.createCapabilities();

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, Window.width, 0, Window.height, -1, 1);
        glMatrixMode(GL_MODELVIEW);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        TextureManager.loadTextures();
        FontManager.loadFonts();

        Input.init(Window.handle);
        Mouse.init(Window.handle);

        game = new Game(levelOrder);
        instTimer = new Timer();
    }

    public static void main(String[] args) {
        init();

        while (!glfwWindowShouldClose(Window.handle)) {
            update();
            render();
            if (game.level == null) break;
        }

        game.closeLevel();

        glfwDestroyWindow(Window.handle);
        glfwTerminate();
    }

    private static void update() {
        Input.update();
        game.update();
    }

    private static void render() {
        glClear(GL_COLOR_BUFFER_BIT);

        game.render();

        if (instructions) {
            glLoadIdentity();
            glTranslatef(Window.width/2f, Window.height/2f, 0);

            Font f = FontManager.DISPLAY;

            String msg0 = "ASDW to move.";
            String msg1 = "Left click to shoot.";
            String msg2 = "Right click to stab.";
            String msg3 = "Good luck.";

            if (instTimer.elapsed().inSecs() > 1) {
                glPushMatrix();
                glTranslatef(-f.getWidth(msg0)/2f, f.getLineHeight()*2f, 0);
                glScalef(1, -1, 1);
                f.drawString(0, 0, msg0);
                glPopMatrix();
            }
            if (instTimer.elapsed().inSecs() > 2) {
                glPushMatrix();
                glTranslatef(-f.getWidth(msg1)/2f, f.getLineHeight()*1f, 0);
                glScalef(1, -1, 1);
                f.drawString(0, 0, msg1);
                glPopMatrix();
            }
            if (instTimer.elapsed().inSecs() > 3) {
                glPushMatrix();
                glTranslatef(-f.getWidth(msg2)/2f, 0, 0);
                glScalef(1, -1, 1);
                f.drawString(0, 0, msg2);
                glPopMatrix();
            }
            if (instTimer.elapsed().inSecs() > 4) {
                glPushMatrix();
                glTranslatef(-f.getWidth(msg3)/2f, -f.getLineHeight(), 0);
                glScalef(1, -1, 1);
                f.drawString(0, 0, msg3);
                glPopMatrix();
            }

            Texture.bindNone();
            if (instTimer.elapsed().inSecs() > 5) instructions = false;
        }

        glfwSwapBuffers(Window.handle);
    }
}

