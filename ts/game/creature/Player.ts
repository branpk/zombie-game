import * as PIXI from 'pixi.js';
import { Bullet } from '../Bullet.js';
import { Camera } from '../Camera.js';
import { Controls } from '../Controls.js';
import { Input } from '../Input.js';
import { TextureManager } from '../TextureManager.js';
import { Vector } from '../Vector.js';
import { Level } from '../level/Level.js';
import { Creature } from './Creature.js';

export class Player extends Creature {
  hasKey = false;
  ammo = 10;
  private mouseWasDown = true;
  private mouseRWasDown = true;
  private knifeSwinging = false;
  private knifeSwingT = 0;
  private knifeSwingPos = 0;
  private knifeGfx = new PIXI.Graphics();

  constructor() {
    super(10, TextureManager.PLAYER);
  }

  update(dt: number, level: Level) {
    const mousePos = Camera.screenToWorld(Input.mouseX, Input.mouseY);
    this.angle = mousePos.sub(this.pos).angle();
    const facing = new Vector(this.angle);

    let moveDir = new Vector(0, 0);
    if (Input.isDown(Controls.left)) moveDir = moveDir.add(new Vector(-1, 0));
    if (Input.isDown(Controls.right)) moveDir = moveDir.add(new Vector(1, 0));
    if (Input.isDown(Controls.down)) moveDir = moveDir.add(new Vector(0, -1));
    if (Input.isDown(Controls.up)) moveDir = moveDir.add(new Vector(0, 1));
    moveDir = moveDir.direction();
    const speed = 100 * (moveDir.dot(facing) + 3) / 4;
    this.vel = this.vel.add(moveDir.scale(speed * dt));

    if (Input.isMouseDown(0) && !this.mouseWasDown && this.ammo > 0) {
      const bpos = this.pos.add(facing.scale(this.hitRadius + 0.1));
      level.bullets.push(new Bullet(this, bpos, facing));
      this.ammo -= 1;
    }
    this.mouseWasDown = Input.isMouseDown(0);

    if ((Input.isMouseDown(2) && !this.mouseRWasDown) || Input.wasPressed('Space')) {
      if (!this.knifeSwinging) {
        this.knifeSwinging = true;
        this.knifeSwingT = 0;
      }
    }
    this.mouseRWasDown = Input.isMouseDown(2);
    if (this.knifeSwinging) {
      if (this.knifeSwingT >= 1) this.knifeSwinging = false;
      this.knifeSwingPos = 0.7 * (1 - 2 * Math.abs(this.knifeSwingT - 0.5));
      const knifePos = this.pos.add(facing.scale(this.hitRadius + this.knifeSwingPos));
      for (const c of level.creatures) {
        if (c !== this && c.pos.distance(knifePos) <= c.hitRadius) {
          c.takeHit(1, facing);
        }
      }
      this.knifeSwingT += dt / 0.3;
    }

    super.update(dt, level);
  }

  render(container: PIXI.Container) {
    super.render(container);
    if (this.knifeSwinging) {
      if (!this.knifeGfx.parent && this.display) {
        const idx = container.getChildIndex(this.display);
        container.addChildAt(this.knifeGfx, idx);
      }
      this.knifeGfx.clear();
      const base = this.pos.add(new Vector(this.angle).scale(this.hitRadius));
      this.knifeGfx.position.set(base.x, base.y);
      this.knifeGfx.rotation = this.angle;
      this.knifeGfx.beginFill(0x333333);
      this.knifeGfx.moveTo(-0.2, -0.05);
      this.knifeGfx.lineTo(-0.2, 0.05);
      this.knifeGfx.lineTo(this.knifeSwingPos, 0);
      this.knifeGfx.endFill();
    } else if (this.knifeGfx.parent) {
      this.knifeGfx.clear();
      this.knifeGfx.parent.removeChild(this.knifeGfx);
    }
  }
}
