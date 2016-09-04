package game.level;
import game.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;


public class AStarPathSearch {
	private TileGrid grid;
	private Vector startVec;
	private int[] dest;
	private Queue<Node> fringe = new PriorityQueue<>();
	private Set<int[]> marked = new HashSet<>();
	
	public AStarPathSearch(TileGrid grid, Vector startVec, int[] dest) {
		this.grid = grid;
		this.startVec = startVec;
		this.dest = dest;
	}
	
	public List<int[]> calculatePath() {
		if (dest == null || grid.tiles[dest[0]][dest[1]]) return null;
		
		Node n = calcPath();
		
		List<int[]> path = new ArrayList<>();
		while (n != null) {
			path.add(new int[] {n.r, n.c});
			n = n.parent;
		}
		return path;
	}
	
	private Node calcPath() {
		int allowedDepth = 500;
		
		fringe.clear();
		marked.clear();
		
		int[][] starters = {
				{(int) Math.floor(startVec.y), (int) Math.floor(startVec.x)},
				{(int) Math.ceil(startVec.y), (int) Math.floor(startVec.x)},
				{(int) Math.ceil(startVec.y), (int) Math.ceil(startVec.x)},
				{(int) Math.floor(startVec.y), (int) Math.ceil(startVec.x)}
		};
		for (int[] s : starters) {
			if (s[0] >= 0 && s[0] < grid.numRows 
					&& s[1] >= 0 && s[1] < grid.numCols
					&& !grid.tiles[s[0]][s[1]]) {
				Node startN = new Node();
				startN.r = s[0];
				startN.c = s[1];
				startN.dist = new Vector(s[1], s[0]).distance(startVec);
				fringe.add(startN);
			}
		}
		
		while (!fringe.isEmpty() && allowedDepth-- >= 0) {
			Node n = fringe.remove();
			
			if (n.r == dest[0] && n.c == dest[1]) return n;
			
			if (isMarked(n)) continue;
			mark(n);
			
			for (Node c : n.unmarkedChildren()) {
				fringe.add(c);
			}
		}
		
		return null;
	}
	
	private boolean isMarked(Node n) {
		for (int[] m : marked) {
			if (m[0] == n.r && m[1] == n.c)
				return true;
		}
		return false;
	}
	
	private void mark(Node n) {
		marked.add(new int[] {n.r, n.c});
	}
	
	private float heuristic(Node n) {
		return new Vector(n.c - dest[1], n.r - dest[0]).magnitude();
	}
	
	private class Node implements Comparable<Node> {
		int r, c;
		float dist = Float.POSITIVE_INFINITY;
		Node parent;
		
		@Override
		public int compareTo(Node other) {
			return Float.compare(dist + heuristic(this), other.dist + heuristic(other));
		}
		
		List<Node> unmarkedChildren() {
			int[][] cs = {
					new int[] {r - 1, c},
					new int[] {r + 1, c},
					new int[] {r, c - 1},
					new int[] {r, c + 1}
				};
			List<Node> umcs = new ArrayList<>();
			for (int[] c : cs) {
				if (c[0] >= 0 && c[0] < grid.numRows 
						&& c[1] >= 0 && c[1] < grid.numCols
						&& !grid.tiles[c[0]][c[1]]) {
					Node n = new Node();
					n.r = c[0];
					n.c = c[1];
					n.parent = this;
					n.dist = dist + new Vector(n.c - this.c, n.r - r).magnitude();
					if (!isMarked(n)) umcs.add(n);
				}
			}
			return umcs;
		}
	}
}
