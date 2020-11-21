import { Loading } from './util';

export class AudioAssets {

  private data: { [index: string]: AudioBuffer | Loading } = {};

  constructor(
    private readonly host: string,
    private readonly ctx: AudioContext) {

  }

  get(name: string): AudioBuffer | undefined {
    const data = this.data[name];

    if (data instanceof AudioBuffer) {
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

  load(name: string): Promise<AudioBuffer> {
    return this.ajax(`/r/${name}`)
      .then((raw) => new Promise((resolve, reject) =>
        this.ctx.decodeAudioData(
          raw,
          (ab) => resolve(ab),
          (e) => reject("Error with decoding audio data" + e)
        )
      ))
  }


  private ajax(url: string): Promise<ArrayBuffer> {
    return new Promise((resolve, reject) => {
      const xhr = new XMLHttpRequest();
      xhr.responseType = 'arraybuffer';
      xhr.open("GET", this.host + url);
      xhr.onerror = () => {
        reject(url + ': request failed')
      };
      xhr.onload = () => {
        if (xhr.status === 200) {
          resolve(xhr.response)
        } else {
          reject(url + ': request failed');
        }
      };
      xhr.send();
    });
  }
}
