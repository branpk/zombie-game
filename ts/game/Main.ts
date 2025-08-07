import * as PIXI from 'pixi.js';
import { Camera } from './Camera.js';
import { FontManager } from './FontManager.js';
import { Game } from './Game.js';
import { Input } from './Input.js';
import { TextureManager } from './TextureManager.js';
import { Timer } from './Timer.js';

const levelOrder = ['1', '2', '3', 'b'];

const app = new PIXI.Application({ width: 600, height: 500, backgroundColor: 0x000000 });
document.body.appendChild(app.view as HTMLCanvasElement);

const info = document.createElement('div');
info.id = 'info';
info.innerHTML = `
  <p><a href="https://github.com/brandonpickering/zombie-game">Source on GitHub</a></p>
  <h2>Controls</h2>
  <ul>
    <li>ASDW to move.</li>
    <li>Left click to shoot.</li>
    <li>Right click or space to stab.</li>
    <li><b>Debug:</b> Press 1-4 to switch level.</li>
  </ul>
`;
document.body.appendChild(info);

Camera.init(app);
TextureManager.loadTextures();
FontManager.loadFonts();
Input.initialize(app.view);

const world = new PIXI.Container();
const ui = new PIXI.Container();
app.stage.addChild(world);
app.stage.addChild(ui);

const game = new Game(levelOrder, world);

let instructions = true;
const instTimer = new Timer();
const msgs = ['ASDW to move.', 'Left click to shoot.', 'Right click to stab.', 'Good luck.'];
const texts = msgs.map(m => new PIXI.Text(m, FontManager.DISPLAY));
texts.forEach(t => { t.anchor.set(0.5); t.y = Camera.height / 2; });

app.ticker.add(() => {
  const dt = Math.min(app.ticker.deltaMS / 1000, 0.1);
  game.update(dt);
  game.render(ui);
  if (instructions) {
    const secs = instTimer.elapsed().inSecs();
    const centerX = Camera.width / 2;
    texts[0].x = centerX; texts[0].y = Camera.height/2 - 1*texts[0].height; if (secs > 1) ui.addChild(texts[0]);
    texts[1].x = centerX; texts[1].y = Camera.height/2 + 0*texts[1].height; if (secs > 2) ui.addChild(texts[1]);
    texts[2].x = centerX; texts[2].y = Camera.height/2 + 1*texts[2].height; if (secs > 3) ui.addChild(texts[2]);
    texts[3].x = centerX; texts[3].y = Camera.height/2 + 2*texts[3].height; if (secs > 4) ui.addChild(texts[3]);
    if (secs > 5) { instructions = false; texts.forEach(t => ui.removeChild(t)); }
  }
});
