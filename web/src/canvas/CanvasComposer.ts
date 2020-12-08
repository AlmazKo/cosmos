export interface Support {
  shift: Shifted;
  zoom: ZoomChange;
  zoomY: ZoomYChange;
  cursor: CursorChange;
  click: Clickable;
  pressable: Pressable;
}

export interface Shifted {
  shiftChange(shiftX: px, shiftY: px, stop?: boolean): void;

  // shift(shiftX: px, shiftY: px): [px, px];
}

export interface ZoomChange {
  zoomChange(change: float, pos: [px, px] | undefined): void;
}
// fixme delete it
export const ZOOM_Y_AREA_WIDTH: px = 50;

export interface ZoomYChange {
  zoomY(change: float): void;
}

export interface Clickable {
  click(): void;
}

export interface Pressable {
  keydown?(e: KeyboardEvent): void;

  keypress(e: KeyboardEvent): void;

  keyUp?(e: KeyboardEvent): void;
}

export interface CursorChange {
  changeCursorPosition(pos?: [px, px]): void;
}

export interface CanvasComposer {
  register?(): Partial<Support>;

  //todo: rename ti onReadyToRender
  init(ctx: CanvasRenderingContext2D, width: px, height: px): void;

  onFrame(time: DOMHighResTimeStamp, frameId?: uint): void;

  onEndFrame?(time: DOMHighResTimeStamp, error?: Error): void;

  changeSize(width: px, height: px): void;

  destroy?(): void;
}
