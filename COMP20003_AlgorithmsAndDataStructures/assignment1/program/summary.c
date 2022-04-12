/* summary.c
*
* Created by Thomas Choi (inchongc@student.unimelb.edu.au)
11/08/2021
*
* Check if the watchtower is in the face and output a summary
* to the third file of the program's arguments
* 
*/

#include <stdio.h>

#include <stdlib.h>

#include <assert.h>

#include "input.h"

#include "dcel.h"

#include "summary.h"

// loop through all faces, watchtowers to find if it is in the face, 
// then output it to file
void output_summary(char *filename, dcels_t *dcels, watchtower_t **watchtowers,
                int watchtower_num) {
    FILE *fp = NULL;
    int i, j, *watchtower_occupied, *face_population;

    fp = fopen(filename, "w");
    assert(fp);

    // calloc: initialise the space to zero
    watchtower_occupied = (int*) calloc(watchtower_num, sizeof(int));
    assert(watchtower_occupied);
    face_population = (int*) calloc(dcels->face_num, sizeof(int));
    assert(face_population);

    for (i = 0; i < dcels->face_num; i++) {
        fprintf(fp, "%d\n", i);
        for (j = 0; j < watchtower_num; j++) {

            /* skip the watchtower that already found in a face
               assume every watchtower can only be in one face */
            if (watchtower_occupied[j] != OCCUPIED) {
                if (loop_the_face(dcels, dcels->faces[i], watchtowers[j]) == IN_FACE) {
                    print_watchtower(watchtowers[j], fp);
                    watchtower_occupied[j] = OCCUPIED;
                    face_population[i] += watchtowers[j]->population;
                }
            }

        }
    }

    print_face_population(face_population, dcels->face_num, fp);

    fclose(fp);

    free(watchtower_occupied);
    watchtower_occupied = NULL;

    free(face_population);
    face_population = NULL;
}

// loop the half edges in the face and call check_in_face()
int loop_the_face(dcels_t *dcels, face_t face, watchtower_t *watchtower) {
    half_edge_t *current, *first;
    int is_in_face = IN_FACE;

    first = face.start;
    if (check_in_face(dcels, first, watchtower->x, watchtower->y) == IN_FACE) {
        current = first->next;
        while (current != first) {
            
            // test fails: leave the loop
            if (check_in_face(dcels, current, watchtower->x, watchtower->y) != IN_FACE) {
                is_in_face = NOT_IN_FACE;
                break;
            }

            current = current->next;
        }
        if (is_in_face == IN_FACE) {
            return IN_FACE;
        }
    }

    return NOT_IN_FACE;
}

// print watchtower infomation to file
void print_watchtower(watchtower_t *watchtower, FILE *fp) {
    fprintf(fp, "Watchtower ID: %s, Postcode: %s, Population Served: %d, "
    "Watchtower Point of Contact Name: %s, x: %.6f, y: %.6f\n", 
    watchtower->id, watchtower->postcode, watchtower->population, 
    watchtower->contact, watchtower->x, watchtower->y);
}

// print total population covered by watchtowers in the face
void print_face_population(int face_population[], int n, FILE *fp) {
    int i;
    for (i = 0; i < n; i++) {
        fprintf(fp, "Face %d population served: %d\n", i, face_population[i]);
    }
}

// free all the watchtowers memory
void free_watchtowers(watchtower_t **watchtowers, int n) {
    int i;

    assert(watchtowers);
    for (i = 0; i < n; i++) {
        free(watchtowers[i]->id);
        free(watchtowers[i]->postcode);
        free(watchtowers[i]->contact);
        free(watchtowers[i]);
    }
    
    free(watchtowers);
}