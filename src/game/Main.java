package game;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.Font;
import org.newdawn.slick.opengl.TextureImpl;

import static org.lwjgl.opengl.GL11.*;


public class Main {
	private static String[] levelOrder = {"1", "2", "3", "b"};
	private static Game game;
	private static CommandPrompt prompt;
	
	private static boolean instructions = true;
	private static Timer instTimer;
	
	
	private static void init() {
		try {
			Display.setTitle("Not McCrario");
			Display.setDisplayMode(new DisplayMode(600, 500));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), 0, Display.getHeight(), -1, 1);
		glMatrixMode(GL_MODELVIEW);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		TextureManager.loadTextures();
		FontManager.loadFonts();
		
		game = new Game(levelOrder);
		if (DebugMode.enabled) prompt = new CommandPrompt(game);
		
		instTimer = new Timer();
	}
	
	public static void main(String[] args) {
		init();
		
		while (!Display.isCloseRequested()) {
			update();
			render();
			
			if (game.level == null) break;
		}
		
		game.closeLevel();
		
		Display.destroy();
	}
	
	private static void update() {
		if (prompt != null && !prompt.isOpen && Input.wasPressed(Keyboard.KEY_ESCAPE)) {
			prompt.open();
		}
		
		if (prompt != null && prompt.isOpen) {
			prompt.update();
		}
		
		if (prompt == null || !prompt.isOpen) {
			Input.update();
		}
		
		game.update();
	}

	private static void render() {
		glClear(GL_COLOR_BUFFER_BIT);
		
		game.render();
		
		if (instructions) {
			glLoadIdentity();
			glTranslatef(Display.getWidth()/2, Display.getHeight()/2, 0);
			
			Font f = FontManager.DISPLAY;
			
			String msg0 = "ASDW to move.";
			String msg1 = "Left click to shoot.";
			String msg2 = "Right click to stab.";
			String msg3 = "Good luck.";
			
			if (instTimer.elapsed().inSecs() > 1) {
				glPushMatrix();
				glTranslatef(-f.getWidth(msg0)/2, f.getLineHeight()*2, 0);
				glScalef(1, -1, 1);
				f.drawString(0, 0, msg0);
				glPopMatrix();
			}
			
			if (instTimer.elapsed().inSecs() > 2) {
				glPushMatrix();
				glTranslatef(-f.getWidth(msg1)/2, f.getLineHeight()*1, 0);
				glScalef(1, -1, 1);
				f.drawString(0, 0, msg1);
				glPopMatrix();
			}
			
			if (instTimer.elapsed().inSecs() > 3) {
				glPushMatrix();
				glTranslatef(-f.getWidth(msg2)/2, f.getLineHeight()*0, 0);
				glScalef(1, -1, 1);
				f.drawString(0, 0, msg2);
				glPopMatrix();
			}
			
			if (instTimer.elapsed().inSecs() > 4) {
				glPushMatrix();
				glTranslatef(-f.getWidth(msg3)/2, f.getLineHeight()*-1, 0);
				glScalef(1, -1, 1);
				f.drawString(0, 0, msg3);
				glPopMatrix();
			}
			
			TextureImpl.bindNone();
			if (instTimer.elapsed().inSecs() > 5) instructions = false;
		}
		
		if (prompt != null && prompt.isOpen) {
			glLoadIdentity();
			prompt.render();
		}
		
		Display.update();
	}
}
