package game.level;
import game.Vector;
import game.creature.Creature;
import game.creature.CreatureType;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.stream.FileImageInputStream;


public class SpawnChart {
	public List<SpawnPair> spawns = new ArrayList<>();
	
	public static class SpawnPair {
		public Vector pos;
		public float angle;
		public CreatureType ctype;
		
		public SpawnPair() {}
		
		public SpawnPair(Vector pos, float angle, CreatureType ctype) {
			this.pos = pos;
			this.angle = angle;
			this.ctype = ctype;
		}
		
		public Creature instantiate() {
			Creature c = ctype.instantiate();
			c.pos = pos;
			c.angle = angle;
			return c;
		}
	}
	
	public List<Creature> instantiate() {
		List<Creature> creatures = new ArrayList<>();
		for (SpawnPair pair : spawns) {
			creatures.add(pair.instantiate());
		}
		return creatures;
	}
	
	public void saveToFile(String filename) {
		PrintWriter outputWriter = null;
		try {
			outputWriter = new PrintWriter(filename);
			outputWriter.println(spawns.size());
			for (SpawnPair pair : spawns) {
				outputWriter.println(pair.pos.x + " " + pair.pos.y + " " + pair.angle 
						+ " " + pair.ctype.getCode());
			}
			outputWriter.flush();
			System.out.println("Saved spawn chart to '" + filename + "'.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			outputWriter.close();
		}
	}
	
	public static SpawnChart loadFromFile(String filename) throws IOException {
		FileImageInputStream reader = null;
		SpawnChart chart = new SpawnChart();
		try {
			reader = new FileImageInputStream(new File(filename));
			
			Scanner s = new Scanner(reader.readLine());
			int numSpawns = s.nextInt();
			s.close();
			
			for (int i = 0; i < numSpawns; i++) {
				SpawnPair pair = new SpawnPair();
				s = new Scanner(reader.readLine());
				pair.pos = new Vector(s.nextFloat(), s.nextFloat());
				pair.angle = s.nextFloat();
				pair.ctype = CreatureType.fromCode(s.nextInt());
				s.close();
				chart.spawns.add(pair);
			}
			System.out.println("Loaded spawn chart from '" + filename + "'.");
		} catch (IOException e) {
			throw new IOException("Failed to load spawn chart: " + filename);
		} finally {
			try {
				reader.close();
			} catch (Exception e) {}
		}
		return chart;
	}
}
