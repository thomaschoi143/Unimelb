/* header file to accompany input.c */

#ifndef _INPUT_H_
#define _INPUT_H_

#include <stdio.h>

#include <stdlib.h>

#include <assert.h>

#include <string.h>

#include "dcel.h"

#define ROW_MAX_CHAR 512       /* max number of char in a row */
#define INITIAL_WATCHTOWER 20  /* assume the initial number of watchtowers 
                                  to use malloc */
#define FIELD_NUM 6            /* the number of fields */
#define DELIM ","             /* the delim to separate the row */

// struct of watchtower data
typedef struct{
    char *id, *postcode, *contact;
    int population;
    double x, y;
}watchtower_t;

// read the watchtowers data and store in array of pointers to struct
watchtower_t** read_watchtower(char *filename, int *watchtower_num);

// read vertices data and store in dcels->vertices
void read_vertex(dcels_t *dcels, char *filename);

#endif