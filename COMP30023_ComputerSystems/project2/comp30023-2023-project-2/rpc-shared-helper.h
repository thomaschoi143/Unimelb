/* header file to accompany rpc-shared-helper.c */
#ifndef RPC_SHARED_HELPER_H
#define RPC_SHARED_HELPER_H

#include "rpc.h"
#include <stdint.h>

#define MAX_data2_len 100000
#define PREFIX_SIZE 5
#define MAX_HANDLER_NAME_NUM_CHAR 1000
#define MIN_HANDLER_NAME_NUM_CHAR 0
#define HANDLER_VALID_NAME_LOWER 32
#define HANDLER_VALID_NAME_UPPER 126
#define ERROR_CODE 'E'
#define RESPONSE_CODE 'R'
#define HANDLER_INDEX_CODE 'H'
#define CALL_CODE 'C'
#define FIND_CODE 'F'

// Check if the handler name is valid
int check_handler_name(char *name);

// Send the prefix to the other end
int send_prefix(int sockfd, int32_t length_bytes, char command);

// Read the prefix from the other end, return length_bytes
int32_t get_prefix(int sockfd, char *command);

// Read the required bytes and store them in the read buffer
int read_to_buffer(uint8_t *read_buffer, int32_t length_bytes, int sockfd);

// Read payload from the buffer and return a rpc_data*
rpc_data *read_payload_from_buffer(uint8_t *buffer, size_t data2_len);

// Store the payload into the send buffer
void prepare_payload_send_buffer(uint8_t *send_buffer, rpc_data *payload);

// Check all the fields in payload are valid
int check_payload(rpc_data *payload);

#endif