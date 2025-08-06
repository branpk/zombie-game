import { Vector } from '../Vector.js';
import { TileGrid } from './TileGrid.js';

interface Node {
  r: number;
  c: number;
  dist: number;
  parent?: Node;
}

export class AStarPathSearch {
  private fringe: Node[] = [];
  private marked: Array<[number, number]> = [];

  constructor(private grid: TileGrid, private startVec: Vector, private dest: number[]) {}

  calculatePath(): number[][] | null {
    if (!this.dest || this.grid.tiles[this.dest[0]][this.dest[1]]) return null;
    const n = this.calcPath();
    if (!n) return null;
    const path: number[][] = [];
    let cur: Node | null = n;
    while (cur) {
      path.push([cur.r, cur.c]);
      cur = cur.parent ?? null;
    }
    return path;
  }

  private calcPath(): Node | null {
    let allowedDepth = 500;
    this.fringe = [];
    this.marked = [];
    const starters = [
      [Math.floor(this.startVec.y), Math.floor(this.startVec.x)],
      [Math.ceil(this.startVec.y), Math.floor(this.startVec.x)],
      [Math.ceil(this.startVec.y), Math.ceil(this.startVec.x)],
      [Math.floor(this.startVec.y), Math.ceil(this.startVec.x)]
    ];
    for (const s of starters) {
      if (s[0] >= 0 && s[0] < this.grid.numRows && s[1] >= 0 && s[1] < this.grid.numCols && !this.grid.tiles[s[0]][s[1]]) {
        this.fringe.push({ r: s[0], c: s[1], dist: new Vector(s[1], s[0]).distance(this.startVec) });
      }
    }
    while (this.fringe.length && allowedDepth-- >= 0) {
      this.fringe.sort((a, b) => (a.dist + this.heuristic(a)) - (b.dist + this.heuristic(b)));
      const n = this.fringe.shift()!;
      if (n.r === this.dest[0] && n.c === this.dest[1]) return n;
      if (this.isMarked(n)) continue;
      this.mark(n);
      for (const c of this.unmarkedChildren(n)) this.fringe.push(c);
    }
    return null;
  }

  private isMarked(n: Node): boolean {
    return this.marked.some(m => m[0] === n.r && m[1] === n.c);
  }

  private mark(n: Node) { this.marked.push([n.r, n.c]); }

  private heuristic(n: Node): number {
    return new Vector(n.c - this.dest[1], n.r - this.dest[0]).magnitude();
  }

  private unmarkedChildren(n: Node): Node[] {
    const cs = [
      [n.r - 1, n.c],
      [n.r + 1, n.c],
      [n.r, n.c - 1],
      [n.r, n.c + 1]
    ];
    const umcs: Node[] = [];
    for (const c of cs) {
      if (c[0] >= 0 && c[0] < this.grid.numRows && c[1] >= 0 && c[1] < this.grid.numCols && !this.grid.tiles[c[0]][c[1]]) {
        const node: Node = { r: c[0], c: c[1], parent: n, dist: n.dist + new Vector(c[1] - n.c, c[0] - n.r).magnitude() };
        if (!this.isMarked(node)) umcs.push(node);
      }
    }
    return umcs;
  }
}
