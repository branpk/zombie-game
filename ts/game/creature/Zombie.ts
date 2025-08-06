import { TextureManager } from '../TextureManager.js';
import { Vector } from '../Vector.js';
import { AStarPathSearch } from '../level/AStarPathSearch.js';
import { Level } from '../level/Level.js';
import { Creature } from './Creature.js';
import { Player } from './Player.js';

export class Zombie extends Creature {
  protected detectedPlayer = false;
  protected walkSpeed = 40;

  constructor() {
    super(3, TextureManager.ZOMBIE);
  }

  update(dt: number, level: Level) {
    if (!this.detectedPlayer && !level.tileGrid.hitsWall(this.pos, level.player.pos) && level.player.pos.distance(this.pos) <= 10) {
      this.detectedPlayer = true;
    }

    if (this.detectedPlayer && level.player.health > 0) {
      const dest = level.tileGrid.getTile(level.player.pos);
      const path = dest ? new AStarPathSearch(level.tileGrid, this.pos, dest).calculatePath() : null;
      let target: Vector;
      if (path && path.length > 1) {
        const targ1 = path[path.length - 1];
        const targ2 = path[path.length - 2];
        target = new Vector((targ1[1] + targ2[1]) / 2, (targ1[0] + targ2[0]) / 2);
      } else {
        target = level.player.pos;
      }
      const facing = new Vector(this.angle);
      const dir = target.sub(this.pos).dot(facing.perpCCW()) > 0 ? 1 : -1;
      const speed = 2 * (1 - target.sub(this.pos).direction().dot(facing) + 1);
      this.angle += speed * dir * dt;
      this.vel = this.vel.add(new Vector(this.angle).scale(this.walkSpeed * dt));
    }

    super.update(dt, level);
  }

  onTouch(other: Creature) {
    super.onTouch(other);
    if (other instanceof Player) {
      other.takeHit(1, other.pos.sub(this.pos).direction());
    }
  }
}
