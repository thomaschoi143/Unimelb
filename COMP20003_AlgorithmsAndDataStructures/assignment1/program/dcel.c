/* dcel.c
*
* Created by Thomas Choi (inchongc@student.unimelb.edu.au)
* 11/08/2021
*
* All dcel-related functions including initialisation, 
* modifying the dcel and freeing the dcels
*
*/

#include <stdio.h>

#include <stdlib.h>

#include <assert.h>

#include "dcel.h"

// make an empty dcels_t using malloc
dcels_t* make_empty_dcels(void) {
    dcels_t *dcels;

    dcels = (dcels_t*) malloc(sizeof(*dcels));
    assert(dcels);
    dcels->vertices = NULL;
    dcels->edges = NULL;
    dcels->faces = NULL;
    dcels->vertex_num = dcels->edge_num = dcels->face_num = 0;

    return dcels;
}

// connect the given vertices and make a new polygon
void make_new_polygon(dcels_t *dcels) {
    int i;
    half_edge_t *current, *old, *first;

    for (i = 0; i < dcels->vertex_num; i++) {
        if (i == 0) {
            first = current = 
                add_new_half_edge(dcels, NULL, NULL, i, i+1, NULL, NEED_NEW_FACE);
        } else if (i == dcels->vertex_num - 1) {
            current = add_new_half_edge(dcels, old, first, i, 0, NULL, NO_NEW_FACE);
        } else {
            current = add_new_half_edge(dcels, old, NULL, i, i+1, NULL, NO_NEW_FACE);
        }

        old = current;
    }
}

// read split data from stdin and call new_split()
void split_stage(dcels_t *dcels) {
    int src_index, dst_index;
    half_edge_t *src, *dst;
    while (scanf("%d%d", &src_index, &dst_index) != EOF) {
        src = dcels->edges[src_index].half_edge;
        dst = dcels->edges[dst_index].half_edge;

        new_split(dcels, src, dst);
    }
}

// split the face from source to destination edge
void new_split(dcels_t *dcels, half_edge_t *src, half_edge_t *dst) {
    double x1, y1, x2, y2, x_mid, y_mid;
    int v1, v2, is_adjacent, src_old_end, dst_old_start;
    half_edge_t *split_half_edge, *split_twin, *src_new_half_edge, 
                *dst_new_half_edge, *src_old_next, *dst_old_prev;

    // start vertex of the split (midpoint of start edge)
    x1 = (dcels->vertices[src->start].x + dcels->vertices[src->end].x) / 2;
    y1 = (dcels->vertices[src->start].y + dcels->vertices[src->end].y) / 2;
    v1 = add_new_vertex(dcels, x1, y1);

    // end vertex of the split (midpoint of end edge)
    x2 = (dcels->vertices[dst->start].x + dcels->vertices[dst->end].x) / 2;
    y2 = (dcels->vertices[dst->start].y + dcels->vertices[dst->end].y) / 2;
    v2 = add_new_vertex(dcels, x2, y2);

    // midpoint of the start vertex and end vertex
    x_mid = (x1 + x2) / 2;
    y_mid = (y1 + y2) / 2;

    /* check if the half edge is in the correct direction with the split 
       (if the point is inside the half-plane?)
       assume the direction is always correct if there is no twin
    */
    if (dst->twin != NULL && check_in_face(dcels, dst, x_mid, y_mid) == NOT_IN_FACE) {
        dst = dst->twin;
    }
    if (src->twin != NULL && check_in_face(dcels, src, x_mid, y_mid) == NOT_IN_FACE) {
        src = src->twin;
    }

    src_old_end = src->end;
    dst_old_start = dst->start;
    src_old_next = src->next;
    dst_old_prev = dst->prev;

    is_adjacent = IS_ADJACENT;
    if (src->next != dst && dst->prev != src) {
        is_adjacent = NOT_ADJACENT;
    }

    // the split edge from src to dst
    split_half_edge = add_new_half_edge(dcels, src, dst, v1, v2, 
                                        NULL, NO_NEW_FACE);
    dcels->faces[split_half_edge->face].start = split_half_edge;

    // its twin
    split_twin = add_new_half_edge(dcels, NULL, NULL, v2, v1, 
                                   split_half_edge, NEED_NEW_FACE);

    if (is_adjacent == IS_ADJACENT) {
        src_new_half_edge = add_new_half_edge(dcels, split_twin, NULL, v1, 
                                              src_old_end, NULL, NO_NEW_FACE);

        dst_new_half_edge = add_new_half_edge(dcels, src_new_half_edge, split_twin, 
                                            src_old_end, v2, NULL, NO_NEW_FACE);
    } else {
        update_face(src_old_next, dst_old_prev, split_twin->face);

        src_new_half_edge = add_new_half_edge(dcels, split_twin, src_old_next, v1, 
                                                src_old_end, NULL, NO_NEW_FACE);

        dst_new_half_edge = add_new_half_edge(dcels, dst_old_prev, split_twin, 
                                        dst_old_start, v2, NULL, NO_NEW_FACE);
    }

    // if dst or src has twin, split the twin half-edge as well
    if (dst->twin != NULL) {
        half_edge_t *dst_twin_old_next;
        int dst_twin_old_end;

        dst_twin_old_end = dst->twin->end;
        dst->twin->end = dst->start;
        dst_twin_old_next = dst->twin->next;

        add_new_half_edge(dcels, dst->twin, dst_twin_old_next, dst->twin->end, 
                            dst_twin_old_end, dst_new_half_edge, NO_NEW_FACE);
    }
    if (src->twin != NULL) {
        half_edge_t *src_twin_old_prev;
        int src_twin_old_start;

        src_twin_old_start = src->twin->start;
        src->twin->start = src->end;
        src_twin_old_prev = src->twin->prev;

        add_new_half_edge(dcels, src_twin_old_prev, src->twin, src_twin_old_start, 
                            src->twin->start, src_new_half_edge, NO_NEW_FACE);
    }

}

