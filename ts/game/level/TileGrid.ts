import * as PIXI from 'pixi.js';
import { Camera } from '../Camera.js';
import { TextureManager } from '../TextureManager.js';
import { Vector } from '../Vector.js';
import { Creature } from '../creature/Creature.js';

export class TileGrid {
  tiles: boolean[][]; // true iff solid
  exitRow = -1;
  exitCol = -1;
  keyActivated: Array<[number, number]> = [];
  graphics = new PIXI.Graphics();
  wallGraphics = new PIXI.Graphics();
  private doorSprite?: PIXI.Sprite;

  constructor(public numRows: number, public numCols: number) {
    this.tiles = Array.from({ length: numRows }, () => Array(numCols).fill(false));
  }

  hitsWall(start: Vector, end: Vector): boolean {
    const dir = end.sub(start).direction();
    const dist = dir.dot(end.sub(start));
    for (let t = 0; t <= dist; t += 0.01) {
      const tile = this.getTile(start.add(dir.scale(t)));
      if (tile && this.tiles[tile[0]][tile[1]]) return true;
    }
    return false;
  }

  getTile(p: Vector): number[] | null {
    const col = Math.floor(p.x + 0.5);
    if (col < 0 || col >= this.numCols) return null;
    const row = Math.floor(p.y + 0.5);
    if (row < 0 || row >= this.numRows) return null;
    return [row, col];
  }

  collide(c: Creature) {
    if (c.solid) {
      let left = Math.max(0, Math.floor(c.pos.x - c.drawRadius));
      let right = Math.min(this.numCols - 1, Math.floor(c.pos.x + c.drawRadius) + 1);
      let down = Math.max(0, Math.floor(c.pos.y - c.drawRadius));
      let up = Math.min(this.numRows - 1, Math.floor(c.pos.y + c.drawRadius) + 1);
      for (let row = down; row <= up; row++) {
        for (let col = left; col <= right; col++) {
          if (this.tiles[row][col]) this.checkCol(c, row, col);
        }
      }
    }
    if (c.pos.x - c.drawRadius <= -0.5) {
      c.pos = new Vector(-0.5 + c.drawRadius, c.pos.y);
      c.vel = new Vector(Math.max(c.vel.x, 0), c.vel.y);
    }
    if (c.pos.x + c.drawRadius >= this.numCols - 1 + 0.5) {
      c.pos = new Vector(this.numCols - 1 + 0.5 - c.drawRadius, c.pos.y);
      c.vel = new Vector(Math.min(c.vel.x, 0), c.vel.y);
    }
    if (c.pos.y - c.drawRadius <= -0.5) {
      c.pos = new Vector(c.pos.x, -0.5 + c.drawRadius);
      c.vel = new Vector(c.vel.x, Math.max(c.vel.y, 0));
    }
    if (c.pos.y + c.drawRadius >= this.numRows - 1 + 0.5) {
      c.pos = new Vector(c.pos.x, this.numRows - 1 + 0.5 - c.drawRadius);
      c.vel = new Vector(c.vel.x, Math.min(c.vel.y, 0));
    }
  }

  private checkCol(c: Creature, row: number, col: number) {
    if (c.pos.x <= col - 0.5 && Math.abs(c.pos.y - row) <= 0.5) {
      const depth = c.pos.x + c.drawRadius - (col - 0.5);
      if (depth >= 0) {
        c.pos = new Vector(col - 0.5 - c.drawRadius, c.pos.y);
        c.vel = new Vector(Math.min(c.vel.x, 0), c.vel.y);
      }
      return;
    }
    if (c.pos.x >= col + 0.5 && Math.abs(c.pos.y - row) <= 0.5) {
      const depth = (col + 0.5) - (c.pos.x - c.drawRadius);
      if (depth >= 0) {
        c.pos = new Vector(col + 0.5 + c.drawRadius, c.pos.y);
        c.vel = new Vector(Math.max(c.vel.x, 0), c.vel.y);
      }
      return;
    }
    if (c.pos.y <= row - 0.5 && Math.abs(c.pos.x - col) <= 0.5) {
      const depth = c.pos.y + c.drawRadius - (row - 0.5);
      if (depth >= 0) {
        c.pos = new Vector(c.pos.x, row - 0.5 - c.drawRadius);
        c.vel = new Vector(c.vel.x, Math.min(c.vel.y, 0));
      }
      return;
    }
    if (c.pos.y >= row + 0.5 && Math.abs(c.pos.x - col) <= 0.5) {
      const depth = (row + 0.5) - (c.pos.y - c.drawRadius);
      if (depth >= 0) {
        c.pos = new Vector(c.pos.x, row + 0.5 + c.drawRadius);
        c.vel = new Vector(c.vel.x, Math.max(c.vel.y, 0));
      }
      return;
    }
    const corners = [
      new Vector(col - 0.5, row - 0.5),
      new Vector(col + 0.5, row - 0.5),
      new Vector(col + 0.5, row + 0.5),
      new Vector(col - 0.5, row + 0.5)
    ];
    let minDist = Infinity;
    let nearest: Vector | null = null;
    for (const corner of corners) {
      const d = corner.distance(c.pos);
      if (d < minDist) {
        minDist = d;
        nearest = corner;
      }
    }
    const depth = c.drawRadius - minDist;
    if (depth >= 0 && nearest) {
      const normal = c.pos.sub(nearest).direction();
      c.pos = c.pos.add(normal.scale(depth));
      const vn = c.vel.dot(normal);
      if (vn < 0) c.vel = c.vel.sub(normal.scale(vn));
    }
  }

