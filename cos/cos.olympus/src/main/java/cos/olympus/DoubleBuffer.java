package cos.olympus;

import cos.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class DoubleBuffer<T> {

    private final Logger       logger   = new Logger(DoubleBuffer.class).atErrors();
    private final ArrayList<T> first    = new ArrayList<>();
    private final ArrayList<T> second   = new ArrayList<>();
    private       ArrayList<T> consumer = first;

    synchronized public void add(T cmd) {
        consumer.add(cmd);
        logger.info("Add in ${System.identityHashCode(consumer)}, size=" + consumer.size());
    }

    //todo: use compareAndSet
    public synchronized List<T> getAndSwap() {

        ArrayList<T> result;

        if (consumer == first) {
            consumer = second;
            result = first;
        } else {
            consumer = first;
            result = second;
        }
//        logger.info("Now  is ${System.identityHashCode(consumer)}");
        consumer.clear();
        return result;
    }
}
