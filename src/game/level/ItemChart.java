package game.level;
import game.Vector;
import game.item.Item;
import game.item.ItemType;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.stream.FileImageInputStream;


public class ItemChart {
	public List<SpawnPair> spawns = new ArrayList<>();
	
	public static class SpawnPair {
		Vector pos;
		ItemType itype;
		
		public SpawnPair() {}
		
		public SpawnPair(Vector pos, ItemType itype) {
			this.pos = pos;
			this.itype = itype;
		}
		
		public Item instantiate() {
			return itype.instantiate(pos);
		}
	}
	
	public List<Item> instantiate() {
		List<Item> items = new ArrayList<>();
		for (SpawnPair pair : spawns) {
			items.add(pair.instantiate());
		}
		return items;
	}
	
	public void saveToFile(String filename) {
		PrintWriter outputWriter = null;
		try {
			outputWriter = new PrintWriter(filename);
			outputWriter.println(spawns.size());
			for (SpawnPair pair : spawns) {
				outputWriter.println(pair.pos.x + " " + pair.pos.y + " " + pair.itype.getCode());
			}
			outputWriter.flush();
			System.out.println("Saved item chart to '" + filename + "'.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			outputWriter.close();
		}
	}
	
	public static ItemChart loadFromFile(String filename) throws IOException {
		FileImageInputStream reader = null;
		ItemChart chart = new ItemChart();
		try {
			reader = new FileImageInputStream(new File(filename));
			
			Scanner s = new Scanner(reader.readLine());
			int numSpawns = s.nextInt();
			s.close();
			
			for (int i = 0; i < numSpawns; i++) {
				SpawnPair pair = new SpawnPair();
				s = new Scanner(reader.readLine());
				pair.pos = new Vector(s.nextFloat(), s.nextFloat());
				pair.itype = ItemType.fromCode(s.nextInt());
				s.close();
				chart.spawns.add(pair);
			}
			System.out.println("Loaded item chart from " + filename);
		} catch (IOException e) {
			throw new IOException("Failed to load item chart: " + filename);
		} finally {
			try {
				reader.close();
			} catch (Exception e) {}
		}
		return chart;
	}
}
