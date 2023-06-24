/* rpc-shared-helper.c
 *
 * Created by Thomas Choi (inchongc@student.unimelb.edu.au)
 * 08/05/2023
 *
 * Helper functions for both the server and client components of the RPC system
 *
 */

#include "rpc-shared-helper.h"
#include "rpc.h"

#include <arpa/inet.h>
#include <endian.h>
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

// Check if the handler name is valid
int check_handler_name(char *name) {
    int str_len = strlen(name);
    if (str_len <= MIN_HANDLER_NAME_NUM_CHAR ||
        str_len > MAX_HANDLER_NAME_NUM_CHAR) {
        return -1;
    }
    // Not accept invalid name
    for (int i = 0; i < strlen(name); i++) {
        if (name[i] < HANDLER_VALID_NAME_LOWER ||
            name[i] > HANDLER_VALID_NAME_UPPER) {
            return -1;
        }
    }
    return 1;
}

// Send the prefix to the other end
int send_prefix(int sockfd, int32_t length_bytes, char command) {
    char prefix_buffer[PREFIX_SIZE];
    prefix_buffer[0] = command;
    *(int32_t *)&prefix_buffer[1] = htonl(length_bytes);

    int n = write(sockfd, prefix_buffer, PREFIX_SIZE);
    if (n < 0) {
        perror("write");
    }
    return n;
}

// Read the prefix from the other end, return length_bytes
int32_t get_prefix(int sockfd, char *command) {
    char prefix_buffer[PREFIX_SIZE];
    if (read_to_buffer((uint8_t *)prefix_buffer, PREFIX_SIZE, sockfd) < 0) {
        return -1;
    }

    *command = prefix_buffer[0];
    int32_t length_bytes = ntohl(*(int32_t *)&prefix_buffer[1]);
    return length_bytes;
}

// Read the required bytes and store them in the read buffer
int read_to_buffer(uint8_t *read_buffer, int32_t length_bytes, int sockfd) {
    int bytes_had_read = 0, n;
    while (bytes_had_read < length_bytes) {
        n = read(sockfd, read_buffer + bytes_had_read,
                 length_bytes - bytes_had_read);
        if (n < 0) {
            perror("read");
            return -1;
        } else if (n == 0) {
            fprintf(stderr, "Socket %d is closed\n", sockfd);
            return -1;
        }
        bytes_had_read += n;
    }
    return bytes_had_read;
}

// Read payload from the buffer and return a rpc_data*
rpc_data *read_payload_from_buffer(uint8_t *buffer, size_t data2_len) {
    int64_t data1_64bit = be64toh(*(int64_t *)buffer);
    if (data1_64bit > INT_MAX || data1_64bit < INT_MIN) {
        fprintf(stderr, "Overlength error\n");
        return NULL;
    }
    rpc_data *payload = malloc(sizeof(*payload));
    if (!payload) {
        fprintf(stderr, "Failed to malloc payload\n");
        return NULL;
    }
    payload->data1 = data1_64bit;
    payload->data2_len = data2_len;
    if (payload->data2_len == 0) {
        payload->data2 = NULL;
    } else {
        payload->data2 = malloc(payload->data2_len);
        if (!payload->data2) {
            rpc_data_free(payload);
            fprintf(stderr, "Failed to malloc payload->data2\n");
            return NULL;
        }
        memcpy(payload->data2, &buffer[sizeof(int64_t)], payload->data2_len);
    }
    return payload;
}

// Store the payload into the send buffer
void prepare_payload_send_buffer(uint8_t *send_buffer, rpc_data *payload) {
    int64_t data1 = (int64_t)payload->data1;
    *(int64_t *)send_buffer = htobe64(data1);
    memcpy(&send_buffer[8], payload->data2, payload->data2_len);
}

// Check all the fields in payload are valid
int check_payload(rpc_data *payload) {
    if (payload->data2_len >= MAX_data2_len || payload->data1 < INT64_MIN ||
        payload->data1 > INT64_MAX) {
        fprintf(stderr, "Overlength error\n");
        return -1;
    }
    if ((payload->data2_len == 0 && payload->data2 != NULL) ||
        (payload->data2_len != 0 && payload->data2 == NULL)) {
        fprintf(stderr, "Incorrect combination of data2_len/data2\n");
        return -1;
    }
    return 1;
}