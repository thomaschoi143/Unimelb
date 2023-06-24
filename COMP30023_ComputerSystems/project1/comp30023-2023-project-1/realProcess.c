/* realProcess.c
 *
 * Created by Thomas Choi (inchongc@student.unimelb.edu.au)
 * 11/04/2023
 *
 * All functions related to manage real processes including creation,
 * suspension, resumption, continuation and termination.
 *
 */

#include "realProcess.h"

// Create a real process and set up 2 pipes to write to and read from it
real_process_t* create_real_process(unsigned int simulation_time,
                                    char* process_name) {
    pid_t childpid;
    int *fd_write = (int*)malloc(sizeof(int) * NUM_FD_PIPE),
        *fd_read = (int*)malloc(sizeof(int) * NUM_FD_PIPE);
    assert(fd_write);
    assert(fd_read);

    real_process_t* real_process =
        (real_process_t*)malloc(sizeof(*real_process));
    assert(real_process);

    real_process->fd_write = fd_write;
    real_process->fd_read = fd_read;

    pipe(fd_write);  // pipe in which the process manager writes to the process
    pipe(fd_read);   // pipe in which the process manager reads from the process

    if ((childpid = fork()) == -1) {
        perror("fork");
        exit(1);
    }

    if (childpid == 0) {
        close(fd_write[1]);
        dup2(fd_write[0], STDIN_FILENO);
        close(fd_write[0]);

        close(fd_read[0]);
        dup2(fd_read[1], STDOUT_FILENO);
        close(fd_read[1]);

        char* argv[] = {"./process", process_name, NULL};
        execv(argv[0], argv);
    } else {
        close(fd_write[0]);
        write_simulation_time_to_process(simulation_time, fd_write);

        close(fd_read[1]);
        verify_sent_to_process(simulation_time, fd_read);

        real_process->pid = childpid;
    }
    return real_process;
}

// Suspend the real process
void suspend_real_process(unsigned int simulation_time,
                          real_process_t* real_process) {
    pid_t w;
    int wstatus;
    write_simulation_time_to_process(simulation_time, real_process->fd_write);
    kill(real_process->pid, SIGTSTP);

    do {
        w = waitpid(real_process->pid, &wstatus, WUNTRACED);
        if (w == -1) {
            perror("waitpid");
            exit(1);
        }

        // DEBUG
        // if (WIFSTOPPED(wstatus)) {
        //     printf("stopped by signal %d\n", WSTOPSIG(wstatus));
        // }
    } while (!WIFSTOPPED(wstatus));
}

// Resume or continue the real process
void resume_or_continue_real_process(unsigned int simulation_time,
                                     real_process_t* real_process) {
    write_simulation_time_to_process(simulation_time, real_process->fd_write);
    kill(real_process->pid, SIGCONT);
    verify_sent_to_process(simulation_time, real_process->fd_read);
}

// Terminate the real process and print the sha hash read from the process
void terminate_real_process(unsigned int simulation_time,
                            real_process_t* real_process, char* process_name) {
    int* fd_read = real_process->fd_read;
    char sha_hash[NUM_BYTES_SHA_HASH];
    int nbytes;

    write_simulation_time_to_process(simulation_time, real_process->fd_write);
    kill(real_process->pid, SIGTERM);

    if ((nbytes = read(fd_read[0], sha_hash, sizeof(sha_hash))) == -1) {
        perror("read");
        exit(1);
    }

    printf("%u,FINISHED-PROCESS,process_name=%s,sha=", simulation_time,
           process_name);
    print_sha_hash(sha_hash);
    printf("\n");

    close(real_process->fd_write[1]);
    close(fd_read[0]);

    free(real_process->fd_write);
    free(real_process->fd_read);
    free(real_process);
}

// Write simulation time to the process using the "write to" pipe
void write_simulation_time_to_process(unsigned int simulation_time,
                                      int* fd_write) {
    uint8_t bytes[NUM_BYTES_32_BITS] = {0, 0, 0, 0};
    convert_to_big_endian(simulation_time, bytes);

    if (write(fd_write[1], bytes, sizeof(bytes)) == -1) {
        perror("write");
        exit(1);
    }
}

// Verify the process received the right bytes using the "read from" pipe
void verify_sent_to_process(unsigned int simulation_time, int* fd_read) {
    uint8_t bytes[NUM_BYTES_32_BITS] = {0, 0, 0, 0}, verify_byte;
    convert_to_big_endian(simulation_time, bytes);

    if (read(fd_read[0], &verify_byte, sizeof(verify_byte)) == -1) {
        perror("read");
        exit(1);
    }
    assert(verify_byte == bytes[NUM_BYTES_32_BITS - 1]);
}

// Convert the 32-bit unsigned int into Big Endian Byte Ordering
void convert_to_big_endian(unsigned int n, uint8_t* bytes) {
    *(uint32_t*)bytes = n;

    uint8_t temp;
    int j = NUM_BYTES_32_BITS - 1;
    for (int i = 0; i < NUM_BYTES_32_BITS / 2; i++) {
        temp = bytes[i];
        bytes[i] = bytes[j];
        bytes[j] = temp;
        j--;
    }
}

// Print the sha hash char by char
void print_sha_hash(char sha_hash[]) {
    // As the string doesn't have the NULL byte
    for (int i = 0; i < NUM_BYTES_SHA_HASH; i++) {
        printf("%c", sha_hash[i]);
    }
}