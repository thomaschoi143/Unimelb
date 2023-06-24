/* rpc-client-helper.c
 *
 * Created by Thomas Choi (inchongc@student.unimelb.edu.au)
 * 08/05/2023
 *
 * Helper functions for the client component of the RPC system
 *
 */

#include "rpc-client-helper.h"

#define _POSIX_C_SOURCE 200112L
#include <netdb.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

// Connect client to the server using a IPv6 socket (code from lab 9)
int connect_to_server(char *addr, char *port) {
    int sockfd, s;
    struct addrinfo hints, *servinfo, *rp;

    fprintf(stderr, "Connecting to server on %s:%s\n", addr, port);
    // Create address
    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_INET6;
    hints.ai_socktype = SOCK_STREAM;

    // Get addrinfo of server
    s = getaddrinfo(addr, port, &hints, &servinfo);
    if (s != 0) {
        fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(s));
        return -1;
    }

    // Connect to first valid result
    for (rp = servinfo; rp != NULL; rp = rp->ai_next) {
        sockfd = socket(rp->ai_family, rp->ai_socktype, rp->ai_protocol);
        if (sockfd == -1) {
            continue;
        }

        if (connect(sockfd, rp->ai_addr, rp->ai_addrlen) != -1) {
            break; // success
        }

        close(sockfd);
    }
    freeaddrinfo(servinfo);
    if (rp == NULL) {
        fprintf(stderr, "client: failed to connect\n");
        return -1;
    }
    return sockfd;
}