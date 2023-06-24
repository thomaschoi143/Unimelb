/* header file to accompany processQueue.c */

#ifndef _PROCESS_QUEUE_H_
#define _PROCESS_QUEUE_H_

#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "realProcess.h"

#define READY 1
#define RUNNING 2
#define FINISHED 3

typedef struct node node_t;

typedef struct {
    unsigned int time_arrived;
    char* name;
    unsigned int service_time;
    unsigned int used_time;
    int memory_requirement;
    int memory_start;
    int state;
    real_process_t* real_process;
} process_t;

struct node {
    process_t* process;
    node_t* next;
};

typedef struct {
    node_t* head;
    node_t* tail;
} queue_t;

// Make an empty process queue and return it
queue_t* make_empty_queue();

// Check if the queue is empty or not
int is_empty_queue(queue_t* queue);

// Insert the process to the tail of the queue
void push(queue_t* queue, process_t* process);

// Get and return the process in the head of the queue
process_t* pop(queue_t* queue);

// Remove the node from the queue
void remove_node(queue_t* queue, node_t* node);

// Get and return the process with the shortest service time in the queue
process_t* get_shortest_service_time_process(queue_t* queue);

// Count and return the number of nodes in the queue
int count_nodes(queue_t* queue);

// Free the dynamic memory allocated to the queue and the nodes
void free_queue(queue_t* queue);

#endif