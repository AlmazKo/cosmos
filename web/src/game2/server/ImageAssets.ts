import { Images } from '../render/Images';

const basicResources: string[] = [
  // todo characters & spell images
];

enum Loading {
  REQUESTING, FAIL
}


export class ImageAssets implements Images {

  private data: { [index: string]: HTMLImageElement | Loading } = {};

  constructor(private readonly host: string) {

  }

  get(name: string): HTMLImageElement | undefined {
    const data = this.data[name];

    if (data instanceof HTMLImageElement) {
      return data;
    } else {
      if (data === undefined) {
        this.data[name] = Loading.REQUESTING;
        this.load(name)
          .then(i => this.data[name] = i)
          .catch(() => this.data[name] = Loading.FAIL)
      }
      return undefined;
    }
  }

  load(name: string): Promise<HTMLImageElement> {
    return new Promise((resolve, reject) => {
      let img = new Image();
      img.crossOrigin = "Anonymous";
      img.src = `${this.host}/r/${name}.png`;
      img.onerror = reject;
      img.onload = () => {
        resolve(img)
      };
    });
  }
}


