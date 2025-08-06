package game;

import static org.lwjgl.opengl.GL11.*;

/**
 * Simple OpenGL texture wrapper used to replace the old Slick texture
 * classes.  Textures are created by {@link TextureManager} and can be
 * bound for rendering via {@link #bind()}.
 */
public class Texture {
    private final int id;
    public final int width;
    public final int height;

    public Texture(int id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    /** Bind this texture to the current OpenGL context. */
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    /** Unbind any bound texture. */
    public static void bindNone() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}

