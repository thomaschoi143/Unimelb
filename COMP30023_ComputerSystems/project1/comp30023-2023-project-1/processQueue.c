/* processQueue.c
 *
 * Created by Thomas Choi (inchongc@student.unimelb.edu.au)
 * 11/04/2023
 *
 * All functions related to a process queue.
 * A process queue is basically a queue storing processes but
 * also can remove a node in the middle to allow the implementation
 * of SJF scheduler.
 *
 */

#include "processQueue.h"

// Make an empty process queue and return it
queue_t* make_empty_queue() {
    queue_t* queue = (queue_t*)malloc(sizeof(*queue));
    assert(queue);
    queue->head = queue->tail = NULL;

    return queue;
}

// Check if the queue is empty or not
int is_empty_queue(queue_t* queue) {
    assert(queue);
    return queue->head == NULL;
}

// Insert the process to the tail of the queue
void push(queue_t* queue, process_t* process) {
    node_t* new = (node_t*)malloc(sizeof(*new));
    assert(queue && new);
    new->process = process;
    new->next = NULL;

    if (queue->tail == NULL) {
        // First insertion into the queue
        queue->head = queue->tail = new;
    } else {
        queue->tail->next = new;
        queue->tail = new;
    }
}

// Get and return the process in the head of the queue
process_t* pop(queue_t* queue) {
    assert(queue && queue->head);
    node_t* old_head = queue->head;
    process_t* target = queue->head->process;
    queue->head = old_head->next;
    if (old_head->next == NULL) {
        // old_head is the last node
        queue->tail = NULL;
    }
    free(old_head);

    return target;
}

// Remove the node from the queue
void remove_node(queue_t* queue, node_t* node) {
    assert(queue);
    node_t *curr = queue->head, *prev = NULL;
    while (curr) {
        if (curr == node) {
            if (prev != NULL) {
                // curr is the last or middle node
                prev->next = curr->next;
            } else {
                // curr is the head node
                queue->head = curr->next;
            }
            if (curr->next == NULL) {
                // curr is the last node
                queue->tail = prev;
            }
            free(curr);
            break;
        }
        prev = curr;
        curr = curr->next;
    }
}

/* Helper function of get_shortest_service_time_process(): compare 2 processes
based on service_time, time_arrived and name. */
int compare(process_t* process1, process_t* process2) {
    int service_time_compare = process1->service_time - process2->service_time;

    if (service_time_compare != 0) {
        return service_time_compare;
    }
    // same service_time, compare time_arrived
    int time_arrived_compare = process1->time_arrived - process2->time_arrived;
    if (time_arrived_compare != 0) {
        return time_arrived_compare;
    }
    // same time_arrived, compare name
    return strcmp(process1->name, process2->name);
}

// Get and return the process with the shortest service time in the queue
process_t* get_shortest_service_time_process(queue_t* queue) {
    assert(queue);
    node_t* target_node = queue->head;
    node_t* curr = queue->head->next;

    while (curr) {
        if (compare(target_node->process, curr->process) > 0) {
            target_node = curr;
        }
        curr = curr->next;
    }

    process_t* target_process = target_node->process;
    remove_node(queue, target_node);

    return target_process;
}

// Count and return the number of nodes in the queue
int count_nodes(queue_t* queue) {
    assert(queue);
    int count = 0;
    node_t* curr = queue->head;
    while (curr) {
        count++;
        curr = curr->next;
    }
    return count;
}

// Free the dynamic memory allocated to the queue and the nodes
void free_queue(queue_t* queue) {
    node_t *curr, *prev;
    assert(queue);

    curr = queue->head;
    while (curr) {
        prev = curr;
        curr = curr->next;
        if (prev->process->real_process) {
            free(prev->process->real_process->fd_write);
            free(prev->process->real_process->fd_read);
            free(prev->process->real_process);
        }
        free(prev->process->name);
        free(prev->process);
        free(prev);
    }
    free(queue);
}
