package game.level;

import static org.lwjgl.opengl.GL11.*;
import game.Camera;
import game.TextureManager;
import game.Vector;
import game.creature.Creature;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import javax.imageio.stream.FileImageInputStream;

import org.newdawn.slick.opengl.TextureImpl;

public class TileGrid {
	public final int numRows, numCols;
	public boolean[][] tiles; // true iff solid
	public int exitRow = -1, exitCol = -1;
	public Set<int[]> keyActivated = new HashSet<>();
	
	public TileGrid(int numRows, int numCols) {
		this.numRows = numRows;
		this.numCols = numCols;
		tiles = new boolean[numRows][numCols];
	}
	
	public boolean hitsWall(Vector start, Vector end) {
		Vector dir = end.sub(start).direction();
		float dist = dir.dot(end.sub(start));
		for (float t = 0; t <= dist; t += 0.01f) {
			int[] tile = getTile(start.add(dir.scale(t)));
			if (tile != null && tiles[tile[0]][tile[1]]) {
				return true;
			}
		}
		return false;
	}
	
	public int[] getTile(Vector p) {
		int col = Math.round(p.x);
		if (col < 0 || col >= numCols) return null;
		int row = Math.round(p.y);
		if (row < 0 || row >= numRows) return null;
		return new int[] {row, col};
	}
	
	public void collide(Creature c) {
		if (c.solid) {
			int left = (int) (c.pos.x - c.drawRadius);
			left = Math.max(0, left);
			int right = (int) (c.pos.x + c.drawRadius) + 1;
			right = Math.min(numCols - 1, right);
			
			int down = (int) (c.pos.y - c.drawRadius);
			down = Math.max(0, down);
			int up = (int) (c.pos.y + c.drawRadius) + 1;
			up = Math.min(numRows - 1, up);
			
			for (int row = down; row <= up; row++) {
				for (int col = left; col <= right; col++) {
					if (tiles[row][col]) {
						checkCol(c, row, col);
					}
				}
			}
		}
		
		if (c.pos.x - c.drawRadius <= -0.5f) {
			c.pos = new Vector(-0.5f + c.drawRadius, c.pos.y);
			c.vel = new Vector(Math.max(c.vel.x, 0), c.vel.y);
		}
		if (c.pos.x + c.drawRadius >= numCols - 1 + 0.5f) {
			c.pos = new Vector(numCols - 1 + 0.5f - c.drawRadius, c.pos.y);
			c.vel = new Vector(Math.min(c.vel.x, 0), c.vel.y);
		}
		if (c.pos.y - c.drawRadius <= -0.5f) {
			c.pos = new Vector(c.pos.x, -0.5f + c.drawRadius);
			c.vel = new Vector(c.vel.x, Math.max(c.vel.y, 0));
		}
		if (c.pos.y + c.drawRadius >= numRows - 1 + 0.5f) {
			c.pos = new Vector(c.pos.x, numRows - 1 + 0.5f - c.drawRadius);
			c.vel = new Vector(c.vel.x, Math.min(c.vel.y, 0));
		}
	}
	
	private void checkCol(Creature c, int row, int col) {
		if (c.pos.x <= col - 0.5f && Math.abs(c.pos.y - row) <= 0.5f) {
			float depth = (c.pos.x + c.drawRadius) - (col - 0.5f);
			if (depth >= 0) {
				c.pos = new Vector((col - 0.5f) - c.drawRadius, c.pos.y);
				c.vel = new Vector(Math.min(c.vel.x, 0), c.vel.y);
			}
			return;
		}
		if (c.pos.x >= col + 0.5f && Math.abs(c.pos.y - row) <= 0.5f) {
			float depth = (col + 0.5f) - (c.pos.x - c.drawRadius);
			if (depth >= 0) {
				c.pos = new Vector((col + 0.5f) + c.drawRadius, c.pos.y);
				c.vel = new Vector(Math.max(c.vel.x, 0), c.vel.y);
			}
			return;
		}
		if (c.pos.y <= row - 0.5f && Math.abs(c.pos.x - col) <= 0.5f) {
			float depth = (c.pos.y + c.drawRadius) - (row - 0.5f);
			if (depth >= 0) {
				c.pos = new Vector(c.pos.x, (row - 0.5f) - c.drawRadius);
				c.vel = new Vector(c.vel.x, Math.min(c.vel.y, 0));
			}
			return;
		}
		if (c.pos.y >= row + 0.5f && Math.abs(c.pos.x - col) <= 0.5f) {
			float depth = (row + 0.5f) - (c.pos.y - c.drawRadius);
			if (depth >= 0) {
				c.pos = new Vector(c.pos.x, (row + 0.5f) + c.drawRadius);
				c.vel = new Vector(c.vel.x, Math.max(c.vel.y, 0));
			}
			return;
		}
		
		Vector[] corners = new Vector[] {
				new Vector(col - 0.5f, row - 0.5f),
				new Vector(col + 0.5f, row - 0.5f),
				new Vector(col + 0.5f, row + 0.5f),
				new Vector(col - 0.5f, row + 0.5f),
			};
		float minDist = Float.POSITIVE_INFINITY;
		Vector nearestCorner = null;
		for (Vector corner : corners) {
			if (corner.distance(c.pos) < minDist) {
				minDist = corner.distance(c.pos);
				nearestCorner = corner;
			}
		}
		
		float depth = c.drawRadius - minDist;
		if (depth >= 0) {
			Vector normal = c.pos.sub(nearestCorner).direction();
			c.pos = c.pos.add(normal.scale(depth));
			float vn = c.vel.dot(normal);
			if (vn < 0) {
				c.vel = c.vel.sub(normal.scale(vn));
			}
		}
	}
	
