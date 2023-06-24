/* rpc.c
 *
 * Created by Thomas Choi (inchongc@student.unimelb.edu.au)
 * 08/05/2023
 *
 * The implementation of the RPC system
 * This file consists of 3 parts: Server Component, Client Component and
 * Shared Component
 *
 */

#include "rpc.h"
#include "rpc-client-helper.h"
#include "rpc-server-helper.h"
#include "rpc-shared-helper.h"

#define _DEFAULT_SOURCE
#include <arpa/inet.h>
#include <endian.h>
#include <netdb.h>
#include <pthread.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

/* Server Component */

struct rpc_server {
    int sockfd;
    rpc_handler_info **handlers;
    int handlers_len;
    int handlers_size;
};

// Set up a listening socket and return the state of the server
rpc_server *rpc_init_server(int port) {
    rpc_server *srv = malloc(sizeof(*srv));
    if (!srv) {
        fprintf(stderr, "Failed to malloc srv\n");
        return NULL;
    }

    int n = snprintf(NULL, 0, "%d", port);
    char service[n + 1];
    snprintf(service, n + 1, "%d", port);

    srv->sockfd = create_listening_socket(service);
    if (srv->sockfd == -1) {
        free(srv);
        return NULL;
    }

    srv->handlers_size = INITIAL_HANDLERS_SIZE;
    srv->handlers_len = 0;
    srv->handlers = malloc(srv->handlers_size * sizeof(*(srv->handlers)));
    if (!srv->handlers) {
        free(srv);
        fprintf(stderr, "Failed to malloc srv->handlers\n");
        return NULL;
    }

    return srv;
}

// Register the handler and its name to the server
int rpc_register(rpc_server *srv, char *name, rpc_handler handler) {
    if (srv == NULL || name == NULL || handler == NULL) {
        fprintf(stderr, "One of arguments in rpc_register is NULL\n");
        return -1;
    }

    // Search for the handler name
    int handler_index =
        search_handler_name(srv->handlers, srv->handlers_len, name);
    if (handler_index >= 0) {
        srv->handlers[handler_index]->handler = handler;
        return 1;
    }

    // Check if the name is valid
    if (check_handler_name(name) == -1) {
        fprintf(stderr, "Invalid handler name\n");
        return -1;
    }

    // Valid handler name, add the handler
    if (srv->handlers_len == srv->handlers_size) {
        srv->handlers_size *= 2;
        srv->handlers = realloc(srv->handlers,
                                srv->handlers_size * sizeof(*(srv->handlers)));
        if (!srv->handlers) {
            fprintf(stderr, "Failed to realloc srv->handlers\n");
            return -1;
        }
    }
    srv->handlers[srv->handlers_len] = malloc(sizeof(rpc_handler_info));
    if (!srv->handlers[srv->handlers_len]) {
        fprintf(stderr, "Failed to malloc rpc_handler_info\n");
        return -1;
    }
    srv->handlers[srv->handlers_len]->name =
        malloc((strlen(name) + 1) * sizeof(char));
    if (!srv->handlers[srv->handlers_len]->name) {
        free(srv->handlers[srv->handlers_len]);
        fprintf(stderr, "Failed to malloc handler name in srv->handlers\n");
        return -1;
    }
    strcpy(srv->handlers[srv->handlers_len]->name, name);

    srv->handlers[srv->handlers_len]->handler = handler;
    srv->handlers_len++;

    return 1;
}

// Start the server and handle clients' requests
void rpc_serve_all(rpc_server *srv) {
    if (srv == NULL) {
        fprintf(stderr, "NULL srv error\n");
        return;
    }
    if (listen(srv->sockfd, MAX_NUM_CLIENTS_QUEUE) < 0) {
        perror("listen");
        return;
    }
    fprintf(stderr, "Server started\n");

    int newsockfd, port;
    struct sockaddr_in client_addr;
    socklen_t client_addr_size;
    client_addr_size = sizeof(client_addr);

    while (1) {
        newsockfd = accept(srv->sockfd, (struct sockaddr *)&client_addr,
                           &client_addr_size);
        if (newsockfd < 0) {
            perror("accept");
            continue;
        }
        char ip[INET_ADDRSTRLEN];
        getpeername(newsockfd, (struct sockaddr *)&client_addr,
                    &client_addr_size);
        inet_ntop(client_addr.sin_family, &client_addr.sin_addr, ip,
                  INET_ADDRSTRLEN);
        port = ntohs(client_addr.sin_port);
        fprintf(stderr, "New connection from %s:%d on socket %d\n", ip, port,
                newsockfd);

        pthread_t tid;
        request_handler_arg *req_info = malloc(sizeof(*req_info));
        if (!req_info) {
            fprintf(stderr, "Failed to malloc req_handler_info\n");
            continue;
        }
        req_info->sockfd = newsockfd;
        req_info->handlers = srv->handlers;
        req_info->handlers_len = srv->handlers_len;

        if (pthread_create(&tid, NULL, request_handler, (void *)req_info) !=
            0) {
            free(req_info);
            close(newsockfd);
            fprintf(stderr, "Failed to create thread\n");
            continue;
        }
        if (pthread_detach(tid) != 0) {
            free(req_info);
            close(newsockfd);
            fprintf(stderr, "Failed to detach thread\n");
            continue;
        }
        fprintf(stderr, "Created thread for socket %d\n", newsockfd);
    }
    close(srv->sockfd);
}

