import * as PIXI from 'pixi.js';
import { Bullet } from '../Bullet.js';
import { Vector } from '../Vector.js';
import { Creature } from '../creature/Creature.js';
import { Player } from '../creature/Player.js';
import { TurretZombie } from '../creature/TurretZombie.js';
import { Item } from '../item/Item.js';
import { TileGrid } from './TileGrid.js';
import { instantiateSpawnChart, loadSpawnChart } from './SpawnChart.js';
import { instantiateItemChart, loadItemChart } from './ItemChart.js';

export class Level {
  tileGrid: TileGrid;
  creatures: Creature[] = [];
  spawnReqs: Creature[] = [];
  player: Player;
  boss?: Creature;
  items: Item[] = [];
  bullets: Bullet[] = [];
  didWin = false;
  didDie = false;

  constructor(public levelName: string, tileGrid: TileGrid, creatures: Creature[], items: Item[]) {
    this.tileGrid = tileGrid;
    this.creatures = creatures;
    this.items = items;
    this.player = this.creatures.find(c => c instanceof Player) as Player;
    if (!this.player) {
      this.player = new Player();
      this.player.pos = Vector.zeroVector;
      this.player.angle = 0;
      this.creatures.push(this.player);
    }
    if (levelName === 'b') {
      for (const c of this.creatures) if (c instanceof TurretZombie) { this.boss = c; break; }
    }
    if (levelName === '3' || levelName === 'b') this.player.ammo = 0;
  }

  static async load(levelName: string): Promise<Level> {
    const levelPath = `res/levels/${levelName}`;
    const tileGrid = await TileGrid.loadFromFile(`${levelPath}.tg`);
    const creatures = instantiateSpawnChart(await loadSpawnChart(`${levelPath}.sc`));
    const items = instantiateItemChart(await loadItemChart(`${levelPath}.ic`));
    return new Level(levelName, tileGrid, creatures, items);
  }

  update(dt: number) {
    this.spawnReqs = [];
    for (const c of this.creatures) c.update(dt, this);
    this.updateBullets(dt);
    this.checkCollisions();
    for (let i = 0; i < this.creatures.length; i++) {
      if (this.creatures[i].health <= 0) {
        if (this.creatures[i] === this.player) {
          console.log('Player has died.');
          this.didDie = true;
        }
        const disp = this.creatures[i].display;
        if (disp && disp.parent) disp.parent.removeChild(disp);
        this.creatures.splice(i, 1);
        i--;
      }
    }
    this.updateItems(dt);
    if (this.player.hasKey) {
      const pTile = this.tileGrid.getTile(this.player.pos);
      if (pTile && pTile[0] === this.tileGrid.exitRow && pTile[1] === this.tileGrid.exitCol) {
        console.log('Level complete!');
        this.didWin = true;
      }
    }
    this.creatures.push(...this.spawnReqs);
  }

  private updateBullets(dt: number) {
    for (let i = 0; i < this.bullets.length; i++) {
      const b = this.bullets[i];
      b.update(dt);
      let struck = false;
      for (const c of this.creatures) {
        if (b.collide(c)) { struck = true; break; }
      }
      if (!struck) {
        const tile = this.tileGrid.getTile(b.pos);
        if (tile && this.tileGrid.tiles[tile[0]][tile[1]]) struck = true;
      }
      if (struck) {
        if (b.graphics.parent) b.graphics.parent.removeChild(b.graphics);
        this.bullets.splice(i, 1);
        i--;
      }
    }
  }

  private updateItems(_dt: number) {
    const hadKey = this.player.hasKey;
    if (this.boss && this.boss.health <= 0) this.player.hasKey = true;
    for (let i = 0; i < this.items.length; i++) {
      const item = this.items[i];
      let pickedUp = false;
      for (const c of this.creatures) {
        if (!c.solid) continue;
        if (c.pos.distance(item.pos) <= c.hitRadius + item.radius) {
          if (item.pickedUpBy(c)) { pickedUp = true; break; }
        }
      }
      if (pickedUp) {
        if (item.display && item.display.parent) {
          item.display.parent.removeChild(item.display);
        }
        this.items.splice(i, 1);
        i--;
      }
    }
    if (!hadKey && this.player.hasKey) {
      for (const tile of this.tileGrid.keyActivated) {
        this.tileGrid.tiles[tile[0]][tile[1]] = !this.tileGrid.tiles[tile[0]][tile[1]];
      }
    }
  }

  private checkCollisions() {
    for (let i = 0; i < this.creatures.length; i++) {
      for (let j = i + 1; j < this.creatures.length; j++) {
        this.creatures[i].collide(this.creatures[j]);
      }
    }
    for (const c of this.creatures) this.tileGrid.collide(c);
  }

  render(container: PIXI.Container) {
    this.tileGrid.render(container);
    for (const i of this.items) i.render(container);
    for (const c of this.creatures) c.render(container);
    for (const b of this.bullets) b.render(container);
    this.tileGrid.renderOverlays(container);
  }
}