  render(container: PIXI.Container) {
    if (!this.graphics.parent) container.addChild(this.graphics);
    this.graphics.clear();
    let left = Math.max(0, Math.floor(Camera.getLowerLeft().x));
    let right = Math.min(this.numCols - 1, Math.floor(Camera.getUpperRight().x) + 1);
    let down = Math.max(0, Math.floor(Camera.getLowerLeft().y));
    let up = Math.min(this.numRows - 1, Math.floor(Camera.getUpperRight().y) + 1);
    for (let row = down; row <= up; row++) {
      for (let col = left; col <= right; col++) {
        if (!this.tiles[row][col]) this.renderTile(row, col);
      }
    }
  }

  renderOverlays(container: PIXI.Container) {
    if (!this.wallGraphics.parent) container.addChild(this.wallGraphics);
    this.wallGraphics.clear();
    let left = Math.max(0, Math.floor(Camera.getLowerLeft().x));
    let right = Math.min(this.numCols - 1, Math.floor(Camera.getUpperRight().x) + 1);
    let down = Math.max(0, Math.floor(Camera.getLowerLeft().y));
    let up = Math.min(this.numRows - 1, Math.floor(Camera.getUpperRight().y) + 1);
    for (let row = down; row <= up; row++) {
      for (let col = left; col <= right; col++) {
        if (this.tiles[row][col] && !(row === this.exitRow && col === this.exitCol)) {
          this.wallGraphics.beginFill(0x000000);
          this.wallGraphics.drawRect(col - 0.5, row - 0.5, 1, 1);
          this.wallGraphics.endFill();
        }
      }
    }
  }

  private renderTile(row: number, col: number) {
    this.graphics.lineStyle(1 / Camera.pixelsPerUnit, 0x6b6b6b, 1);
    this.graphics.beginFill(0x808080);
    this.graphics.drawRect(col - 0.5, row - 0.5, 1, 1);
    this.graphics.endFill();
    if (row === this.exitRow && col === this.exitCol) {
      if (!this.doorSprite) {
        this.doorSprite = new PIXI.Sprite(TextureManager.DOOR);
        this.doorSprite.anchor.set(0.5);
        this.doorSprite.width = this.doorSprite.height = 1;
        this.doorSprite.scale.y *= -1;
        this.graphics.addChild(this.doorSprite);
      }
      this.doorSprite.position.set(col, row);
    }
  }

  saveToFile(_filename: string) {
    console.warn('TileGrid.saveToFile is not supported in the browser');
  }

  static async loadFromFile(filename: string): Promise<TileGrid> {
    const res = await fetch(filename);
    const data = new Uint8Array(await res.arrayBuffer());
    let offset = 0;
    const decoder = new TextDecoder();
    function readLine(): string {
      let end = data.indexOf(0x0a, offset);
      if (end === -1) end = data.length;
      const line = decoder.decode(data.subarray(offset, end));
      offset = end + 1;
      return line;
    }
    const [numRows, numCols] = readLine().split(' ').map(Number);
    const grid = new TileGrid(numRows, numCols);
    const [exitRow, exitCol] = readLine().split(' ').map(Number);
    grid.exitRow = exitRow;
    grid.exitCol = exitCol;
    const tileCount = numRows * numCols;
    for (let i = 0; i < tileCount; i++) {
      const byte = data[offset++];
      const r = Math.floor(i / numCols);
      const c = i % numCols;
      grid.tiles[r][c] = byte !== 0;
    }
    while (offset < data.length) {
      if (data[offset] === 0x0a) { offset++; continue; }
      let end = data.indexOf(0x0a, offset);
      if (end === -1) end = data.length;
      const line = decoder.decode(data.subarray(offset, end));
      const parts = line.trim().split(' ');
      if (parts.length === 2) grid.keyActivated.push([Number(parts[0]), Number(parts[1])]);
      offset = end + 1;
    }
    return grid;
  }
}
