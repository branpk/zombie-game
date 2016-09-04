package game;
import static org.lwjgl.opengl.GL11.*;
import game.level.Level;
import game.level.LevelEditor;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.opengl.TextureImpl;


public class Game {
	public Level level = null;
	public LevelEditor levelEditor = null;
	
	public boolean paused = false;
	
	private Queue<String> levelOrder;
	private Timer frameTimer = new Timer();
	
	
	public Game(String[] levels) {
		levelOrder = new LinkedList<>();
		for (String levelName : levels) {
			levelOrder.add(levelName);
		}
		loadLevel(levelOrder.remove());
	}
	
	public void loadLevel(String levelName) {
		closeLevel();
		try {
			openLevel(new Level(levelName));
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
			System.out.println("Failed to load level " + levelName);
			return;
		}
	}
	
	public void openLevel(Level level) {
		closeLevel();
		
		this.level = level;
		
		frameTimer.restart();
		
		Camera.focus = level.player.pos;
		Camera.pixelsPerUnit = 40;
	}
	
	public void closeLevel() {
		closeEditor(true);
		level = null;
	}
	
	public void openEditor() {
		if (level != null)
			levelEditor = new LevelEditor(level);
	}
	
	public void closeEditor(boolean save) {
		if (levelEditor != null) {
			if (save) levelEditor.save();
			levelEditor = null;
		}
	}
	
	public void update() {
		if (level == null) return;
		
		float dt = (float) frameTimer.tick().inSecs();
		
		if (paused) return;
		
		level.update(dt);
		if (level.didWin) {
			if (levelOrder.isEmpty()) {
				System.out.println("Beat game!");
				closeLevel();
			} else {
				loadLevel(levelOrder.remove());
			}
			return;
		}
		if (level.didDie) {
			loadLevel(level.levelName);
			return;
		}
		
		Camera.focus = level.player.pos;
		
		if (DebugMode.enabled) { 
			Camera.pixelsPerUnit += 0.01f*Mouse.getDWheel();
			
			if (levelEditor != null) {
				levelEditor.update();
			}
			if (Input.wasPressed(Keyboard.KEY_D)) {
				level.player.permInvincible = !level.player.permInvincible;
			}
			if (Input.wasPressed(Keyboard.KEY_TAB)) {
				level.player.solid = !level.player.solid;
			}
		}
	}
	
	public void render() {
		if (level == null) return;
		
		Camera.lookThrough();
		
		level.render();
		
		if (levelEditor != null) {
			levelEditor.render();
		}
		
		if (level.player.hasKey) {
			glLoadIdentity();
			glTranslatef(0, Display.getHeight(), 0);
			int numPixels = 40;
			glScalef(numPixels, numPixels, 1);
			
			glColor3f(1, 1, 1);
			TextureManager.KEY.bind();
			glBegin(GL_QUADS);
			glTexCoord2f(0, 1);
			glVertex2f(0, -1);
			glTexCoord2f(1, 1);
			glVertex2f(1, -1);
			glTexCoord2f(1, 0);
			glVertex2f(1, 0);
			glTexCoord2f(0, 0);
			glVertex2f(0, 0);
			glEnd();
		}
		
		glLoadIdentity();
		glTranslatef(0, FontManager.DISPLAY.getLineHeight(), 0);
		glScalef(1, -1, 1);
		FontManager.DISPLAY.drawString(0, 0, "Health: " + level.player.health);
		TextureImpl.bindNone();
		
		glLoadIdentity();
		String message = "Ammo: " + level.player.ammo;
		glTranslatef(Display.getWidth() - FontManager.DISPLAY.getWidth(message), FontManager.DISPLAY.getLineHeight(), 0);
		glScalef(1, -1, 1);
		FontManager.DISPLAY.drawString(0, 0, message);
		TextureImpl.bindNone();
	}
}
