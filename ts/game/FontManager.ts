import * as PIXI from 'pixi.js';

export class FontManager {
  static DISPLAY = new PIXI.TextStyle({ fontFamily: 'Arial', fontSize: 20, fill: 0xffffff });
  static loadFonts() {
    // Web fonts are loaded via CSS, so nothing to do here.
  }
}
