package game;

import static org.lwjgl.opengl.GL11.*;
import game.level.Level;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Font;
import org.newdawn.slick.opengl.TextureImpl;

public class CommandPrompt {
	private String currentEntered;
	public boolean isOpen = false;
	private String validChars = " \\/'\".,?!<>=-_()*&^%$#@`~;:[]{}|";
	private Game game;
	
	public CommandPrompt(Game game) {
		this.game = game;
	}
	
	public void open() {
		currentEntered = "";
		isOpen = true;
		game.paused = true;
	}
	
	public void update() {
		while (Keyboard.next()) {
			if (!Keyboard.getEventKeyState()) continue;
			
			int key = Keyboard.getEventKey();
			if (key == Keyboard.KEY_ESCAPE) {
				isOpen = false;
				game.paused = false;
				return;
			}
			
			if (key == Keyboard.KEY_BACK && currentEntered.length() > 0) {
				if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
					int index = currentEntered.lastIndexOf(' ');
					if (index == -1) {
						currentEntered = "";
					} else {
						currentEntered = currentEntered.substring(0, index);
					}
				} else {
					currentEntered = currentEntered.substring(0, currentEntered.length() - 1);
				}
				continue;
			}
			
			if (key == Keyboard.KEY_RETURN) {
				String[] input = currentEntered.trim().toLowerCase().split("\\s+");
				currentEntered = "";
				if (input.length > 0 && input[0].length() > 0) submit(input);
				continue;
			}
			
			char c = Keyboard.getEventCharacter();
			if (Character.isLetterOrDigit(c) || validChars.contains("" + c)) {
				currentEntered += c;
			}
		}
	}
	
	private void submit(String[] input) {
		System.out.print("Ran command:");
		for (String word : input) System.out.print(" " + word);
		System.out.println();
		
		try {
			String cmd = input[0];
			
			if (cmd.equals("openlevel")) {
				game.loadLevel(input[1]);
				
			} else if (cmd.equals("createlevel")) {
				String name = input[1];
				try {
					new Level(name);
					System.out.println("Level already exists. Delete it first.");
					return;
				} catch (Exception e) {}
				int numRows = Integer.parseInt(input[2]);
				int numCols = Integer.parseInt(input[3]);
				game.openLevel(new Level(name, numRows, numCols));
				game.openEditor();
				game.closeEditor(true);
			
			} else if (cmd.equals("deletelevel")) {
				String path = "res/levels/" + input[1];
				Files.deleteIfExists(Paths.get(path + ".tg"));
				Files.deleteIfExists(Paths.get(path + ".sc"));
				Files.deleteIfExists(Paths.get(path + ".ic"));
				
			} else if (cmd.equals("leveledit")) {
				if (input[1].equals("open")) game.openEditor();
				else if (input[1].equals("close")) {
					boolean save;
					if (input.length >= 3) {
						if (input[2].equals("nosave")) save = false;
						else throw new Exception();
					} else {
						save = true;
					}
					game.closeEditor(save);
				}
				else throw new Exception();
			
			} else if (cmd.equals("godmode")) {
				if (input[1].equals("on")) game.level.player.permInvincible = true;
				else if (input[1].equals("off")) game.level.player.permInvincible = false;
				else throw new Exception();
			
			} else if (cmd.equals("sethealth")) {
				game.level.player.health = Integer.parseInt(input[1]);
			
			} else if (cmd.equals("setammo")) {
				game.level.player.ammo = Integer.parseInt(input[1]);
			
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("Error: Bad command format.");
		}
	}
	
	public void render() {
		Font font = FontManager.COMMAND_PROMPT;
		
		glLoadIdentity();
		
		glColor4f(0, 0, 0, 0.3f);
		glBegin(GL_QUADS);
		glVertex2f(0, 0);
		glVertex2f(Display.getWidth(), 0);
		glVertex2f(Display.getWidth(), Display.getHeight());
		glVertex2f(0, Display.getHeight());
		glEnd();
		
		glColor3f(0.1f, 0.1f, 0.1f);
		glBegin(GL_QUADS);
		glVertex2f(0, 0);
		glVertex2f(Display.getWidth(), 0);
		glVertex2f(Display.getWidth(), font.getLineHeight());
		glVertex2f(0, font.getLineHeight());
		glEnd();
		
		glTranslatef(0, font.getLineHeight(), 0);
		glScalef(1, -1, 1);
		
		font.drawString(0, 0, currentEntered);
		TextureImpl.bindNone();
	}
}
