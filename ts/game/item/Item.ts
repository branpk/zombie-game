import * as PIXI from 'pixi.js';
import { Vector } from '../Vector.js';
import { Creature } from '../creature/Creature.js';

export abstract class Item {
  pos: Vector;
  color?: [number, number, number];
  texture?: PIXI.Texture;
  radius = 0.25;
  display?: PIXI.DisplayObject;

  constructor(pos: Vector, texOrColor: PIXI.Texture | [number, number, number]) {
    this.pos = pos;
    if (texOrColor instanceof PIXI.Texture) this.texture = texOrColor;
    else this.color = texOrColor;
  }

  abstract pickedUpBy(c: Creature): boolean;

  render(container: PIXI.Container) {
    if (this.texture) {
      if (!(this.display instanceof PIXI.Sprite)) {
        const spr = new PIXI.Sprite(this.texture);
        spr.anchor.set(0.5);
        spr.width = this.radius * 2;
        spr.height = this.radius * 2;
        spr.scale.y *= -1;
        this.display = spr;
        container.addChild(spr);
      }
      const spr = this.display as PIXI.Sprite;
      spr.position.set(this.pos.x, this.pos.y);
    } else if (this.color) {
      if (!(this.display instanceof PIXI.Graphics)) {
        this.display = new PIXI.Graphics();
        container.addChild(this.display);
      }
      const g = this.display as PIXI.Graphics;
      g.clear();
      const col = (this.color[0]*255<<16) + (this.color[1]*255<<8) + (this.color[2]*255);
      g.beginFill(col);
      g.drawCircle(this.pos.x, this.pos.y, this.radius);
      g.endFill();
    }
  }
}
