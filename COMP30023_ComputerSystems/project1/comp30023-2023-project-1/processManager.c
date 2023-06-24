/* processManager.c
 *
 * Created by Thomas Choi (inchongc@student.unimelb.edu.au)
 * 11/04/2023
 *
 * All functions related to the simulated process manager
 * with memory management and scheduling algorithm.
 *
 */

#include "processManager.h"

// Start the simulated process manager
void runProcessManager(queue_t* processes, char* scheduler,
                       char* memory_strategy, int quantum) {
    unsigned int cycles_num = 0;
    bool is_infinite_memory = strcmp(memory_strategy, "infinite") == 0, *memory;

    if (!is_infinite_memory) {
        bool temp[MAX_MEMORY] = {false};
        memory = temp;
    }

    statistics_t stat = {.num_processes = count_nodes(processes)};

    queue_t* arrived_processes = make_empty_queue();
    queue_t* ready_processes = make_empty_queue();
    process_t* running_process = NULL;

    // Function pointer of the process scheduling algorithm
    process_t* (*scheduler_ptr)(queue_t*, process_t*, unsigned int);
    if (strcmp(scheduler, "SJF") == 0) {
        scheduler_ptr = SJF_scheduler;
    } else {
        scheduler_ptr = RR_scheduler;
    }

    while (true) {
        stat.simulation_time = cycles_num * quantum;

        if (running_process) {
            run_process(running_process, quantum);
            if (running_process->state == FINISHED) {
                // The process is finished

                if (!is_infinite_memory) {
                    /* deallocate its memory if it is implementing
                    the best fit memory allocation */
                    deallocate_memory(memory, running_process);
                }
                finish_process(running_process,
                               count_nodes(arrived_processes) +
                                   count_nodes(ready_processes),
                               &stat);
                running_process = NULL;
            }
        }

        get_new_arrived_processes(stat.simulation_time, processes,
                                  arrived_processes);

        if (is_infinite_memory) {
            infinite_memory_strategy(arrived_processes, ready_processes);
        } else {
            best_fit_memory_strategy(arrived_processes, ready_processes, memory,
                                     stat.simulation_time);
        }

        if (is_empty_queue(processes) && is_empty_queue(arrived_processes) &&
            is_empty_queue(ready_processes) && !running_process) {
            // Finished all the processes, end the process manager
            break;
        }

        running_process = scheduler_ptr(ready_processes, running_process,
                                        stat.simulation_time);

        cycles_num++;
    }

    print_statistics(&stat);

    free_queue(processes);
    free_queue(arrived_processes);
    free_queue(ready_processes);
}

// Run the process for one quantum
void run_process(process_t* running_process, int quantum) {
    if (running_process->service_time - running_process->used_time <= quantum) {
        running_process->state = FINISHED;
    }
    running_process->used_time += quantum;
}

// Terminate the process and calculate the statistics
void finish_process(process_t* finished_process, int num_processes_left,
                    statistics_t* stat) {
    unsigned int turnaround_time =
        stat->simulation_time - finished_process->time_arrived;
    double overhead = (double)turnaround_time / finished_process->service_time;

    printf("%u,FINISHED,process_name=%s,proc_remaining=%d\n",
           stat->simulation_time, finished_process->name, num_processes_left);

    terminate_real_process(stat->simulation_time,
                           finished_process->real_process,
                           finished_process->name);

    stat->total_turnaround_time += turnaround_time;
    stat->total_overhead += overhead;
    if (overhead > stat->max_overhead) {
        stat->max_overhead = overhead;
    }
    free(finished_process->name);
    free(finished_process);
}

/* Add the processes arriving before or at this simulation time to the
arrived_processes queue in the order they appear in the processes queue */
void get_new_arrived_processes(unsigned int simulation_time, queue_t* processes,
                               queue_t* arrived_processes) {
    if (!is_empty_queue(processes)) {
        node_t* curr = processes->head;
        process_t* process = NULL;
        while (curr && curr->process->time_arrived <= simulation_time) {
            curr = curr->next;
            process = pop(processes);
            push(arrived_processes, process);
        }
    }
}

// Infinite memory strategy: move all the arrived processes to ready queue
void infinite_memory_strategy(queue_t* arrived_processes,
                              queue_t* ready_processes) {
    while (!is_empty_queue(arrived_processes)) {
        process_t* process = pop(arrived_processes);
        push(ready_processes, process);
        process->state = READY;
    }
}

/* Helper function of best fit memory strategy: if the memory size of this hole
is enough and is smaller than the current best hole, replace the current best
hole */
void check_hole(int* hole_length, int memory_requirement, int* best_start,
                int* best_hole_length, int current_start) {
    if (*hole_length >= memory_requirement &&
        (*best_start == NO_BEST_START_MEMORY ||
         *hole_length < *best_hole_length)) {
        *best_start = current_start;
        *best_hole_length = *hole_length;
    }
    *hole_length = 0;
}

