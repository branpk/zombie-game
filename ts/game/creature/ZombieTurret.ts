import * as PIXI from 'pixi.js';
import { TextureManager } from '../TextureManager.js';
import { Vector } from '../Vector.js';
import { Level } from '../level/Level.js';
import { Creature } from './Creature.js';

export class ZombieTurret extends Creature {
  detectedPlayer = false;
  headAngle = 0;
  private headSprite: PIXI.Sprite;

  constructor() {
    super(10, TextureManager.TURRETBODY);
    this.drawRadius *= 2;
    this.hitRadius = this.drawRadius - 0.1;
    this.mass = 100;
    this.headSprite = new PIXI.Sprite(TextureManager.ZOMBIEHEAD);
    this.headSprite.anchor.set(0.5);
  }

  update(dt: number, level: Level) {
    if (!this.detectedPlayer && !level.tileGrid.hitsWall(this.pos, level.player.pos) && level.player.pos.distance(this.pos) <= 10) {
      this.detectedPlayer = true;
    }
    if (this.detectedPlayer) {
      const target = level.player.pos;
      const facing = new Vector(this.headAngle);
      const dir = target.sub(this.pos).dot(facing.perpCCW()) > 0 ? 1 : -1;
      const speed = 2 * (1 - target.sub(this.pos).direction().dot(facing) + 1);
      this.headAngle += speed * dir * dt;
    } else {
      this.headAngle = this.angle;
    }
    super.update(dt, level);
  }

  render(container: PIXI.Container) {
    super.render(container);
    if (!this.headSprite.parent) {
      container.addChild(this.headSprite);
      const headRadius = 0.5;
      this.headSprite.width = headRadius * 2;
      this.headSprite.height = headRadius * 2;
      this.headSprite.scale.y *= -1;
    }
    this.headSprite.position.set(this.pos.x, this.pos.y);
    this.headSprite.rotation = this.headAngle;
    this.headSprite.alpha = this.invincible > 0 ? 0.5 : 1;
  }
}
