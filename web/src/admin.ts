import {AdminCanvas} from './admin/AdminCanvas';
import {Player} from './canvas/Player';
import {get} from "./Module";

window.onload = () => {
    let div = document.getElementById("game")!!;
    console.info(div);

    let p = new Player(div);
    const gc = get(AdminCanvas);
    p.start(gc);
};
