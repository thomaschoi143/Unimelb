/* header file to accompany processManager.c */

#ifndef _PROCESS_MANAGER_H_
#define _PROCESS_MANAGER_H_

#include <math.h>
#include <stdbool.h>
#include <stdio.h>
#include <string.h>

#include "processQueue.h"

#define MAX_MEMORY 2048
#define NO_BEST_START_MEMORY -1

typedef struct {
    unsigned int simulation_time;
    int num_processes;
    // total_turnaround_time may be over the max of unsigned int
    long total_turnaround_time;
    double total_overhead;
    double max_overhead;
} statistics_t;

// Start the simulated process manager
void runProcessManager(queue_t* processes, char* scheduler,
                       char* memory_strategy, int quantum);

// Run the process for one quantum
void run_process(process_t* running_process, int quantum);

// Terminate the process and calculate the statistics
void finish_process(process_t* finished_process, int num_processes_left,
                    statistics_t* stat);

/* Add the processes arriving before or at this simulation time to the
arrived_processes queue in the order they appear in the processes queue */
void get_new_arrived_processes(unsigned int simulation_time, queue_t* processes,
                               queue_t* input_processes);

// Infinite memory strategy: move all the arrived processes to ready queue
void infinite_memory_strategy(queue_t* input_processes,
                              queue_t* ready_processes);

// Implement best fit memory allocation algorithm for arrived processes
void best_fit_memory_strategy(queue_t* arrived_processes,
                              queue_t* ready_processes, bool memory[],
                              unsigned int simulation_time);

/* For best-fit memory strategy, deallocate its memory when the process
terminates */
void deallocate_memory(bool memory[], process_t* process);

/* Implement the Shortest Job First (SJF) scheduling algorithm, return the
selected process */
process_t* SJF_scheduler(queue_t* ready_processes, process_t* running_process,
                         unsigned int simulation_time);

/* Implement the Round Robin (RR) scheduling algorithm, return the selected
process */
process_t* RR_scheduler(queue_t* ready_processes, process_t* running_process,
                        unsigned int simulation_time);

// Print the performance statistics
void print_statistics(statistics_t* stat);

#endif