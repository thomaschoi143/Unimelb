/* header file to accompany input.c */

#ifndef _INPUT_H_
#define _INPUT_H_

#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "processQueue.h"

#define PROCESS_NAME_MAX_LENGTH 8

// Read all processess from file and return processes queue
queue_t* read_file(char* filename);

#endif