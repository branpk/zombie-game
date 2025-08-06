export class Timer {
  private startTime = 0;
  constructor() { this.restart(); }
  restart() { this.startTime = performance.now(); }
  elapsed(): DeltaTime { return new DeltaTime(performance.now() - this.startTime); }
  tick(): DeltaTime { const dt = this.elapsed(); this.restart(); return dt; }
}

export class DeltaTime {
  constructor(private millis: number) {}
  inNanos(): number { return this.millis * 1e6; }
  inMicros(): number { return this.millis * 1e3; }
  inMillis(): number { return this.millis; }
  inSecs(): number { return this.millis / 1000; }
}
