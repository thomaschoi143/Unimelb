/* header file to accompany dcel.c */

#ifndef _dcel_H_
#define _dcel_H_

#include <stdio.h>

#include <stdlib.h>

#include <assert.h>

#define NO_NEW_FACE 0       /* flag: no need to create new face */
#define NEED_NEW_FACE 1     /* flag: need to create new face */
#define NOT_ADJACENT 0      /* flag: two edges are not adjacent */
#define IS_ADJACENT 1       /* flag: two edges are adjacent */
#define IN_FACE 1           /* flag: the point is in the correct half-plane */
#define NOT_IN_FACE 0       /* flag: the point is not in the correct half-plane */

typedef struct half_edge half_edge_t;

struct half_edge{
    int start, end;
    half_edge_t *next, *prev;
    half_edge_t *twin;
    int face;
    int edge;
};

typedef struct{
    double x, y;
}vertex_t;

typedef struct{
    half_edge_t *half_edge;
}edge_t;

typedef struct{
    half_edge_t *start;
}face_t;

/* all dcels' vertices, edges and faces
   each face_t has a pointer to the start half-edge of a single dcel
*/
typedef struct{
    vertex_t *vertices;
    edge_t *edges;
    face_t *faces;
    int vertex_num, edge_num, face_num;
}dcels_t;

// make an empty dcels_t using malloc
dcels_t* make_empty_dcels(void);

// connect the given vertices and make a new polygon
void make_new_polygon(dcels_t *dcels);

// read split data from stdin and call new_split()
void split_stage(dcels_t* dcels);

// split the face from source to destination edge
void new_split(dcels_t *dcels, half_edge_t *src, half_edge_t *dst);

// check if the coordinate is in the correct half-plane
int check_in_face(dcels_t *dcels, half_edge_t *half_edge, double check_x, double check_y);

// add a new vertex to dcels as per the coordinate
int add_new_vertex(dcels_t *dcels, double x, double y);

// add a new half edge to dcels and call add_new_face() or add_new_edge() if needed
half_edge_t* add_new_half_edge(dcels_t *dcels, half_edge_t *src, half_edge_t *dst, 
                            int v1, int v2, half_edge_t *its_twin, int new_face);

// add a new edge of the half edge
int add_new_edge(dcels_t *dcels, half_edge_t *one_half_edge);

// add a new face starting from the half edge
int add_new_face(dcels_t *dcels, half_edge_t *one_half_edge);

// update the face index from source to destination
void update_face(half_edge_t *src, half_edge_t *dst, int face);

// free all the dcels's dynamic memory
void free_dcels(dcels_t *dcels);

#endif