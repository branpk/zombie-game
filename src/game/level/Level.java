package game.level;
import game.Bullet;
import game.Vector;
import game.creature.Creature;
import game.creature.Player;
import game.creature.TurretZombie;
import game.item.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Level {
	public String levelName;
	public TileGrid tileGrid;
	public List<Creature> creatures = new ArrayList<Creature>();
	public List<Creature> spawnReqs;
	public Player player;
	public Creature boss;
	public List<Item> items = new ArrayList<Item>();
	public List<Bullet> bullets = new ArrayList<Bullet>();
	public boolean didWin = false;
	public boolean didDie = false;
	
	
	public Level(String levelName, int numRows, int numCols) {
		this.levelName = levelName;
		tileGrid = new TileGrid(numRows, numCols);
		player = new Player();
		player.pos = Vector.zeroVector;
		player.angle = 0;
		creatures.add(player);
	}
	
	public Level(String levelName) throws IOException {
		this.levelName = levelName;
		String levelPath = "res/levels/" + levelName;
		
		tileGrid = TileGrid.loadFromFile(levelPath + ".tg");
		creatures = SpawnChart.loadFromFile(levelPath + ".sc").instantiate();
		items = ItemChart.loadFromFile(levelPath + ".ic").instantiate();
		
		for (Creature c : creatures) {
			if (c instanceof Player) {
				player = (Player) c;
				break;
			}
		}
		if (player == null) {
			player = new Player();
			player.pos = Vector.zeroVector;
			player.angle = 0;
			creatures.add(player);
		}
		
		if (levelName.equals("b")) {
			for (Creature c : creatures) {
				if (c instanceof TurretZombie) {
					boss = c;
					break;
				}
			}
		}
		
		if (levelName.equals("3") || levelName.equals("B")) player.ammo = 0;
	}
	
	
	public void update(float dt) {
		spawnReqs = new ArrayList<>();
		
		for (Creature c : creatures)
			c.update(dt, this);
		
		updateBullets(dt);
		checkCollisions();
		
		for (int i = 0; i < creatures.size(); i++) {
			if (creatures.get(i).health <= 0) {
				if (creatures.get(i) == player) {
					System.out.println("Player has died.");
					didDie = true;
				}
				
				creatures.remove(i);
				i--;
			}
		}
		
		updateItems(dt);
		
		if (player.hasKey) {
			int[] pTile = tileGrid.getTile(player.pos);
			if (pTile != null && pTile[0] == tileGrid.exitRow && pTile[1] == tileGrid.exitCol) {
				System.out.println("Level complete!");
				didWin = true;
			}
		}
		
		creatures.addAll(spawnReqs);
	}
	
	private void updateBullets(float dt) {
		for (int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			b.update(dt);
			
			boolean struck = false;
			for (Creature c : creatures) {
				if (b.collide(c)) {
					struck = true;
					break;
				}
			}
			if (!struck) {
				int[] tile = tileGrid.getTile(b.pos);
				if (tile != null && tileGrid.tiles[tile[0]][tile[1]])
					struck = true;
			}
			if (struck) {
				bullets.remove(i);
				i--;
			}
		}
	}
	
	private void updateItems(float dt) {
		boolean hadKey = player.hasKey;
		
		if (boss != null && boss.health <= 0) player.hasKey = true;
		
		for (int i = 0; i < items.size(); i++) {
			Item item = items.get(i);
			
			boolean pickedUp = false;
			for (Creature c : creatures) {
				if (!c.solid) continue;
				if (c.pos.distance(item.pos) <= c.hitRadius + item.radius) {
					if (item.pickedUpBy(c)) {
						pickedUp = true;
						break;
					}
				}
			}
			if (pickedUp) {
				items.remove(i);
				i--;
			}
		}
		
		if (!hadKey && player.hasKey) {
			for (int[] tile : tileGrid.keyActivated) {
				tileGrid.tiles[tile[0]][tile[1]] = !tileGrid.tiles[tile[0]][tile[1]];
			}
		}
	}
	
	private void checkCollisions() {
		for (int i = 0; i < creatures.size(); i++) {
			for (int j = i+1; j < creatures.size(); j++) {
				creatures.get(i).collide(creatures.get(j));
			}
		}
		for (Creature c : creatures)
			tileGrid.collide(c);
	}


	public void render() {
		tileGrid.render();
		
		for (Item i : items) {
			i.render();
		}
		for (Creature c : creatures) {
			c.render();
		}
		for (Bullet b : bullets) {
			b.render();
		}
	}
}
