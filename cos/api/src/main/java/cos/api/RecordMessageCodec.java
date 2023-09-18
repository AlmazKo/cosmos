/*
 * Copyright (c) 2011-2019 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package cos.api;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;


public class RecordMessageCodec implements MessageCodec<Record, Record> {

    @Override
    public void encodeToWire(Buffer buffer, Record jsonObject) {
        throw new RuntimeException("TODO: encodeToWire");
    }

    @Override
    public Record decodeFromWire(int pos, Buffer buffer) {
        throw new RuntimeException("TODO: decodeFromWire");
    }

    @Override
    public Record transform(Record jsonObject) {
        return jsonObject;
    }

    @Override
    public String name() {
        return "record";
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
