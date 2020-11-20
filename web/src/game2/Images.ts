export type asset = string;

export interface Images {
  get(name: asset): HTMLImageElement | undefined
}
