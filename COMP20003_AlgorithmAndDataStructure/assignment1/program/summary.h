/* header file to accompany summary.c */

#ifndef _SUMMARY_H_
#define _SUMMARY_H_

#include <stdio.h>

#include <stdlib.h>

#include <assert.h>

#include "input.h"
#include "dcel.h"

#define OCCUPIED 1     /* flag: the watchtower has already been found in a face */

// loop through all faces and watchtowers to find if it is in the face, 
// then output it to file
void output_summary(char *filename, dcels_t *dcels, 
                watchtower_t **watchtowers, int watchtower_num);

// loop the half edges in the face and call check_in_face()
int loop_the_face(dcels_t *dcels, face_t face, watchtower_t *watchtower);

// print watchtower infomation to file
void print_watchtower(watchtower_t *watchtower, FILE *fp);

// print total population covered by watchtowers in the face
void print_face_population(int face_population[], int n, FILE *fp);

// free all the watchtowers memory
void free_watchtowers(watchtower_t **watchtowers, int n);

#endif