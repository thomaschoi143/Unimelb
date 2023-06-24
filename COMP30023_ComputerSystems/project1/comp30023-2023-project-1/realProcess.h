/* header file to accompany realProcess.c */

#ifndef _REAL_PROCESS_H_
#define _REAL_PROCESS_H_

#include <assert.h>
#include <signal.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>

#define IMPLEMENTS_REAL_PROCESS
#define NUM_BYTES_32_BITS 4
#define NUM_FD_PIPE 2
#define NUM_BYTES_SHA_HASH 64

typedef struct {
    pid_t pid;
    int* fd_write;
    int* fd_read;
} real_process_t;

// Create a real process and set up 2 pipes to write to and read from it
real_process_t* create_real_process(unsigned int simulation_time,
                                    char* process_name);

// Suspend the real process
void suspend_real_process(unsigned int simulation_time,
                          real_process_t* real_process);

// Resume or continue the real process
void resume_or_continue_real_process(unsigned int simulation_time,
                                     real_process_t* real_process);

// Terminate the real process and print the sha hash read from the process
void terminate_real_process(unsigned int simulation_time,
                            real_process_t* real_process, char* process_name);

// Write simulation time to the process using the "write to" pipe
void write_simulation_time_to_process(unsigned int simulation_time,
                                      int* fd_write);

// Verify the process received the right bytes using the "read from" pipe
void verify_sent_to_process(unsigned int simulation_time, int* fd_read);

// Convert the 32-bit unsigned int into Big Endian Byte Ordering
void convert_to_big_endian(unsigned int n, uint8_t* bytes);

// Print the sha hash char by char
void print_sha_hash(char sha_hash[]);

#endif