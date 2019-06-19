#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>


void grayscale( uint8_t *data, const int size) {
    uint8_t avg;

    for (int i = 0; i < size; i += 4) {
        avg = (data[i] + data[i + 1] + data[i + 2]) / 3;
        data[i] = avg; // red
        data[i + 1] = avg; // green
        data[i + 2] = avg; // blue
    }
}


uint8_t sum(const uint8_t *data, const int size) {
//
    int s = 10;
    for (int i = 0; i < size; i += 1) {
        s += data[i];
    }
    return s;
}