// check if the coordinate is in the correct half-plane
int check_in_face(dcels_t *dcels, half_edge_t *half_edge, 
                double check_x, double check_y) {

    double x1, y1, x2, y2;

    // coordinate of the start vertex of the half-edge
    x1 = dcels->vertices[half_edge->start].x;
    y1 = dcels->vertices[half_edge->start].y;

    // coordinate of the end vertex of the half-edge
    x2 = dcels->vertices[half_edge->end].x;
    y2 = dcels->vertices[half_edge->end].y;

    if (x1 == x2) {
        if ((y2 > y1 && check_x > x1) || (y2 < y1 && check_x < x1)) {
            return IN_FACE;
        }
    } else {
        double gradient, intercept, y_pre, y_r;

        // according to the specs
        gradient = (y2 - y1) / (x2 - x1);
        intercept = y2 - gradient * x2;
        y_pre = gradient * check_x + intercept;
        y_r = check_y - y_pre;

        if ((x2 > x1 && y_r <= 0) || (x2 < x1 && y_r >= 0)) {
            return IN_FACE;
        }
    }

    return NOT_IN_FACE;
}

// add a new vertex to dcel as per the coordinate
int add_new_vertex(dcels_t *dcels, double x, double y) {
    dcels->vertices = 
        realloc(dcels->vertices, sizeof(vertex_t) * (dcels->vertex_num + 1));
    assert(dcels->vertices);
    dcels->vertices[dcels->vertex_num].x = x;
    dcels->vertices[dcels->vertex_num].y = y;

    dcels->vertex_num += 1;
    // return the index of the new vertex
    return dcels->vertex_num - 1;
}

// add a new half edge to dcel and call add_new_face() or add_new_edge() if needed
half_edge_t* add_new_half_edge(dcels_t *dcels, half_edge_t *src, half_edge_t *dst, 
                        int v1, int v2, half_edge_t *its_twin, int new_face) {
    half_edge_t *new;
    new = (half_edge_t*) malloc(sizeof(*new));
    assert(new);

    new->start = v1;
    new->end = v2;
    new->next = dst;
    new->prev = src;

    if (src != NULL) {
        src->next = new;
        src->end = new->start;
    }
    if (dst != NULL) {
        dst->prev = new;
        dst->start = new->end;
    }

    if (its_twin != NULL) {
        new->twin = its_twin;
        new->edge = its_twin->edge;
        its_twin->twin = new;
    } else {
        new->edge = add_new_edge(dcels, new);
        new->twin = NULL;
    }

    if (new_face == NEED_NEW_FACE) {
        new->face = add_new_face(dcels, new);
    } else {
        new->face = src->face;
    }

    return new;
}

// add a new edge of the half edge
int add_new_edge(dcels_t *dcels, half_edge_t *one_half_edge) {
    dcels->edges = realloc(dcels->edges, sizeof(edge_t) * (dcels->edge_num + 1));
    assert(dcels->edges);
    dcels->edges[dcels->edge_num].half_edge = one_half_edge;
    dcels->edge_num += 1;

    // return the index of the new edge
    return dcels->edge_num - 1;
}

// add a new face starting from the half edge
int add_new_face(dcels_t *dcels, half_edge_t *one_half_edge) {
    dcels->faces = realloc(dcels->faces, sizeof(face_t) * (dcels->face_num + 1));
    assert(dcels->faces);
    dcels->faces[dcels->face_num].start = one_half_edge;
    dcels->face_num += 1;

    // return the index of the new face
    return dcels->face_num - 1;
}

// update the face index from source half-edge to destination half-edge
void update_face(half_edge_t *src, half_edge_t *dst, int face) {
    half_edge_t *current = src;

    while (current != dst) {
        current->face = face;
        current = current->next;
    }
    current->face = face;
}

// free all the dcels' dynamic memory
void free_dcels(dcels_t *dcels) {
    int i;
    half_edge_t *current, *old;

    assert(dcels);

    // loop through the faces and half-edges and free them
    for (i = 0; i < dcels->face_num; i++) {
        old = dcels->faces[i].start;
        current = old->next;
        free(old);
        while (current != dcels->faces[i].start) {
            old = current;
            current = current->next;
            free(old);
        }
    }
    
    free(dcels->faces);
    free(dcels->edges);
    free(dcels->vertices);
    free(dcels);
}