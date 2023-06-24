#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <stdbool.h>
#include <stdint.h>
#include <limits.h>

#define NUM_BYTES_32_BITS 4

 void convert_to_big_endian(int n, uint8_t* bytes) {
    *(uint32_t*) bytes = n;

    uint8_t temp;
    int j = NUM_BYTES_32_BITS - 1;
    for (int i = 0; i < NUM_BYTES_32_BITS / 2 ; i++) {
        temp = bytes[i];
        bytes[i] = bytes[j];
        bytes[j] = temp;
        j--;
    }
}

int main(int argc, char** argv) {
    unsigned int simulation_time = 111;
    uint8_t bytes[NUM_BYTES_32_BITS] = {0, 0, 0, 0};
    convert_to_big_endian(simulation_time, bytes);
    printf("%02x %02x %02x %02x\n", bytes[0], bytes[1], bytes[2], bytes[3]);

    return 0;
}