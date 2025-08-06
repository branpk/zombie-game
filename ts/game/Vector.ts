export class Vector {
  static readonly zeroVector = new Vector(0, 0);

  readonly x: number;
  readonly y: number;

  constructor(x: number, y?: number) {
    if (y === undefined) {
      this.x = Math.cos(x);
      this.y = Math.sin(x);
    } else {
      this.x = x;
      this.y = y;
    }
  }

  angle(): number {
    return Math.atan2(this.y, this.x);
  }

  magnitude(): number {
    return Math.sqrt(this.x * this.x + this.y * this.y);
  }

  direction(): Vector {
    const mag = this.magnitude();
    if (mag === 0) {
      return Vector.zeroVector;
    }
    return this.scale(1 / mag);
  }

  scale(s: number): Vector {
    return new Vector(this.x * s, this.y * s);
  }

  negate(): Vector {
    return new Vector(-this.x, -this.y);
  }

  add(v: Vector): Vector {
    return new Vector(this.x + v.x, this.y + v.y);
  }

  sub(v: Vector): Vector {
    return this.add(v.negate());
  }

  dot(v: Vector): number {
    return this.x * v.x + this.y * v.y;
  }

  rotate(a: number): Vector {
    if (this.equals(Vector.zeroVector)) {
      return Vector.zeroVector;
    }
    return new Vector(a + this.angle()).scale(this.magnitude());
  }

  distance(v: Vector): number {
    return this.sub(v).magnitude();
  }

  perpCCW(): Vector {
    return new Vector(-this.y, this.x);
  }

  perpCW(): Vector {
    return new Vector(this.y, -this.x);
  }

  equals(o: unknown): boolean {
    if (!(o instanceof Vector)) return false;
    return o.x === this.x && o.y === this.y;
  }

  toString(): string {
    return `<${this.x}, ${this.y}>`;
  }
}