/* Helper function of best fit memory strategy: go through all memory and find
the best memory allocation. If can find, allocate memory of the required size
and return the start of its memory. If cannot find, return
NO_BEST_START_MEMORY */
int find_best_memory_address(bool memory[], int memory_requirement) {
    int best_start = NO_BEST_START_MEMORY, best_hole_length, current_start,
        hole_length = 0, i;
    bool is_in_hole = false;

    for (i = 0; i < MAX_MEMORY; i++) {
        if (!memory[i]) {
            if (!is_in_hole) {
                is_in_hole = true;
                current_start = i;
            }
            hole_length++;
        } else if (is_in_hole) {
            /* memory[i] is not a hole, memory[i-1] is a hole, hole ends, check
               hole */
            check_hole(&hole_length, memory_requirement, &best_start,
                       &best_hole_length, current_start);
            is_in_hole = false;
        }
    }
    if (is_in_hole) {
        // memory[MAX_MEMORY - 1] is a hole, hole ends, check hole
        check_hole(&hole_length, memory_requirement, &best_start,
                   &best_hole_length, current_start);
    }

    if (best_start != NO_BEST_START_MEMORY) {
        // Allocate memory of the required size from the best_start address
        for (i = best_start; i < best_start + memory_requirement; i++) {
            memory[i] = true;
        }
    }

    return best_start;
}

// Implement best fit memory allocation algorithm for arrived processes
void best_fit_memory_strategy(queue_t* arrived_processes,
                              queue_t* ready_processes, bool memory[],
                              unsigned int simulation_time) {
    node_t *curr = arrived_processes->head, *prev = NULL;
    int best_start;
    while (curr) {
        int memory_requirement = curr->process->memory_requirement;
        process_t* process = curr->process;
        prev = curr;
        curr = curr->next;

        if ((best_start = find_best_memory_address(
                 memory, memory_requirement)) != NO_BEST_START_MEMORY) {
            remove_node(arrived_processes, prev);
            push(ready_processes, process);
            process->state = READY;
            process->memory_start = best_start;

            printf("%u,READY,process_name=%s,assigned_at=%d\n", simulation_time,
                   process->name, process->memory_start);
        }
    }
}

/* For best-fit memory strategy, deallocate its memory when the process
terminates */
void deallocate_memory(bool memory[], process_t* process) {
    for (int i = process->memory_start;
         i < process->memory_start + process->memory_requirement; i++) {
        memory[i] = false;
    }
}

/* Implement the Shortest Job First (SJF) scheduling algorithm, return the
selected process */
process_t* SJF_scheduler(queue_t* ready_processes, process_t* running_process,
                         unsigned int simulation_time) {
    if (!running_process && !is_empty_queue(ready_processes)) {
        // Choose a process from the ready queue to run
        running_process = get_shortest_service_time_process(ready_processes);
        running_process->state = RUNNING;
        running_process->real_process =
            create_real_process(simulation_time, running_process->name);

        printf("%u,RUNNING,process_name=%s,remaining_time=%u\n",
               simulation_time, running_process->name,
               running_process->service_time);

    } else if (running_process) {
        resume_or_continue_real_process(simulation_time,
                                        running_process->real_process);
    }
    return running_process;
}

/* Implement the Round Robin (RR) scheduling algorithm, return the selected
process */
process_t* RR_scheduler(queue_t* ready_processes, process_t* running_process,
                        unsigned int simulation_time) {
    if (!is_empty_queue(ready_processes)) {
        if (running_process) {
            // Running process needs more CPU time, suspend it
            suspend_real_process(simulation_time,
                                 running_process->real_process);
            running_process->state = READY;
            push(ready_processes, running_process);
        }
        running_process = pop(ready_processes);
        running_process->state = RUNNING;

        if (running_process->used_time == 0) {
            // Process runs for the first time
            running_process->real_process =
                create_real_process(simulation_time, running_process->name);
        } else {
            resume_or_continue_real_process(simulation_time,
                                            running_process->real_process);
        }

        printf("%u,RUNNING,process_name=%s,remaining_time=%u\n",
               simulation_time, running_process->name,
               running_process->service_time - running_process->used_time);

    } else if (running_process) {
        /* If ready queue is empty and the process needs more CPU time, it
        continues to run */
        resume_or_continue_real_process(simulation_time,
                                        running_process->real_process);
    }

    return running_process;
}

// Print the performance statistics
void print_statistics(statistics_t* stat) {
    printf("Turnaround time %.0f\n",
           ceil((double)stat->total_turnaround_time / stat->num_processes));
    printf("Time overhead %.2f %.2f\n", round(stat->max_overhead * 100) / 100,
           round(stat->total_overhead / stat->num_processes * 100) / 100);
    printf("Makespan %u\n", stat->simulation_time);
}