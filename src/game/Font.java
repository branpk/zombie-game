package game;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

/**
 * Minimal bitmap font renderer backed by AWT.  This replaces the Slick
 * TrueTypeFont used in the original code.  It is not very efficient but is
 * sufficient for the simple text used by the game.
 */
public class Font {
    private final java.awt.Font awtFont;
    private final FontMetrics metrics;

    public Font(String name, int size) {
        awtFont = new java.awt.Font(name, java.awt.Font.PLAIN, size);
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setFont(awtFont);
        metrics = g.getFontMetrics();
        g.dispose();
    }

    public int getWidth(String text) {
        return metrics.stringWidth(text);
    }

    public int getLineHeight() {
        return metrics.getHeight();
    }

    /**
     * Draw the provided string at the given position in screen coordinates.
     */
    public void drawString(float x, float y, String text) {
        int width = getWidth(text);
        int height = getLineHeight();

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setFont(awtFont);
        g.setColor(Color.WHITE);
        g.drawString(text, 0, metrics.getAscent());
        g.dispose();

        int[] pixels = img.getRGB(0, 0, width, height, null, 0, width);
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
        for (int py = 0; py < height; py++) {
            for (int px = 0; px < width; px++) {
                int pixel = pixels[py * width + px];
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        buffer.flip();

        int texId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(x, y);
        glTexCoord2f(1, 0);
        glVertex2f(x + width, y);
        glTexCoord2f(1, 1);
        glVertex2f(x + width, y + height);
        glTexCoord2f(0, 1);
        glVertex2f(x, y + height);
        glEnd();

        glBindTexture(GL_TEXTURE_2D, 0);
        glDeleteTextures(texId);
    }
}

