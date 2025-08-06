import { Vector } from '../Vector.js';
import { Creature } from '../creature/Creature.js';
import { CreatureType, instantiateCreature, fromCode } from '../creature/CreatureType.js';

export class SpawnChart {
  spawns: SpawnPair[] = [];
}

export class SpawnPair {
  constructor(public pos: Vector, public angle: number, public ctype: CreatureType) {}
  instantiate(): Creature {
    const c = instantiateCreature(this.ctype);
    c.pos = this.pos;
    c.angle = this.angle;
    return c;
  }
}

export function instantiateSpawnChart(chart: SpawnChart): Creature[] {
  return chart.spawns.map((p) => p.instantiate());
}

export function saveSpawnChart(_chart: SpawnChart, _filename: string) {
  console.warn('saveSpawnChart is not supported in the browser');
}

export async function loadSpawnChart(filename: string): Promise<SpawnChart> {
  const res = await fetch(filename);
  const data = (await res.text()).trim().split(/\r?\n/);
  const chart = new SpawnChart();
  const numSpawns = parseInt(data[0], 10);
  for (let i = 1; i <= numSpawns; i++) {
    const [x, y, angle, code] = data[i].split(' ').map(Number);
    chart.spawns.push(new SpawnPair(new Vector(x, y), angle, fromCode(code)));
  }
  return chart;
}
