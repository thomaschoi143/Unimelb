CC=gcc
CFLAGS=-Wall -g
EXE=allocate
OBJ=processQueue.o input.o processManager.o realProcess.o

$(EXE): main.c $(OBJ)
	$(CC) $(CFLAGS) -o $(EXE) $< $(OBJ) -lm

%.o: %.c %.h
	$(CC) $(CFLAGS) -c -o $@ $<

clean: 
	rm -f *.o $(EXE)