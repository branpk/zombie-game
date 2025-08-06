import { TextureManager } from '../TextureManager.js';
import { Vector } from '../Vector.js';
import { Creature } from '../creature/Creature.js';
import { Player } from '../creature/Player.js';
import { Item } from './Item.js';

export class AmmoPack extends Item {
  constructor(pos: Vector) {
    super(pos, TextureManager.AMMOPACK);
  }

  pickedUpBy(c: Creature): boolean {
    if (c instanceof Player) {
      c.ammo += 5;
      return true;
    }
    return false;
  }
}
