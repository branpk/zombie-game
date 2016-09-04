package game.level;
import static org.lwjgl.opengl.GL11.*;
import game.Camera;
import game.FontManager;
import game.Input;
import game.Vector;
import game.creature.Creature;
import game.creature.CreatureType;
import game.creature.TurretZombie;
import game.item.Item;
import game.item.ItemType;
import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.opengl.TextureImpl;


public class LevelEditor {
	private Level level;
	private SpawnChart spawnChart;
	private ItemChart itemChart;
	
	private boolean tileEditMode;
	
	private boolean tileGridEdited = false;
	private boolean spawnChartEdited = false;
	private boolean itemChartEdited = false;
	
	public LevelEditor(Level level) {
		this.level = level;
		try {
			spawnChart = SpawnChart.loadFromFile("res/levels/" + level.levelName + ".sc");
			itemChart = ItemChart.loadFromFile("res/levels/" + level.levelName + ".ic");
		} catch (IOException e) {
			if (spawnChart == null) {
				System.out.println("Could not open spawn chart. Creating new one.");
				spawnChart = new SpawnChart();
				spawnChartEdited = true;
			}
			if (itemChart == null) {
				System.out.println("Could not open item chart. Creating new one.");
				itemChart = new ItemChart();
				itemChartEdited = true;
			}
			tileGridEdited = true;
		}
	}
	
	public void update() {
		checkTileEdit();
		checkBorderEdit();
		checkSpawnEdit();
		checkItemEdit();
	}
	
