import * as PIXI from 'pixi.js';
import { Vector } from '../Vector.js';
import { Camera } from '../Camera.js';
import { Level } from '../level/Level.js';

export class Creature {
  pos: Vector = Vector.zeroVector;
  vel: Vector = Vector.zeroVector;
  angle = 0;
  drawRadius = 0.5;
  hitRadius = 0.4;
  mass = 1;
  health: number;
  invincible = -0.01;
  permInvincible = false;
  solid = true;
  color?: [number, number, number];
  texture?: PIXI.Texture;
  display?: PIXI.DisplayObject;

  constructor(health: number, texOrColor: PIXI.Texture | [number, number, number]) {
    this.health = health;
    if (texOrColor instanceof PIXI.Texture) this.texture = texOrColor;
    else this.color = texOrColor;
  }

  update(dt: number, _level: Level) {
    this.vel = this.vel.add(this.vel.scale(-20 * dt));
    this.pos = this.pos.add(this.vel.scale(dt));
    while (this.angle >= 2 * Math.PI) this.angle -= 2 * Math.PI;
    while (this.angle <= -2 * Math.PI) this.angle += 2 * Math.PI;
    if (this.invincible >= 0) this.invincible -= dt;
    if (this.permInvincible) this.invincible = 0.01;
  }

  collide(other: Creature) {
    if (!this.solid || !other.solid) return;
    const dist = other.pos.sub(this.pos).magnitude();
    const depth = other.hitRadius + this.hitRadius - dist;
    if (depth >= 0) {
      const normal = other.pos.sub(this.pos).direction();
      const relVel = this.vel.sub(other.vel);
      const relVelR = relVel.dot(normal);
      const m1 = this.mass;
      const m2 = other.mass;
      const impact = (1 / (1 / m1 + 1 / m2)) * relVelR;
      this.vel = this.vel.add(normal.scale(-impact / m1));
      other.vel = other.vel.add(normal.scale(impact / m2));
      const disp = (1 / (1 / m1 + 1 / m2)) * depth;
      this.pos = this.pos.add(normal.scale(-disp / m1));
      other.pos = other.pos.add(normal.scale(disp / m2));
      this.onTouch(other);
      other.onTouch(this);
    }
  }

  onTouch(_other: Creature) {}

  takeHit(damage: number, dir: Vector) {
    if (this.invincible > 0) return;
    this.vel = this.vel.add(dir.direction().scale((1 / this.mass) * 20));
    if (damage <= 0) return;
    this.health -= damage;
    this.invincible = 0.7;
  }

  render(container: PIXI.Container) {
    if (this.texture) {
      if (!(this.display instanceof PIXI.Sprite)) {
        const spr = new PIXI.Sprite(this.texture);
        spr.anchor.set(0.5);
        spr.width = this.drawRadius * 2;
        spr.height = this.drawRadius * 2;
        spr.scale.y *= -1;
        this.display = spr;
        container.addChild(spr);
      }
      const spr = this.display as PIXI.Sprite;
      spr.position.set(this.pos.x, this.pos.y);
      spr.rotation = this.angle;
      spr.alpha = this.invincible > 0 ? 0.5 : 1;
    } else if (this.color) {
      if (!(this.display instanceof PIXI.Graphics)) {
        this.display = new PIXI.Graphics();
        container.addChild(this.display);
      }
      const g = this.display as PIXI.Graphics;
      g.clear();
      const col = (this.color[0] * 255 << 16) + (this.color[1] * 255 << 8) + (this.color[2] * 255);
      g.beginFill(col, this.invincible > 0 ? 0.5 : 1);
      g.drawCircle(this.pos.x, this.pos.y, this.drawRadius);
      g.endFill();
      // line to show facing
      g.lineStyle(1 / Camera.pixelsPerUnit, 0x000000, 1);
      g.moveTo(this.pos.x, this.pos.y);
      const end = this.pos.add(new Vector(this.angle).scale(this.drawRadius));
      g.lineTo(end.x, end.y);
    }
  }
}
