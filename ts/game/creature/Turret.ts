import { Bullet } from '../Bullet.js';
import { TextureManager } from '../TextureManager.js';
import { Vector } from '../Vector.js';
import { Level } from '../level/Level.js';
import { Creature } from './Creature.js';

export class Turret extends Creature {
  private detectedPlayer = false;
  private shootTimer = -0.1;
  bulletSpeed = 12;

  constructor() {
    super(3, TextureManager.TURRET);
    this.mass = 10;
    this.hitRadius = 0.5;
  }

  update(dt: number, level: Level) {
    if (!this.detectedPlayer && !level.tileGrid.hitsWall(this.pos, level.player.pos) && level.player.pos.distance(this.pos) <= 10) {
      this.detectedPlayer = true;
    }

    if (this.detectedPlayer && level.player.health > 0 && !level.tileGrid.hitsWall(this.pos, level.player.pos)) {
      const facing = new Vector(this.angle);
      const targFace = level.player.pos.sub(this.pos).direction();
      const dir = targFace.dot(facing.perpCCW()) > 0 ? 1 : -1;
      const speed = 2 * (1 - targFace.dot(facing) + 1);
      this.angle += speed * dir * dt;
      if (this.shootTimer <= 0 && targFace.dot(facing) > 0.8) {
        const bpos = this.pos.add(facing.scale(this.hitRadius + 0.1));
        const bullet = new Bullet(this, bpos, facing);
        bullet.speed = this.bulletSpeed;
        level.bullets.push(bullet);
        this.shootTimer = 1;
      }
    }

    if (this.shootTimer > 0) this.shootTimer -= dt;
    super.update(dt, level);
  }
}
