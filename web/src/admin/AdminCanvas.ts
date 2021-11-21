import {CanvasComposer} from '../canvas/CanvasComposer';
import {BasePainter} from '../draw/BasePainter';
import {coord} from '../game2/render/constants';
import {Api} from '../game2/server/Api';

const PAD = 16;

// const CELL = 12;
// const toX: (pos: coord) => px = (pos: coord) => PAD + pos * CELL;
// const toY: (pos: coord) => px = (pos: coord) => PAD + pos * CELL;


enum CreatureType {
    PLAYER, SHEEP, WOLF
}

interface MapSpec {
    height: int;
    width: int;
    offsetX: int;
    offsetY: int;
}

export class AdminCanvas implements CanvasComposer {
    // @ts-ignore
    height: px;
    // @ts-ignore
    width: px;
    // @ts-ignore
    private p: BasePainter;
    private crs: int[] = [];
    private map: MapSpec = undefined;

    constructor(api: Api) {
        api.listen(p => this.onRawData(p))
    }

    private onRawData(data: any) {
        this.crs = data.data as int[];
        this.map = {width: data.width, height: data.height, offsetX: data.offsetX, offsetY: data.offsetY}
    }

    changeSize(width: px, height: px): void {
        this.width = width;
        this.height = height;
    }

    destroy(): void {
    }

    init(ctx: CanvasRenderingContext2D, width: px, height: px): void {
        this.width = width;
        this.height = height;
        this.p = new BasePainter(ctx);
    }

    onEndFrame(time: DOMHighResTimeStamp, error?: Error): void {
    }

    onFrame(time: DOMHighResTimeStamp, frameId?: uint): void {
        this.p.clearArea(this.width, this.height);
        this.draw();
    }

    private draw() {
        if (!this.map) return;

        const m = this.map;
        const p = this.p;
        const width = m.width - m.offsetX;
        const height = m.height - m.offsetY;
        const CELL = this.width / width;
        const crs = this.crs;

        const toX: (pos: coord) => px = (pos: coord) => PAD + pos * CELL;
        const toY: (pos: coord) => px = (pos: coord) => PAD + pos * CELL;

        for (let pos = 0; pos < width; pos += 16) {
            p.vline(toX(pos), 0, toY(m.height - m.offsetY), {style: '#22222222'});
            p.text('' + (pos + m.offsetX), toX(pos) + 2, 0, {align: 'left', font: '9px sans-serif'});
        }

        for (let pos = 0; pos < height; pos += 16) {
            p.hline(0, this.width + PAD, toY(pos), {style: '#22222222'});
            p.text('' + (pos + m.offsetY), 2, toX(pos) + 2, {align: 'left', font: '9px sans-serif'});
        }

        let npcs = 0;
        let players = 0;

        for (let i = 0; i < crs.length;) {
            let x = crs[i++] - m.offsetX;
            let y = crs[i++] - m.offsetY;
            let type = crs[i++] as CreatureType;

            if (type == CreatureType.SHEEP) {
                npcs++;
                p.fillRect(toX(x), toY(y), CELL, CELL, 'rgb(168,204,155)');
            } else if (type == CreatureType.WOLF) {
                npcs++;
                p.fillRect(toX(x), toY(y), CELL, CELL, 'rgb(255,166,167)');
            } else {
                players++;
                p.fillRect(toX(x), toY(y), CELL, CELL, 'rgb(0,149,255)');
            }
        }

        p.text('Npcs: ' + npcs, 5, toY(height) + PAD + 15, {
            align: 'left',
            font: '12px sans-serif',
            baseline: 'bottom'
        });
        p.text('Players: ' + players, 5, toY(height) + PAD, {
            align: 'left',
            font: '12px sans-serif',
            baseline: 'bottom'
        });
    }
}
