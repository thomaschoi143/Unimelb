/* header file to accompany rpc-client-helper.c */

#ifndef RPC_CLIENT_HELPER_H
#define RPC_CLIENT_HELPER_H

// Connect client to the server using a IPv6 socket
int connect_to_server(char *addr, char *port);

#endif