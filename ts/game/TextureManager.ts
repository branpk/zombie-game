import * as PIXI from 'pixi.js';

export class TextureManager {
  static PLAYER: PIXI.Texture;
  static ZOMBIE: PIXI.Texture;
  static TURRET: PIXI.Texture;
  static ZOMBIEBODY: PIXI.Texture;
  static TURRETBODY: PIXI.Texture;
  static ZOMBIEHEAD: PIXI.Texture;
  static HEALTHPACK: PIXI.Texture;
  static AMMOPACK: PIXI.Texture;
  static KEY: PIXI.Texture;
  static DOOR: PIXI.Texture;

  static loadTextures() {
    this.PLAYER = PIXI.Texture.from('res/textures/player.png');
    this.ZOMBIE = PIXI.Texture.from('res/textures/zombie.png');
    this.TURRET = PIXI.Texture.from('res/textures/turret.png');
    this.ZOMBIEBODY = PIXI.Texture.from('res/textures/zombiebody.png');
    this.TURRETBODY = PIXI.Texture.from('res/textures/turretbody.png');
    this.ZOMBIEHEAD = PIXI.Texture.from('res/textures/zombiehead.png');
    this.HEALTHPACK = PIXI.Texture.from('res/textures/healthpack.png');
    this.AMMOPACK = PIXI.Texture.from('res/textures/ammopack.png');
    this.KEY = PIXI.Texture.from('res/textures/key.png');
    this.DOOR = PIXI.Texture.from('res/textures/door.png');
  }
}