/* Client Component */

struct rpc_client {
    char *port;
    char *addr;
};

struct rpc_handle {
    uint32_t handler_index;
};

// Return the client state containing the target server's address and port
rpc_client *rpc_init_client(char *addr, int port) {
    rpc_client *cl = malloc(sizeof(*cl));
    if (!cl) {
        fprintf(stderr, "Failed to malloc cl\n");
        return NULL;
    }

    int n = snprintf(NULL, 0, "%d", port);
    cl->port = malloc((n + 1) * sizeof(char));
    if (!cl->port) {
        free(cl);
        fprintf(stderr, "Failed to malloc cl->port\n");
        return NULL;
    }
    snprintf(cl->port, n + 1, "%d", port);

    cl->addr = malloc((strlen(addr) + 1) * sizeof(char));
    if (!cl->addr) {
        free(cl->port);
        free(cl);
        fprintf(stderr, "Failed to malloc cl->addr\n");
        return NULL;
    }
    strcpy(cl->addr, addr);

    return cl;
}

// Make a find request
rpc_handle *rpc_find(rpc_client *cl, char *name) {
    if (cl == NULL || name == NULL) {
        return NULL;
    }
    if (check_handler_name(name) == -1) {
        fprintf(stderr, "Invalid handler name\n");
        return NULL;
    }
    fprintf(stderr, "---Made a find request\n");
    int sockfd = connect_to_server(cl->addr, cl->port);
    if (sockfd < 0) {
        return NULL;
    }
    int32_t length_bytes = strlen(name) + 1;
    if (send_prefix(sockfd, length_bytes, FIND_CODE) < 0) {
        close(sockfd);
        return NULL;
    }

    // Send search name to server
    int n = write(sockfd, name, length_bytes);
    if (n < 0) {
        close(sockfd);
        perror("write");
        return NULL;
    }

    // Read the handler index response from server
    char response_type;
    int bytes_need_to_read = sizeof(uint32_t);
    if ((get_prefix(sockfd, &response_type)) < 0) {
        fprintf(stderr, "Server closed the connection\n");
        close(sockfd);
        return NULL;
    }
    if (response_type == ERROR_CODE) {
        fprintf(stderr, "Handler name not exist\n");
        close(sockfd);
        return NULL;
    }

    uint8_t response[bytes_need_to_read];
    if (read_to_buffer(response, bytes_need_to_read, sockfd) < 0) {
        close(sockfd);
        return NULL;
    }
    close(sockfd);

    uint32_t handler_index = ntohl(*(uint32_t *)response);

    rpc_handle *handle = malloc(sizeof(*handle));
    if (!handle) {
        fprintf(stderr, "Failed to malloc handle\n");
        return NULL;
    }
    handle->handler_index = handler_index;

    return handle;
}

// Make a call request
rpc_data *rpc_call(rpc_client *cl, rpc_handle *h, rpc_data *payload) {
    int sockfd, n;
    if (cl == NULL || h == NULL || payload == NULL) {
        return NULL;
    }
    if (check_payload(payload) < 0) {
        return NULL;
    }

    fprintf(stderr, "---Made a call request\n");
    if ((sockfd = connect_to_server(cl->addr, cl->port)) < 0) {
        return NULL;
    }

    int32_t length_bytes =
        sizeof(h->handler_index) + sizeof(int64_t) + payload->data2_len;
    // Prepare the send buffer
    uint8_t send_buffer[length_bytes];

    // Copy handler_index to send_buffer
    *(uint32_t *)send_buffer = htonl(h->handler_index);

    // Copy payload data to send_buffer
    prepare_payload_send_buffer(&send_buffer[4], payload);

    if (send_prefix(sockfd, length_bytes, CALL_CODE) < 0) {
        close(sockfd);
        return NULL;
    }

    n = write(sockfd, send_buffer, length_bytes);
    if (n < 0) {
        close(sockfd);
        perror("write");
        return NULL;
    }

    // Get response
    char command;
    int length_bytes_to_read = get_prefix(sockfd, &command);
    if (length_bytes_to_read < 0) {
        fprintf(stderr, "Server closed the connection\n");
        close(sockfd);
        return NULL;
    }
    if (command == ERROR_CODE) {
        fprintf(stderr, "Invalid handle\n");
        close(sockfd);
        return NULL;
    }
    if (length_bytes_to_read == 0 && command == RESPONSE_CODE) {
        fprintf(stderr, "NULL or invalid response payload from server\n");
        close(sockfd);
        return NULL;
    }

    uint8_t response_payload_buffer[length_bytes_to_read];
    if (read_to_buffer(response_payload_buffer, length_bytes_to_read, sockfd) <
        0) {
        close(sockfd);
        return NULL;
    }
    close(sockfd);

    rpc_data *response_payload = read_payload_from_buffer(
        response_payload_buffer, length_bytes_to_read - sizeof(int64_t));

    return response_payload;
}

// Free the client state
void rpc_close_client(rpc_client *cl) {
    if (cl == NULL) {
        return;
    }
    if (cl->port) {
        free(cl->port);
    }
    cl->port = NULL;
    if (cl->addr) {
        free(cl->addr);
    }
    cl->addr = NULL;

    free(cl);
}

/* Shared Component */
// Free the rpc_data*
void rpc_data_free(rpc_data *data) {
    if (data == NULL) {
        return;
    }
    if (data->data2 != NULL) {
        free(data->data2);
    }
    free(data);
}
