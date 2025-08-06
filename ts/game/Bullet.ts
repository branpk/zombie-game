import * as PIXI from 'pixi.js';
import { Vector } from './Vector.js';
import { Creature } from './creature/Creature.js';
import { Turret } from './creature/Turret.js';
import { TurretZombie } from './creature/TurretZombie.js';

export class Bullet {
  shooter: Creature;
  pos: Vector;
  dir: Vector;
  speed = 18;
  graphics = new PIXI.Graphics();

  constructor(shooter: Creature, pos: Vector, dir: Vector) {
    this.shooter = shooter;
    this.pos = pos;
    this.dir = dir.direction();
  }

  update(dt: number) {
    this.pos = this.pos.add(this.dir.scale(this.speed * dt));
  }

  collide(c: Creature): boolean {
    if (!c.solid) return false;
    if (c.pos.distance(this.pos) <= c.hitRadius) {
      if (this.shooter instanceof Turret) {
        if (c instanceof Turret || c instanceof TurretZombie) return true;
      }
      const damage = this.shooter.health > 0 ? 1 : 0;
      c.takeHit(damage, this.dir);
      return true;
    }
    return false;
  }

  render(container: PIXI.Container) {
    if (!this.graphics.parent) container.addChild(this.graphics);
    const radius = 0.07;
    this.graphics.clear();
    this.graphics.beginFill(0x000000);
    this.graphics.drawCircle(this.pos.x, this.pos.y, radius);
    this.graphics.endFill();
  }
}
