import { Vector } from '../Vector.js';
import { Item } from '../item/Item.js';
import { ItemType, instantiateItem, fromCode } from '../item/ItemType.js';

export class ItemChart {
  spawns: SpawnPair[] = [];
}

export class SpawnPair {
  constructor(public pos: Vector, public itype: ItemType) {}
  instantiate(): Item {
    return instantiateItem(this.itype, this.pos);
  }
}

export function instantiateItemChart(chart: ItemChart): Item[] {
  return chart.spawns.map((s) => s.instantiate());
}

export function saveItemChart(_chart: ItemChart, _filename: string) {
  console.warn('saveItemChart is not supported in the browser');
}

export async function loadItemChart(filename: string): Promise<ItemChart> {
  const res = await fetch(filename);
  const data = (await res.text()).trim().split(/\r?\n/);
  const chart = new ItemChart();
  const num = parseInt(data[0], 10);
  for (let i = 1; i <= num; i++) {
    const [x, y, code] = data[i].split(' ').map(Number);
    chart.spawns.push(new SpawnPair(new Vector(x, y), fromCode(code)));
  }
  return chart;
}
