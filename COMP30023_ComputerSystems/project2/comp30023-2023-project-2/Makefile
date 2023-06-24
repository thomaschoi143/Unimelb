CC=gcc
CFLAGS=-Wall -g
RPC_SYSTEM=rpc.o
RPC_SYSTEM_A=rpc.a
OBJ=rpc-server-helper.o rpc-client-helper.o rpc-shared-helper.o
SERVER_EXE=rpc-server
CLIENT_EXE=rpc-client
LDFLAGS=-L. -l:rpc.a
.PHONY: format all

all:  $(OBJ) $(RPC_SYSTEM) $(RPC_SYSTEM_A) $(SERVER_EXE) $(CLIENT_EXE)

$(SERVER_EXE): server.c 
	$(CC) $(CFLAGS) -o $(SERVER_EXE) $< $(LDFLAGS)

$(CLIENT_EXE): client.c
	$(CC) $(CFLAGS) -o $(CLIENT_EXE) $< $(LDFLAGS)

$(RPC_SYSTEM): rpc.c rpc.h
	$(CC) $(CFLAGS) -c -o $@ $< -lpthread

rpc-server-helper.o: rpc-server-helper.c rpc-server-helper.h
	$(CC) $(CFLAGS) -c -o $@ $< -lpthread
	
%.o: %.c %.h
	$(CC) $(CFLAGS) -c -o $@ $<

$(RPC_SYSTEM_A): $(RPC_SYSTEM) $(OBJ)
	ar rcs $(RPC_SYSTEM_A) $(RPC_SYSTEM) $(OBJ)

clean: 
	rm -f *.a *.o $(SERVER_EXE) $(CLIENT_EXE)

format:
	clang-format -style=file -i *.c *.h
