import * as PIXI from 'pixi.js';
import { Camera } from './Camera.js';
import { FontManager } from './FontManager.js';
import { Input } from './Input.js';
import { TextureManager } from './TextureManager.js';
import { Level } from './level/Level.js';

export class Game {
  level: Level | null = null;
  paused = false;
  private levelOrder: string[] = [];
  private keySprite: PIXI.Sprite = new PIXI.Sprite(TextureManager.KEY);
  private healthText: PIXI.Text = new PIXI.Text('', FontManager.DISPLAY);
  private ammoText: PIXI.Text = new PIXI.Text('', FontManager.DISPLAY);

  constructor(levels: string[], private world: PIXI.Container) {
    this.levelOrder = [...levels];
    this.keySprite.anchor.set(0, 0);
    void this.loadLevel(this.levelOrder.shift()!);
  }

  async loadLevel(levelName: string) {
    this.closeLevel();
    const level = await Level.load(levelName);
    this.openLevel(level);
  }

  openLevel(level: Level) {
    this.level = level;
    Camera.focus = level.player.pos;
    Camera.pixelsPerUnit = 40;
  }

  closeLevel() {
    this.world.removeChildren();
    this.level = null;
  }

  update(dt: number) {
    if (!this.level) return;
    dt = Math.min(dt, 0.1);
    if (this.paused) return;
    this.level.update(dt);
    if (this.level.didWin) {
      if (this.levelOrder.length === 0) {
        console.log('Beat game!');
        this.closeLevel();
      } else {
        void this.loadLevel(this.levelOrder.shift()!);
      }
      return;
    }
    if (this.level.didDie) {
      void this.loadLevel(this.level.levelName);
      return;
    }
    Camera.focus = this.level.player.pos;
    if (Input.wasPressed('Digit1')) void this.loadLevel('1');
    if (Input.wasPressed('Digit2')) void this.loadLevel('2');
    if (Input.wasPressed('Digit3')) void this.loadLevel('3');
    if (Input.wasPressed('Digit4')) void this.loadLevel('b');
    Input.update();
  }

  render(ui: PIXI.Container) {
    if (!this.level) return;
    Camera.lookThrough(this.world);
    this.level.render(this.world);
    ui.removeChildren();
    let healthX = 0;
    if (this.level.player.hasKey) {
      this.keySprite.width = this.keySprite.height = 40;
      this.keySprite.x = 0;
      this.keySprite.y = Camera.height - 40;
      ui.addChild(this.keySprite);
      healthX = 45; // offset to avoid overlapping the key icon
    }
    this.healthText.text = `Health: ${this.level.player.health}`;
    this.healthText.x = healthX;
    this.healthText.y = Camera.height - this.healthText.height;
    ui.addChild(this.healthText);
    this.ammoText.text = `Ammo: ${this.level.player.ammo}`;
    this.ammoText.x = Camera.width - this.ammoText.width;
    this.ammoText.y = Camera.height - this.ammoText.height;
    ui.addChild(this.ammoText);
  }
}
