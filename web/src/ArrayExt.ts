interface Array<T> {
//   isEmpty(): boolean;
//
//   first(): T | undefined;
//
//   last(): T | undefined;
//
//   contains(e: T): boolean;
//
//   remove(e: T): boolean;
//
  removeIf(filter: (e: T) => boolean): void;
}

Array.prototype.isEmpty = function<T>(): boolean {
  return this.length === 0;
};

Array.prototype.first = function<T>(): T {
  return this[0];
};

Array.prototype.last = function<T>(): T {
  return this[this.length - 1];
};

Array.prototype.contains = function<T>(e: T) {
  return this.indexOf(e) !== -1;
};

Array.prototype.remove = function<T>(e: T) {
  const idx = this.indexOf(e);
  if (idx < 0) return false;

  delete this[idx];
  return true;
};

Array.prototype.removeIf = function<T>(filter: (e: T) => boolean) {
  for (let i = 0; i < this.length; i++) {
    if (filter(this[i])) {
      this.splice(i, 1);
    }
  }
};
