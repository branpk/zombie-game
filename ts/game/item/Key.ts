import { TextureManager } from '../TextureManager.js';
import { Vector } from '../Vector.js';
import { Creature } from '../creature/Creature.js';
import { Player } from '../creature/Player.js';
import { Item } from './Item.js';

export class Key extends Item {
  constructor(pos: Vector) {
    super(pos, TextureManager.KEY);
  }

  pickedUpBy(c: Creature): boolean {
    if (c instanceof Player) {
      c.hasKey = true;
      return true;
    }
    return false;
  }
}