	public void render() {
		int left = (int) Camera.getLowerLeft().x;
		left = Math.max(0, left);
		int right = (int) Camera.getUpperRight().x + 1;
		right = Math.min(numCols - 1, right);
		
		int down = (int) Camera.getLowerLeft().y;
		down = Math.max(0, down);
		int up = (int) Camera.getUpperRight().y + 1;
		up = Math.min(numRows - 1, up);
		
		for (int row = down; row <= up; row++) {
			for (int col = left; col <= right; col++) {
				if (!tiles[row][col]) {
					renderTile(row, col);
				}
			}
		}
	}
	
	private void renderTile(int row, int col) {

		glColor3f(0.5f, 0.5f, 0.5f);
		glBegin(GL_QUADS);
		glVertex2f(col - 0.5f, row - 0.5f);
		glVertex2f(col + 0.5f, row - 0.5f);
		glVertex2f(col + 0.5f, row + 0.5f);
		glVertex2f(col - 0.5f, row + 0.5f);
		glEnd();
		
		glColor3f(0.42f, 0.42f, 0.42f);
		glBegin(GL_LINE_LOOP);
		glVertex2f(col - 0.5f, row - 0.5f);
		glVertex2f(col + 0.5f, row - 0.5f);
		glVertex2f(col + 0.5f, row + 0.5f);
		glVertex2f(col - 0.5f, row + 0.5f);
		glEnd();
		
		if (row == exitRow && col == exitCol) {
			TextureManager.DOOR.bind();
			glColor3f(1, 1, 1);
			
			glBegin(GL_QUADS);
			glTexCoord2f(0, 1);
			glVertex2f(col - 0.5f, row - 0.5f);
			glTexCoord2f(1, 1);
			glVertex2f(col + 0.5f, row - 0.5f);
			glTexCoord2f(1, 0);
			glVertex2f(col + 0.5f, row + 0.5f);
			glTexCoord2f(0, 0);
			glVertex2f(col - 0.5f, row + 0.5f);
			glEnd();
			
			TextureImpl.bindNone();
		}
	}
	
	public void saveToFile(String filename) {
		PrintWriter outputWriter = null;
		try {
			outputWriter = new PrintWriter(filename);
			outputWriter.println(numRows + " " + numCols);
			outputWriter.println(exitRow + " " + exitCol);
			for (int i = 0; i < numRows; i++) {
				for (int j = 0; j < numCols; j++) {
					char c = (char) (tiles[i][j] ? 1 : 0);
					outputWriter.write(new char[] {c});
				}
			}
			
			for (int[] tile : keyActivated) {
				outputWriter.println(tile[0] + " " + tile[1]);
			}
			
			outputWriter.flush();
			System.out.println("Saved tile grid to '" + filename + "'.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			outputWriter.close();
		}
	}
	
	public static TileGrid loadFromFile(String filename) throws IOException {
		FileImageInputStream reader = null;
		TileGrid grid = null;
		try {
			reader = new FileImageInputStream(new File(filename));
			
			Scanner s = new Scanner(reader.readLine());
			int numRows = s.nextInt();
			int numCols = s.nextInt();
			s.close();
			
			grid = new TileGrid(numRows, numCols);
			
			s = new Scanner(reader.readLine());
			grid.exitRow = s.nextInt();
			grid.exitCol = s.nextInt();
			s.close();
			
			for (int i = 0; i < numRows; i++) {
				for (int j = 0; j < numCols; j++) {
					byte c = reader.readByte();
					grid.tiles[i][j] = (c != 0);
				}
			}
			
			String line;
			while ((line = reader.readLine()) != null) {
				s = new Scanner(line);
				grid.keyActivated.add(new int[] {s.nextInt(), s.nextInt()});
			}
			
			System.out.println("Loaded tile grid from '" + filename + "'.");
		} catch (IOException e) {
			throw new IOException("Failed to load tile grid: " + filename);
		} finally {
			try {
				reader.close();
			} catch (Exception e) {}
		}
		return grid;
	}
}
