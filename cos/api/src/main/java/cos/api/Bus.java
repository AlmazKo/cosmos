package cos.api;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;

import java.util.function.Consumer;

public class Bus {

    private final EventBus bus;
    static DeliveryOptions OPTIONS = new DeliveryOptions().setCodecName("record");

    public Bus(EventBus bus) {
        this.bus = bus;
    }

    <T extends Record> void consume(String name, Consumer<T> handler) {
        bus.localConsumer(name).handler(m -> handler.accept((T) m.body()));
    }

    void publish(String name, Object record) {
        bus.request(name, record, Bus.OPTIONS);
    }

}