	private void checkTileEdit() {
		Vector mousePos = Camera.screenToWorld(Mouse.getX(), Mouse.getY());
		int[] tile = level.tileGrid.getTile(mousePos);
		
		if (Keyboard.isKeyDown(Keyboard.KEY_X) && tile != null) {
			if (Input.wasPressed(Keyboard.KEY_X)) {
				tileEditMode = !level.tileGrid.tiles[tile[0]][tile[1]];
				if (Keyboard.isKeyDown(Keyboard.KEY_T) && Keyboard.isKeyDown(Keyboard.KEY_1))
					fillTiles(tile[0], tile[1]);
			} else {
				level.tileGrid.tiles[tile[0]][tile[1]] = tileEditMode;
			}
			tileGridEdited = true;
		}
		
		if (Mouse.isButtonDown(2) && tile != null) {
			level.tileGrid.exitRow = tile[0];
			level.tileGrid.exitCol = tile[1];
			tileGridEdited = true;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
			level.tileGrid.keyActivated.removeIf((int[] t) ->
					t[0] == tile[0] && t[1] == tile[1]);
			if (!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
				level.tileGrid.keyActivated.add(tile);
			tileGridEdited = true;
		}
	}
	
	private void fillTiles(int row, int col) {
		if (row < 0 || col < 0 || row >= level.tileGrid.numRows || col >= level.tileGrid.numCols)
			return;
		
		if (level.tileGrid.tiles[row][col] != tileEditMode) {
			level.tileGrid.tiles[row][col] = tileEditMode;
			fillTiles(row - 1, col);
			fillTiles(row + 1, col);
			fillTiles(row, col - 1);
			fillTiles(row, col + 1);
		}
	}
	
	private void checkBorderEdit() {
		Vector mousePos = Camera.screenToWorld(Mouse.getX(), Mouse.getY());
		int[] tile = new int[] {Math.round(mousePos.y), Math.round(mousePos.x)};
		
		if (Keyboard.isKeyDown(Keyboard.KEY_DELETE)) {
			if (tile[0] == 0) {
				if (level.player.pos.y < 0.5f) return;
				
				TileGrid grid = new TileGrid(level.tileGrid.numRows - 1, level.tileGrid.numCols);
				for (int i = 0; i < grid.numRows; i++) {
					for (int j = 0; j < grid.numCols; j++) {
						grid.tiles[i][j] = level.tileGrid.tiles[i+1][j];
					}
				}
				grid.exitRow = level.tileGrid.exitRow - 1;
				grid.exitCol = level.tileGrid.exitCol;
				grid.keyActivated = level.tileGrid.keyActivated;
				for (int[] t : grid.keyActivated) {
					t[0] -= 1;
				}
				level.tileGrid = grid;
				tileGridEdited = true;
				
				for (SpawnChart.SpawnPair pair : spawnChart.spawns)
					pair.pos = new Vector(pair.pos.x, pair.pos.y - 1);
				for (Creature c : level.creatures)
					c.pos = new Vector(c.pos.x, c.pos.y - 1);
				spawnChartEdited = true;
				
				for (ItemChart.SpawnPair pair : itemChart.spawns)
					pair.pos = new Vector(pair.pos.x, pair.pos.y - 1);
				for (Item i : level.items)
					i.pos = new Vector(i.pos.x, i.pos.y - 1);
				itemChartEdited = true;
			}
			if (tile[0] == level.tileGrid.numRows - 1) {
				if (level.player.pos.y > level.tileGrid.numRows - 1 - 0.5f) return;
				
				TileGrid grid = new TileGrid(level.tileGrid.numRows - 1, level.tileGrid.numCols);
				for (int i = 0; i < grid.numRows; i++) {
					for (int j = 0; j < grid.numCols; j++) {
						grid.tiles[i][j] = level.tileGrid.tiles[i][j];
					}
				}
				grid.exitCol = level.tileGrid.exitCol;
				grid.exitRow = level.tileGrid.exitRow;
				grid.keyActivated = level.tileGrid.keyActivated;
				level.tileGrid = grid;
				tileGridEdited = true;
			}
			if (tile[1] == 0) {
				if (level.player.pos.x < 0.5f) return;
				
				TileGrid grid = new TileGrid(level.tileGrid.numRows, level.tileGrid.numCols - 1);
				for (int i = 0; i < grid.numRows; i++) {
					for (int j = 0; j < grid.numCols; j++) {
						grid.tiles[i][j] = level.tileGrid.tiles[i][j+1];
					}
				}
				grid.exitCol = level.tileGrid.exitCol - 1;
				grid.exitRow = level.tileGrid.exitRow;
				grid.keyActivated = level.tileGrid.keyActivated;
				for (int[] t : grid.keyActivated) {
					t[1] -= 1;
				}
				level.tileGrid = grid;
				tileGridEdited = true;
				
				for (SpawnChart.SpawnPair pair : spawnChart.spawns)
					pair.pos = new Vector(pair.pos.x - 1, pair.pos.y);
				for (Creature c : level.creatures)
					c.pos = new Vector(c.pos.x - 1, c.pos.y);
				spawnChartEdited = true;
				
				for (ItemChart.SpawnPair pair : itemChart.spawns)
					pair.pos = new Vector(pair.pos.x - 1, pair.pos.y);
				for (Item i : level.items)
					i.pos = new Vector(i.pos.x - 1, i.pos.y);
				itemChartEdited = true;
			}
			if (tile[1] == level.tileGrid.numCols - 1) {
				if (level.player.pos.x > level.tileGrid.numCols - 1 - 0.5f) return;
				
				TileGrid grid = new TileGrid(level.tileGrid.numRows, level.tileGrid.numCols - 1);
				for (int i = 0; i < grid.numRows; i++) {
					for (int j = 0; j < grid.numCols; j++) {
						grid.tiles[i][j] = level.tileGrid.tiles[i][j];
					}
				}
				grid.exitCol = level.tileGrid.exitCol;
				grid.exitRow = level.tileGrid.exitRow;
				grid.keyActivated = level.tileGrid.keyActivated;
				level.tileGrid = grid;
				tileGridEdited = true;
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_INSERT)) {
			if (tile[0] < 0) {
				TileGrid grid = new TileGrid(level.tileGrid.numRows + 1, level.tileGrid.numCols);
				for (int i = 1; i < grid.numRows; i++) {
					for (int j = 0; j < grid.numCols; j++) {
						grid.tiles[i][j] = level.tileGrid.tiles[i-1][j];
					}
				}
				for (int i = 0; i < grid.numCols; i++) {
					grid.tiles[0][i] = true;
				}
				grid.exitCol = level.tileGrid.exitCol;
				grid.exitRow = level.tileGrid.exitRow + 1;
				grid.keyActivated = level.tileGrid.keyActivated;
				for (int[] t : grid.keyActivated) {
					t[0] += 1;
				}
				level.tileGrid = grid;
				tileGridEdited = true;
				
				for (SpawnChart.SpawnPair pair : spawnChart.spawns)
					pair.pos = new Vector(pair.pos.x, pair.pos.y + 1);
				for (Creature c : level.creatures)
					c.pos = new Vector(c.pos.x, c.pos.y + 1);
				spawnChartEdited = true;
				
				for (ItemChart.SpawnPair pair : itemChart.spawns)
					pair.pos = new Vector(pair.pos.x, pair.pos.y + 1);
				for (Item i : level.items)
					i.pos = new Vector(i.pos.x, i.pos.y + 1);
				itemChartEdited = true;
			}
			if (tile[0] > level.tileGrid.numRows - 1) {
				TileGrid grid = new TileGrid(level.tileGrid.numRows + 1, level.tileGrid.numCols);
				for (int i = 0; i < grid.numRows - 1; i++) {
					for (int j = 0; j < grid.numCols; j++) {
						grid.tiles[i][j] = level.tileGrid.tiles[i][j];
					}
				}
				for (int i = 0; i < grid.numCols; i++) {
					grid.tiles[grid.numRows - 1][i] = true;
				}
				grid.exitCol = level.tileGrid.exitCol;
				grid.exitRow = level.tileGrid.exitRow;
				grid.keyActivated = level.tileGrid.keyActivated;
				level.tileGrid = grid;
				tileGridEdited = true;
			}
			if (tile[1] < 0) {
				TileGrid grid = new TileGrid(level.tileGrid.numRows, level.tileGrid.numCols + 1);
				for (int i = 0; i < grid.numRows; i++) {
					for (int j = 1; j < grid.numCols; j++) {
						grid.tiles[i][j] = level.tileGrid.tiles[i][j-1];
					}
				}
				for (int i = 0; i < grid.numRows; i++) {
					grid.tiles[i][0] = true;
				}
				grid.exitCol = level.tileGrid.exitCol + 1;
				grid.exitRow = level.tileGrid.exitRow;
				grid.keyActivated = level.tileGrid.keyActivated;
				for (int[] t : grid.keyActivated) {
					t[1] += 1;
				}
				level.tileGrid = grid;
				tileGridEdited = true;
				
				for (SpawnChart.SpawnPair pair : spawnChart.spawns)
					pair.pos = new Vector(pair.pos.x + 1, pair.pos.y);
				for (Creature c : level.creatures)
					c.pos = new Vector(c.pos.x + 1, c.pos.y);
				spawnChartEdited = true;
				
				for (ItemChart.SpawnPair pair : itemChart.spawns)
					pair.pos = new Vector(pair.pos.x + 1, pair.pos.y);
				for (Item i : level.items)
					i.pos = new Vector(i.pos.x + 1, i.pos.y);
				itemChartEdited = true;
			}
			if (tile[1] > level.tileGrid.numCols - 1) {
				TileGrid grid = new TileGrid(level.tileGrid.numRows, level.tileGrid.numCols + 1);
				for (int i = 0; i < grid.numRows; i++) {
					for (int j = 0; j < grid.numCols - 1; j++) {
						grid.tiles[i][j] = level.tileGrid.tiles[i][j];
					}
				}
				for (int i = 0; i < grid.numRows; i++) {
					grid.tiles[i][grid.numCols - 1] = true;
				}
				grid.exitCol = level.tileGrid.exitCol;
				grid.exitRow = level.tileGrid.exitRow;
				grid.keyActivated = level.tileGrid.keyActivated;
				level.tileGrid = grid;
				tileGridEdited = true;
			}
		}
	}
	
	private void checkSpawnEdit() {
		SpawnChart.SpawnPair editPair = null;
		float dist = 0.5f;
		Vector mousePos = Camera.screenToWorld(Mouse.getX(), Mouse.getY());
		for (SpawnChart.SpawnPair pair : spawnChart.spawns) {
			if (pair.pos.distance(mousePos) <= dist) {
				editPair = pair;
				dist = pair.pos.distance(mousePos);
			}
		}
		
		if (Input.wasPressed(Keyboard.KEY_Q)) {
			if (editPair == null) {
				spawnChart.spawns.add(new SpawnChart.SpawnPair(mousePos, 0, CreatureType.fromCode(0)));
			} else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				spawnChart.spawns.remove(editPair);
			} else {
				int code = editPair.ctype.getCode();
				editPair.ctype = CreatureType.fromCode(code + 1);
				if (editPair.ctype == null) editPair.ctype = CreatureType.fromCode(0);
			}
			spawnChartEdited = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_P) && editPair != null) {
			editPair.angle = mousePos.sub(editPair.pos).angle();
			spawnChartEdited = true;
		}
		if (Input.wasPressed(Keyboard.KEY_2) && editPair != null) {
			level.creatures.add(editPair.instantiate());
		}
	}
	
	private void checkItemEdit() {
		if (Input.wasPressed(Keyboard.KEY_F)) {
			Vector mousePos = Camera.screenToWorld(Mouse.getX(), Mouse.getY());
			
			ItemChart.SpawnPair editPair = null;
			for (ItemChart.SpawnPair pair : itemChart.spawns) {
				if (pair.pos.distance(mousePos) <= 0.5f) {
					editPair = pair;
					break;
				}
			}
			
			if (editPair == null) {
				itemChart.spawns.add(new ItemChart.SpawnPair(mousePos, ItemType.fromCode(0)));
			} else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				itemChart.spawns.remove(editPair);
			} else {
				int code = editPair.itype.getCode();
				editPair.itype = ItemType.fromCode(code + 1);
				if (editPair.itype == null) editPair.itype = ItemType.fromCode(0);
			}
			itemChartEdited = true;
		}
	}

	public void render() {
		glColor3f(1, 1, 1);
		glBegin(GL_LINE_LOOP);
		glVertex2f(-0.5f, -0.5f);
		glVertex2f(level.tileGrid.numCols - 0.5f, -0.5f);
		glVertex2f(level.tileGrid.numCols - 0.5f, level.tileGrid.numRows - 0.5f);
		glVertex2f(-0.5f, level.tileGrid.numRows - 0.5f);
		glEnd();
		
		for (SpawnChart.SpawnPair pair : spawnChart.spawns) {
			Creature c = pair.instantiate();
			float oldRadius = c.drawRadius;
			c.drawRadius = 0.25f;
			if (c instanceof TurretZombie) {
				TurretZombie tz = (TurretZombie) c;
				tz.head.drawRadius *= c.drawRadius/oldRadius;
			}
			c.render();
		}
		
		for (ItemChart.SpawnPair pair : itemChart.spawns) {
			glPushMatrix();
			glTranslatef(pair.pos.x, pair.pos.y, 0);
			glScalef(0.6f, 0.6f, 1);
			
			float[] color = pair.itype.instantiate(pair.pos).color;
			float s = 0.8f;
			glColor4f(color[0]*s, color[1]*s, color[2]*s, 0.5f);
			
			glBegin(GL_QUADS);
			glVertex2f(-0.5f, -0.5f);
			glVertex2f(0.5f, -0.5f);
			glVertex2f(0.5f, 0.5f);
			glVertex2f(-0.5f, 0.5f);
			glEnd();
			
			glPopMatrix();
		}
		
		for (int[] tile : level.tileGrid.keyActivated) {
			glPushMatrix();
			glTranslatef(tile[1], tile[0], 0);
			
			glColor3f(1, 0, 0);
			glBegin(GL_LINE_LOOP);
			glVertex2f(-0.5f, -0.5f);
			glVertex2f(0.5f, -0.5f);
			glVertex2f(0.5f, 0.5f);
			glVertex2f(-0.5f, 0.5f);
			glEnd();
			
			glPopMatrix();
		}
		
		glPushMatrix();
		glLoadIdentity();
		
		glTranslatef(0, Display.getHeight(), 0);
		glScalef(1, -1, 1);
		FontManager.DISPLAY.drawString(0, 0, "Level Editor");
		TextureImpl.bindNone();
		
		glPopMatrix();
	}
	
	public void save() {
		String levelPath = "res/levels/" + level.levelName;
		
		if (tileGridEdited) { 
			TileGrid copy = new TileGrid(level.tileGrid.numRows, level.tileGrid.numCols);
			copy.exitCol = level.tileGrid.exitCol;
			copy.exitRow = level.tileGrid.exitRow;
			copy.keyActivated = level.tileGrid.keyActivated;
			for (int i = 0; i < copy.numRows; i++) {
				for (int j = 0; j < copy.numCols; j++) {
					copy.tiles[i][j] = level.tileGrid.tiles[i][j];
				}
			}
			if (level.player.hasKey) {
				for (int[] tile : level.tileGrid.keyActivated) {
					copy.tiles[tile[0]][tile[1]] = !copy.tiles[tile[0]][tile[1]];
				}
			}
			copy.saveToFile(levelPath + ".tg");
		}
		if (spawnChartEdited) spawnChart.saveToFile(levelPath + ".sc");
		if (itemChartEdited) itemChart.saveToFile(levelPath + ".ic");
	}
}
