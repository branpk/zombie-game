import { Vector } from '../Vector.js';
import { Item } from './Item.js';
import { Key } from './Key.js';
import { HealthPack } from './HealthPack.js';
import { AmmoPack } from './AmmoPack.js';

export enum ItemType {
  Key,
  HealthPack,
  AmmoPack
}

export function instantiateItem(t: ItemType, pos: Vector): Item {
  switch (t) {
    case ItemType.Key:
      return new Key(pos);
    case ItemType.HealthPack:
      return new HealthPack(pos);
    case ItemType.AmmoPack:
      return new AmmoPack(pos);
  }
}

export function getCode(t: ItemType): number { return t; }
export function fromCode(code: number): ItemType { return code as ItemType; }
