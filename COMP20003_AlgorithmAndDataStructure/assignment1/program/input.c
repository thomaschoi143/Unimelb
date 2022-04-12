/* input.c
*
* Created by Thomas Choi (inchongc@student.unimelb.edu.au)
11/08/2021
*
* Input Stage: Read watchtower and vertex data from the 
* first two program arguments (2 file names)
*
*/

#include <stdio.h>

#include <stdlib.h>

#include <assert.h>

#include <string.h>

#include "input.h"

#include "dcel.h"

// read the watchtowers data and store in array of pointers to struct
watchtower_t** read_watchtower(char *filename, int *watchtower_num) {
    FILE *fp;
    int len, line_num = 0, current_size, i;
    char *line = NULL;
    size_t buffer_size = ROW_MAX_CHAR + 1;    /* +1 the NULL byte */
    watchtower_t *one_watchtower, **watchtowers;

    current_size = INITIAL_WATCHTOWER;

    watchtowers = (watchtower_t**) malloc(sizeof(*watchtowers) * current_size);
    assert(watchtowers);

    fp = fopen(filename, "r");
    assert(fp);

    while ((len = getline(&line, &buffer_size, fp)) != EOF) {
        // skip the header of the csv
        if (line_num != 0) {
            if (line_num == current_size) {
                current_size *= 2;
                watchtowers = 
                    realloc(watchtowers, sizeof(*watchtowers) * current_size);
                assert(watchtowers);
            }

            // separate the row into 6 fields by ','
            i = 0;
            char *fields[FIELD_NUM];
            line[len - 1] = '\0';
            fields[i] = strtok(line, DELIM);
            while (fields[i] != NULL) {
                i++;
                fields[i] = strtok(NULL, DELIM);
            }

            one_watchtower = (watchtower_t*) malloc(sizeof(*one_watchtower));
            assert(one_watchtower);

            // +1 the NULL byte
            one_watchtower->id = 
                (char*) malloc(sizeof(char) * (strlen(fields[0]) + 1));
            assert(one_watchtower->id);
            strcpy(one_watchtower->id, fields[0]);

            // +1 the NULL byte
            one_watchtower->postcode = 
                (char*) malloc(sizeof(char) * (strlen(fields[1]) + 1));
            assert(one_watchtower->postcode);
            strcpy(one_watchtower->postcode, fields[1]);

            one_watchtower->population = atoi(fields[2]);

            // +1 the NULL byte
            one_watchtower->contact =
                (char*) malloc(sizeof(char) * (strlen(fields[3]) + 1));
            assert(one_watchtower->contact);
            strcpy(one_watchtower->contact, fields[3]);

            one_watchtower->x = atof(fields[4]);
            one_watchtower->y = atof(fields[5]);

            watchtowers[line_num-1] = one_watchtower;
        }
        line_num++;
    }

    free(line);
    fclose(fp);

    // exclude the header of csv
    *watchtower_num = line_num - 1;

    // reduce the size of the dynamic array of pointers to struct
    watchtowers = realloc(watchtowers, sizeof(*watchtowers) * *watchtower_num);
    assert(watchtowers);

    return watchtowers;
}

// read vertices data and store in dcels->vertices
void read_vertex(dcels_t *dcels, char *filename) {
    FILE *fp;
    double x, y;

    fp = fopen(filename, "r");
    assert(fp);

    while (fscanf(fp, "%lf%lf", &x, &y) != EOF) {
        add_new_vertex(dcels, x, y);
    }

    fclose(fp);
}