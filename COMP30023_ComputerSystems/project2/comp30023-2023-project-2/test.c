#include <arpa/inet.h>
#include <limits.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#define _BSD_SOURCE
#include <endian.h>

int main(int argc, char **argv) {
    printf("%d\n", INT_MIN);

    uint8_t buffer[8];
    int64_t data_1 = -100000;
    *(uint64_t *)buffer = data_1;
    printf("Before (Little-endian): %02x %02x %02x %02x %02x %02x %02x %02x\n",
           (uint8_t)buffer[0], (uint8_t)buffer[1], (uint8_t)buffer[2],
           (uint8_t)buffer[3], (uint8_t)buffer[4], (uint8_t)buffer[5],
           (uint8_t)buffer[6], (uint8_t)buffer[7]);
    printf("%ld\n", *(uint64_t *)buffer);

    int n = 1;
    // From
    // https://stackoverflow.com/questions/4181951/how-to-check-whether-a-system-is-big-endian-or-little-endian
    if (*(uint8_t *)&n == 1) {
        // It is Little-edian

        // Inspired by
        // https://stackoverflow.com/questions/40526279/portable-way-of-sending-64-bit-variable-through-posix-socket
        *(uint32_t *)buffer = htonl(data_1 >> 32);
        *(uint32_t *)&buffer[4] = htonl((uint32_t)data_1);
    } else {
        printf("It is Big-edian\n");
    }
    printf("After (Big-edian): %02x %02x %02x %02x %02x %02x %02x %02x\n",
           (uint8_t)buffer[0], (uint8_t)buffer[1], (uint8_t)buffer[2],
           (uint8_t)buffer[3], (uint8_t)buffer[4], (uint8_t)buffer[5],
           (uint8_t)buffer[6], (uint8_t)buffer[7]);

    printf("---server---\n");

    // From
    // https://stackoverflow.com/questions/4181951/how-to-check-whether-a-system-is-big-endian-or-little-endian
    if (*(uint8_t *)&n == 1) {
        uint8_t result_buffer[8];
        // It is Little-edian
        *(uint32_t *)result_buffer = ntohl(*(uint32_t *)&buffer[4]);
        *(uint32_t *)&result_buffer[4] = ntohl(*(uint32_t *)buffer);

        printf("Convert back to Little-edian: %02x %02x %02x %02x %02x %02x "
               "%02x %02x\n",
               (uint8_t)result_buffer[0], (uint8_t)result_buffer[1],
               (uint8_t)result_buffer[2], (uint8_t)result_buffer[3],
               (uint8_t)result_buffer[4], (uint8_t)result_buffer[5],
               (uint8_t)result_buffer[6], (uint8_t)result_buffer[7]);
        printf("Result int = %ld\n", *(uint64_t *)result_buffer);
    } else {
        printf("It is Big-edian\n");
        int64_t data_1 = *(uint64_t *)buffer;
    }

    return 0;
}