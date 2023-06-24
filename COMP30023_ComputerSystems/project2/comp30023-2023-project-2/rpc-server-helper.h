/* header file to accompany rpc-server-helper.c */
#ifndef RPC_SERVER_HELPER_H
#define RPC_SERVER_HELPER_H

#include "rpc.h"

#define _POSIX_C_SOURCE 200112L
#define INITIAL_HANDLERS_SIZE 10
#define MAX_NUM_CLIENTS_QUEUE 100

typedef struct {
    char *name;
    rpc_handler handler;
} rpc_handler_info;

typedef struct {
    int sockfd;
    rpc_handler_info **handlers;
    int handlers_len;
} request_handler_arg;

// Create a IPv6 listening socket for the server
int create_listening_socket(char *service);

// Handle any type of request from the client
void *request_handler(void *request_handler_info);

// Check if the handler name exists
int search_handler_name(rpc_handler_info **handlers, int handlers_len,
                        char *name);

#endif