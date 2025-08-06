import { TextureManager } from '../TextureManager.js';
import { Vector } from '../Vector.js';
import { Creature } from '../creature/Creature.js';
import { Player } from '../creature/Player.js';
import { Item } from './Item.js';

export class HealthPack extends Item {
  constructor(pos: Vector) {
    super(pos, TextureManager.HEALTHPACK);
  }

  pickedUpBy(c: Creature): boolean {
    if (c instanceof Player) {
      c.health += 1;
      return true;
    }
    return false;
  }
}
