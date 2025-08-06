export class Input {
  private static keysPressed: Set<string> = new Set();
  private static keysDown: Set<string> = new Set();
  private static mousePressed: Set<number> = new Set();
  private static mouseDown: Set<number> = new Set();
  static mouseX = 0;
  static mouseY = 0;

  static initialize(canvas: any) {
    const el = canvas as HTMLElement;
    window.addEventListener('keydown', (e) => {
      if (!this.keysDown.has(e.code)) this.keysPressed.add(e.code);
      this.keysDown.add(e.code);
    });
    window.addEventListener('keyup', (e) => {
      this.keysDown.delete(e.code);
    });
    el.addEventListener('mousedown', (e) => {
      if (!this.mouseDown.has(e.button)) this.mousePressed.add(e.button);
      this.mouseDown.add(e.button);
    });
    el.addEventListener('contextmenu', (e) => e.preventDefault());
    window.addEventListener('mouseup', (e) => {
      this.mouseDown.delete(e.button);
    });
    el.addEventListener('mousemove', (e) => {
      const rect = el.getBoundingClientRect();
      this.mouseX = e.clientX - rect.left;
      this.mouseY = e.clientY - rect.top;
    });
  }

  static update() {
    this.keysPressed.clear();
    this.mousePressed.clear();
  }

  static wasPressed(code: string): boolean {
    return this.keysPressed.has(code);
  }

  static isDown(code: string): boolean {
    return this.keysDown.has(code);
  }

  static mouseWasPressed(button: number): boolean {
    return this.mousePressed.has(button);
  }

  static isMouseDown(button: number): boolean {
    return this.mouseDown.has(button);
  }
}
