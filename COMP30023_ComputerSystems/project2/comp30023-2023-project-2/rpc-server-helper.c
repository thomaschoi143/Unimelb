/* rpc-server-helper.c
 *
 * Created by Thomas Choi (inchongc@student.unimelb.edu.au)
 * 08/05/2023
 *
 * Helper functions for the server component of the RPC system
 *
 */

#include "rpc-server-helper.h"
#include "rpc-shared-helper.h"
#include "rpc.h"

#define NONBLOCKING
#include <arpa/inet.h>
#include <netdb.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

// Helper functions of request_handler()
void find_request_handler(request_handler_arg *req_info, int32_t length_bytes);
void call_request_handler(request_handler_arg *req_info, int32_t length_bytes);

// Create a IPv6 listening socket for the server (code from lab 9)
int create_listening_socket(char *service) {
    int re, s, sockfd;
    struct addrinfo hints, *res;

    // Create address we're going to listen on (with given port number)
    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_INET6;      // IPv6
    hints.ai_socktype = SOCK_STREAM; // Connection-mode byte streams
    hints.ai_flags = AI_PASSIVE;     // for bind, listen, accept
    // node (NULL means any interface), service (port), hints, res
    s = getaddrinfo(NULL, service, &hints, &res);
    if (s != 0) {
        fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(s));
        return -1;
    }
    // Create socket
    sockfd = socket(res->ai_family, res->ai_socktype, res->ai_protocol);
    if (sockfd < 0) {
        perror("socket");
        return -1;
    }

    // Reuse port if possible
    re = 1;
    if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &re, sizeof(int)) < 0) {
        perror("setsockopt");
        return -1;
    }
    // Bind address to the socket
    if (bind(sockfd, res->ai_addr, res->ai_addrlen) < 0) {
        perror("bind");
        return -1;
    }
    freeaddrinfo(res);

    return sockfd;
}

// Handle any type of request from the client
void *request_handler(void *req_info_void) {
    request_handler_arg *req_info = (request_handler_arg *)req_info_void;
    char command;
    int32_t length_bytes = get_prefix(req_info->sockfd, &command);
    if (length_bytes < 0) {
        fprintf(stderr, "Client closed the connection\n");
        goto close_socket;
    }
    if (command == FIND_CODE) {
        find_request_handler(req_info, length_bytes);
    } else if (command == CALL_CODE) {
        call_request_handler(req_info, length_bytes);
    }

close_socket:
    close(req_info->sockfd);
    fprintf(stderr, "Socket %d closed\n", req_info->sockfd);
    free(req_info);
    pthread_exit(NULL);
}

// Handle a find request from the client
void find_request_handler(request_handler_arg *req_info, int32_t length_bytes) {
    // Read handler name
    char handler_name[length_bytes];
    if (read_to_buffer((uint8_t *)handler_name, length_bytes,
                       req_info->sockfd) < 0) {
        return;
    }

    int handler_index = search_handler_name(
        req_info->handlers, req_info->handlers_len, handler_name);
    if (handler_index < 0) {
        send_prefix(req_info->sockfd, 0, ERROR_CODE);
        return;
    }

    // Respond the handler index
    char buffer[4];
    *(uint32_t *)buffer = htonl((uint32_t)handler_index);
    if (send_prefix(req_info->sockfd, 4, HANDLER_INDEX_CODE) < 0) {
        return;
    }
    if (write(req_info->sockfd, buffer, 4) < 0) {
        perror("write");
        return;
    }
}

// Handle a call request from the client
void call_request_handler(request_handler_arg *req_info, int32_t length_bytes) {
    // Read handler_index and input payload
    uint8_t read_buffer[length_bytes];
    if (read_to_buffer(read_buffer, length_bytes, req_info->sockfd) < 0) {
        return;
    }

    uint32_t handler_index = ntohl(*(uint32_t *)read_buffer);
    if (handler_index < 0 || handler_index >= req_info->handlers_len) {
        fprintf(stderr, "Invalid handler_index\n");
        send_prefix(req_info->sockfd, 0, ERROR_CODE);
        return;
    }
    rpc_handler handler = (req_info->handlers)[handler_index]->handler;
    rpc_data *input_payload = read_payload_from_buffer(
        &read_buffer[4], length_bytes - sizeof(uint32_t) - sizeof(int64_t));
    if (input_payload == NULL) {
        return;
    }

    // Prepare response
    rpc_data *output_payload = handler(input_payload);
    rpc_data_free(input_payload);
    if (output_payload == NULL) {
        send_prefix(req_info->sockfd, 0, RESPONSE_CODE);
        return;
    }
    length_bytes = sizeof(int64_t) + output_payload->data2_len;
    uint8_t send_buffer[length_bytes];

    if (check_payload(output_payload) < 0) {
        send_prefix(req_info->sockfd, 0, RESPONSE_CODE);
        goto cleanup;
    }

    prepare_payload_send_buffer(send_buffer, output_payload);

    if (send_prefix(req_info->sockfd, length_bytes, RESPONSE_CODE) < 0) {
        goto cleanup;
    }

    if (write(req_info->sockfd, send_buffer, length_bytes) < 0) {
        perror("write");
        goto cleanup;
    }
cleanup:
    rpc_data_free(output_payload);
    return;
}

// Check if the handler name exists
int search_handler_name(rpc_handler_info **handlers, int handlers_len,
                        char *name) {
    for (int i = 0; i < handlers_len; i++) {
        if (strcmp(handlers[i]->name, name) == 0) {
            return i;
        }
    }
    return -1;
}