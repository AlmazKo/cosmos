///*
//package cos.olympus.game.server
//
//import cos.logging.Logger
//import cos.olympus.DoubleBuffer
//import cos.olympus.game.Op
//import cos.olympus.ops.AnyOp
//import cos.olympus.ops.Login
//import cos.olympus.ops.Move
//import java.lang.Thread.sleep
//import java.net.InetSocketAddress
//import java.net.StandardSocketOptions
//import java.nio.ByteBuffer
//import java.nio.channels.SelectionKey
//import java.nio.channels.Selector
//import java.nio.channels.ServerSocketChannel
//import java.nio.channels.SocketChannel
//import kotlin.concurrent.thread
//
//
//class GameServer(private val actionsBuffer: DoubleBuffer<AnyOp>) {
//    @Volatile private var running = true
//    @Volatile var id = 0
//
//
//    private val logger = Logger(javaClass)
//        .atErrors()
//
//    fun start(port: Int) {
//
//        val selector = Selector.open()
//        val socket = ServerSocketChannel.open()
//
//
//
//
//        socket.configureBlocking(false)
//        //set some options
//        socket.setOption(StandardSocketOptions.SO_RCVBUF, 256 * 1024);
//        socket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
//        socket.bind(InetSocketAddress(6666))
//        socket.register(selector, SelectionKey.OP_ACCEPT)
//        //        log.atInfo().log("Waiting for connections ...");
//
//        while (running) {
//            logger.info(Thread.currentThread().name + " i'm a server and i'm waiting for new connection and buffer select...")
//            // Selects a set of keys whose corresponding channels are ready for I/O operations
//            selector.select()
//            // token representing the registration of a SelectableChannel with a Selector
//
//            val keys = selector.selectedKeys().iterator()
//
//            while (keys.hasNext()) {
//                val key = keys.next()
//                keys.remove()
//                if (!key.isValid) continue
//
//                logger.info(Thread.currentThread().name + " next: $key")
//                logger.info(
//                    Thread.currentThread()
//                        .name + " isAcceptable=${key.isAcceptable} isReadable=${key.isReadable}  isConnectable=${key.isConnectable}  isValid=${key.isValid}  isWritable=${key.isWritable}"
//                )
//                // Tests whether this key's channel is ready to accept a new socket connection
//                if (key.isAcceptable) {
//                    val client = socket.accept()
//                    // Adjusts this channel's blocking mode to false
//                    client.configureBlocking(false)
//                    // Operation-set bit for read operations
//                    client.register(selector, SelectionKey.OP_READ)
//                    logger.info(Thread.currentThread().name + " Connection Accepted: " + client.remoteAddress)
//                    // Tests whether this key's channel is ready for reading
//                } else if (key.isReadable) {
//                    val client = key.channel() as SocketChannel
//                    val buf = ByteBuffer.allocate(32)
//                    client.read(buf)
//
//
//
//                    if (buf.get(0) == Op.NOPE) {
//                        logger.info(Thread.currentThread().name + " No Data, close it")
//                        client.close()
//                        continue
//                    }
//
//
//                    //                    logger.info(Arrays.toString(buf.array()))
//                    while (buf.remaining() > 0) {
//                        //                        bb.getByte()
//                        val op = parseOp(buf)
//                        logger.info("Op:  $op")
//                        if (op !== null) {
//                            actionsBuffer.add(op)
//                        }
//                    }
//
//
//                }
//
//            }
//            sleep(300)
//        }
//
//    }
//
//
//    private fun parseOp(b: ByteBuffer) = when (b.get()) {
//        Op.LOGIN -> Login(b)
//        Op.MOVE -> Move(b)
//        Op.FINISH -> null
//        else -> null
//    }
//
//    private fun log(msg: String) {
//        logger.info(Thread.currentThread().name + " " + msg)
//    }
//
//
//    companion object {
//        fun run(actionsBuffer: DoubleBuffer<AnyOp>) {
//            thread(name = "GameServer") {
//                val server = GameServer(actionsBuffer)
//                server.start(6666)
//            }
//        }
//    }
//}
//*/
