/* main.c
 *
 * Created by Thomas Choi (inchongc@student.unimelb.edu.au)
 * 29/03/2023
 *
 * The main program that reads input file and runs the
 * process manager
 *
 * Usage: allocate -f <filename> -s (SJF | RR) -m (infinite | best-fit) -q (1 |
 * 2 | 3)
 *
 */

#include <getopt.h>
#include <stdio.h>

#include "input.h"
#include "processManager.h"
#include "processQueue.h"

int main(int argc, char** argv) {
    char* filename = NULL;
    char* scheduler = NULL;
    char* memory_strategy = NULL;
    int quantum, opt;

    // Parse the command line arguments
    while ((opt = getopt(argc, argv, "f:s:m:q:")) != -1) {
        switch (opt) {
            case 'f':
                filename = optarg;
                break;
            case 's':
                scheduler = optarg;
                break;
            case 'm':
                memory_strategy = optarg;
                break;
            case 'q':
                quantum = atoi(optarg);
                break;
        }
    }

    queue_t* processes = read_file(filename);

    runProcessManager(processes, scheduler, memory_strategy, quantum);

    return 0;
}
