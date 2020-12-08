export interface Dictionary<T> {
  [key: string]: T;
}

//rename it
export interface ReadonlyDictionary<T> {
  readonly [key: string]: T;
}

export interface Styles<T> extends Dictionary<Partial<T>> {
  readonly [key: string]: Partial<T>;
}
