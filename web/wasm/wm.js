async function createWebAssembly(path, importObject) {
    const result = await window.fetch(path);
    const bytes = await result.arrayBuffer();
    return WebAssembly.instantiate(bytes, importObject);
}

const memory = new WebAssembly.Memory({initial: 256, maximum: 256});
let exports = null;
const env = {
    abortStackOverflow: _ => console.error('overflow'),
    table: new WebAssembly.Table({initial: 0, maximum: 0, element: 'anyfunc'}),
    tableBase: 0,
    __table_base: 0,
    memory: memory,
    memoryBase: 1024,
    __memory_base: 1024,
    STACKTOP: 0,
    STACK_MAX: memory.buffer.byteLength,
};

function copyArray() {

}


async function init() {
    const importObject = {env};

    // TODO: do something with importObject
    const wa = await createWebAssembly('hello6.wasm', importObject);
    exports = wa.instance.exports;
    console.info('got exports', exports);
    window.wasm = exports;

    console.log(window.wasm._sum(0, 4));
    window.wasm = {
        grayscale: function (array) {

            new Uint8Array(memory.buffer).set(array, 0);

            exports._grayscale(0, array.length);
            return new Uint8ClampedArray(env.memory.buffer, 0, array.length)
        }
    };
}

init();