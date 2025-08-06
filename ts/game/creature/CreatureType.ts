import { Creature } from './Creature.js';
import { Player } from './Player.js';
import { Zombie } from './Zombie.js';
import { Turret } from './Turret.js';
import { TurretZombie } from './TurretZombie.js';
import { ZombieTurret } from './ZombieTurret.js';

export enum CreatureType {
  Player,
  Zombie,
  Turret,
  TurretZombie,
  ZombieTurret
}

export function instantiateCreature(t: CreatureType): Creature {
  switch (t) {
    case CreatureType.Player:
      return new Player();
    case CreatureType.Zombie:
      return new Zombie();
    case CreatureType.Turret:
      return new Turret();
    case CreatureType.TurretZombie:
      return new TurretZombie();
    case CreatureType.ZombieTurret:
      return new ZombieTurret();
  }
}
export function getCode(t: CreatureType): number {
  return t;
}

export function fromCode(code: number): CreatureType {
  return code as CreatureType;
}
