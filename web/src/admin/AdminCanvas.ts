import {CanvasComposer} from '../canvas/CanvasComposer';
import {BasePainter} from '../draw/BasePainter';
import {coord} from '../game2/render/constants';
import {Api} from '../game2/server/Api';
import {TileType} from "../game2/constants";
import {floor} from "../game2/world/World";
import {getColor} from "../game2/render/layers/MiniMapCanvas";

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
    data: TileType[]
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
        if (data.action === "all-creatures") {
            this.crs = data.data as int[];
        } else if (data.action === "world") {
            this.crs = data.data as int[];
            this.map = {
                data: data.data,
                width: data.width, height: data.height, offsetX: data.offsetX, offsetY: data.offsetY
            }
        }
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

        //fixme proportion
        const CELL = (this.width < this.height) ? ((this.width - PAD * 2) / m.width) : ((this.height - PAD * 2) / m.height);
        const crs = this.crs;
        const toX: (pos: coord) => px = (pos: coord) => PAD + pos * CELL;
        const toY: (pos: coord) => px = (pos: coord) => PAD + pos * CELL;

        for (let i = 0; i < m.data.length; i++) {
            const land = m.data[i];
            const x: pos = (i % m.width);
            const y: pos = floor(i / m.width);
            let color = getColor(land);
            p.fillRect(PAD + x * CELL, PAD + y * CELL, CELL + 1, CELL + 1, color)
        }


        for (let pos = 0; pos <= m.width; pos += 16) {
            p.vline(toX(pos), 0, toY(m.height) + PAD, {style: '#22222222'});
            p.text('' + (pos + m.offsetX), toX(pos) + 2, 0, {align: 'left', font: '9px sans-serif'});
        }

        for (let pos = 0; pos <= m.height; pos += 16) {
            p.hline(0, toX(m.width) + +PAD, toY(pos), {style: '#22222222'});
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
                p.rect(toX(x) + 1, toY(y) + 1, CELL - 2, CELL - 2, {style: 'rgb(43,161,44)', width: 2});
            } else if (type == CreatureType.WOLF) {
                npcs++;
                p.rect(toX(x) + 1, toY(y) + 1, CELL - 2, CELL - 2, {style: 'rgba(255,0,4,0.5)', width: 2});
            } else {
                players++;
                p.rect(toX(x + 1), toY(y) + 1, CELL - 2, CELL - 2, {style: 'rgb(0,147,255)', width: 2});
            }
        }

        p.text('Density: ' + (npcs / (width * height) * 100).toFixed(2) + '%', this.width - 120, PAD + 2, {
            align: 'left',
            font: '12px sans-serif',
            baseline: 'top'
        });

        p.text('Npcs: ' + npcs, this.width - 120, PAD + 15, {
            align: 'left',
            font: '12px sans-serif',
            baseline: 'top'
        });
        p.text('Players: ' + players, this.width - 120, PAD + 27, {
            align: 'left',
            font: '12px sans-serif',
            baseline: 'top'
        });
    }
}
