import * as PIXI from 'pixi.js';
import { Vector } from './Vector.js';

export class Camera {
  static focus: Vector = Vector.zeroVector;
  static pixelsPerUnit = 1;
  private static app: PIXI.Application;

  static init(app: PIXI.Application) {
    this.app = app;
  }

  static get width(): number { return this.app.renderer.width; }
  static get height(): number { return this.app.renderer.height; }

  static lookThrough(container: PIXI.Container) {
    const w = this.app.renderer.width;
    const h = this.app.renderer.height;
    container.position.set(w / 2, h / 2);
    container.scale.set(this.pixelsPerUnit, -this.pixelsPerUnit);
    container.position.x -= this.focus.x * this.pixelsPerUnit;
    container.position.y += this.focus.y * this.pixelsPerUnit;
  }

  static screenToWorld(sx: number, sy: number): Vector {
    const w = this.app.renderer.width;
    const h = this.app.renderer.height;
    sx -= w / 2;
    sy = h / 2 - sy;
    const pos = new Vector(
      sx / this.pixelsPerUnit + this.focus.x,
      sy / this.pixelsPerUnit + this.focus.y
    );
    return pos;
  }

  static getLowerLeft(): Vector {
    const w = this.app.renderer.width;
    const h = this.app.renderer.height;
    const left = this.focus.x - (w / 2) / this.pixelsPerUnit;
    const down = this.focus.y - (h / 2) / this.pixelsPerUnit;
    return new Vector(left, down);
  }

  static getUpperRight(): Vector {
    const w = this.app.renderer.width;
    const h = this.app.renderer.height;
    const right = this.focus.x + (w / 2) / this.pixelsPerUnit;
    const up = this.focus.y + (h / 2) / this.pixelsPerUnit;
    return new Vector(right, up);
  }
}
