import { asset } from '../render/Images';
import { AudioAssets } from '../server/AudioAssets';

export class Audios {

  constructor(
    private readonly ctx: AudioContext,
    private readonly assets: AudioAssets,
  ) {

  }


  public play(audio: asset | undefined) {
    if (!audio) return;

    const buffer = this.assets.get(audio);
    if (!buffer) return;
    const source = this.ctx.createBufferSource();
    source.buffer = buffer;
    source.playbackRate.value = 1.0;
    source.connect(this.ctx.destination);
    source.start(0);

  }

}
