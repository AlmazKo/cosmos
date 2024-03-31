export class Chat {
    private chat: HTMLElement;
    private chat_in: HTMLInputElement;

    constructor() {
        this.chat = document.getElementById('chat_history')!;
        this.chat_in = document.getElementById('chat_input') as any;

        document.addEventListener("keyup", event => {
            if (event.code === "Tab") {
                event.stopPropagation();
                return false;
            }
        });

        this.chat_in.addEventListener("keyup", event => {
            console.warn(event)
            if (event.keyCode === 13) {
                this.post(`You says ${this.chat_in.value}`);
                this.chat_in.value = '';
                event.preventDefault();
                this.chat_in.blur();
            }
        });
    }

    post(msg: string) {
        let p: HTMLParagraphElement = document.createElement("p");
        const time = new Date();
        // p.innerHTML = `${dt.format(time)} <a>#${e.id}</a> hits <a>#${e.victimId}</a> for ${e.amount}`
        p.innerHTML = msg
        this.chat.append(p);
        this.chat.scrollTo(0, this.chat.scrollHeight);
    }

}