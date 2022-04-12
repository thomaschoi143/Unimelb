/* main.c
 *
 * Created by Thomas Choi (inchongc@student.unimelb.edu.au)
 * 11/08/2021
 *
 * The main program that finds the total population the watchtowers in each 
 * region are watching over
 *
 * To run the program type: 
 * ./voronoi datafile polygonfile outputfile < splitsfile
 */

#include <stdio.h>

#include <stdlib.h>

#include "input.h"

#include "dcel.h"

#include "summary.h"

#define FILE_NUM 3

int main(int argc, char **argv) {
    int watchtower_num;
    watchtower_t **watchtowers;
    dcels_t *dcels;

    // +1 including the program's name itself
    if (argc != FILE_NUM + 1) {
        fprintf(stderr, "Incorrect number of input files.\n");
        exit(EXIT_FAILURE);
    }

    /* Initialization Stage */
    watchtowers = read_watchtower(argv[1], & watchtower_num);

    dcels = make_empty_dcels();
    read_vertex(dcels, argv[2]);

    /* dcel Stage */
    make_new_polygon(dcels);

    split_stage(dcels);

    // DEBUG: traversing all faces
    // int i;
    // for(i=0;i < dcels->face_num;i++){
    //     half_edge_t *current = dcels->faces[i].start;
    //     printf("--Face %d\n", i);
    //     printf("start: %2d end: %2d edge: %2d face: %d\n", current->start, 
    //     current->end,  current->edge, current->face);
    //     current = current->next;

    //     while(current!=dcels->faces[i].start){
    //         printf("start: %2d end: %2d edge: %2d face: %d\n", current->start, 
    //         current->end,  current->edge, current->face);
    
    //         current = current->next;
    //     }
    //     printf("\n");
    // }

    // DEBUG: traversing all faces with addresses
//     int i;
//     for(i=0;i < dcels->face_num;i++){
//         half_edge_t *current = dcels->faces[i].start;
//         printf("--Face %d\n", i);
//         printf("start: %2d end: %2d edge: %2d twin: %-14p itself: %p prev: %p \
// next: %p face: %d\n", current->start, current->end,  current->edge, 
//         current->twin, current, current->prev, current->next, current->face);
//         current = current->next;
//         while(current!=dcels->faces[i].start){
//             printf("start: %2d end: %2d edge: %2d twin: %-14p itself: %p prev: %p \
// next: %p face: %d\n", current->start, current->end,  current->edge, 
//         current->twin, current, current->prev, current->next, current->face);
    
//             current = current->next;
//         }
//         printf("\n");
//     }

    /* Summary Stage */
    output_summary(argv[3], dcels, watchtowers, watchtower_num);

    free_watchtowers(watchtowers, watchtower_num);
    watchtowers = NULL;
    
    free_dcels(dcels);
    dcels = NULL;

    return 0;
}