package game;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

/**
 * Loads textures from the resources directory using the standard Java
 * ImageIO APIs.  This replaces the Slick texture loader used previously.
 */
public class TextureManager {
    public static Texture
            PLAYER, ZOMBIE, TURRET, ZOMBIEBODY, TURRETBODY, ZOMBIEHEAD,
            HEALTHPACK, AMMOPACK, KEY, DOOR;

    public static void loadTextures() {
        PLAYER = load("player.png");
        ZOMBIE = load("zombie.png");
        TURRET = load("turret.png");
        ZOMBIEBODY = load("zombiebody.png");
        TURRETBODY = load("turretbody.png");
        ZOMBIEHEAD = load("zombiehead.png");

        HEALTHPACK = load("healthpack.png");
        AMMOPACK = load("ammopack.png");
        KEY = load("key.png");
        DOOR = load("door.png");

        System.out.println("Loaded textures.");
    }

    private static Texture load(String name) {
        try {
            String file = "res/textures/" + name;
            BufferedImage image = ImageIO.read(new File(file));
            int width = image.getWidth();
            int height = image.getHeight();

            int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
            ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = pixels[y * width + x];
                    buffer.put((byte) ((pixel >> 16) & 0xFF));
                    buffer.put((byte) ((pixel >> 8) & 0xFF));
                    buffer.put((byte) (pixel & 0xFF));
                    buffer.put((byte) ((pixel >> 24) & 0xFF));
                }
            }
            buffer.flip();

            int id = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, id);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
            glBindTexture(GL_TEXTURE_2D, 0);

            return new Texture(id, width, height);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

