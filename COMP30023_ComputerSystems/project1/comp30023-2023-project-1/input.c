/* input.c
 *
 * Created by Thomas Choi (inchongc@student.unimelb.edu.au)
 * 29/03/2023
 *
 * A function to read the list of processes to be executed
 * from the specific file
 *
 */

#include "input.h"

// Read all processess from file and return processes queue
queue_t* read_file(char* filename) {
    FILE* fp = fopen(filename, "r");

    if (fp == NULL) {
        perror("fopen");
        exit(EXIT_FAILURE);
    }

    queue_t* processes = make_empty_queue();
    unsigned int time_arrived;
    // +1: the NULL byte
    char name[PROCESS_NAME_MAX_LENGTH + 1];
    unsigned int service_time;
    int memory_requirement;

    while (fscanf(fp, "%u %s %u %d", &time_arrived, name, &service_time,
                  &memory_requirement) == 4) {
        process_t* process = (process_t*)malloc(sizeof(*process));
        assert(process);

        // +1: the NULL byte
        process->name = (char*)malloc(sizeof(char) * (strlen(name) + 1));
        assert(process->name);
        strcpy(process->name, name);

        process->time_arrived = time_arrived;
        process->service_time = service_time;
        process->memory_requirement = memory_requirement;
        process->used_time = 0;

        push(processes, process);
    }

    fclose(fp);

    return processes;
}