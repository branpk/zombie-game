import * as PIXI from 'pixi.js';
import { Vector } from '../Vector.js';
import { Level } from '../level/Level.js';
import { Creature } from './Creature.js';
import { Zombie } from './Zombie.js';
import { Turret } from './Turret.js';
import { TextureManager } from '../TextureManager.js';

export class TurretZombie extends Zombie {
  head = new Turret();
  run = -1;
  runDir = new Vector(0, 0);
  lastRun = 0;
  spawnReq = false;
  spawnPos = new Vector(0, 0);
  spawns: Creature[] = [];

  constructor() {
    super();
    this.drawRadius *= 2;
    this.hitRadius = this.drawRadius - 0.1;
    this.head.drawRadius = 0.45 * this.drawRadius;
    this.head.hitRadius = this.hitRadius;
    this.head.bulletSpeed = 18;
    this.texture = TextureManager.ZOMBIEBODY;
    this.walkSpeed = 60;
    this.mass = 100;
    this.health = 10;
  }

  update(dt: number, level: Level) {
    this.head.pos = this.pos;
    this.head.invincible = this.invincible;
    if (level.tileGrid.hitsWall(this.pos, level.player.pos) || this.run > 0) {
      this.head.angle = this.angle;
    }
    this.head.update(dt, level);

    if (this.run > 0) {
      this.lastRun = Math.random() * 5 + 1;
      const facing = new Vector(this.angle);
      const dir = this.runDir.dot(facing.perpCCW()) > 0 ? 1 : -1;
      this.angle += 3 * dir * dt;
      if (this.run < 1) this.vel = this.vel.add(new Vector(this.angle).scale(this.walkSpeed * 3 * dt));
      this.run -= dt;
      super.update(dt, level);
    } else {
      super.update(dt, level);
    }

    if (this.spawnReq && this.spawnPos.distance(this.pos) > this.hitRadius) {
      this.spawnReq = false;
      let c: Creature;
      if (this.health <= 3) c = new Turret();
      else c = new Zombie();
      c.pos = this.spawnPos;
      c.angle = Math.random() * 2 * Math.PI;
      level.spawnReqs.push(c);
      this.spawns.push(c);
    }

    this.lastRun -= dt;
    if (this.lastRun < 0 && this.detectedPlayer) {
      this.run = 1.5;
      this.runDir = level.player.pos.sub(this.pos).direction();
    }
  }

  takeHit(damage: number, dir: Vector) {
    const oldHealth = this.health;
    super.takeHit(damage, dir);
    if (oldHealth === this.health) return;
    if (this.health === 0) {
      for (const c of this.spawns) {
        c.health = 0;
      }
      return;
    }
    this.run = 1.5;
    this.runDir = dir.negate().direction();
    this.spawnReq = true;
    this.spawnPos = this.pos;
    this.invincible = this.run;
  }

  render(container: PIXI.Container) {
    this.head.pos = this.pos;
    this.head.invincible = this.invincible;
    super.render(container);
    this.head.render(container);
  }
}
