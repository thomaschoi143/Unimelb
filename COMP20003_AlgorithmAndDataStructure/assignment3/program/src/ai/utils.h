#ifndef __UTILS__
#define __UTILS__

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <termios.h>
#include <stdbool.h>
#include <stdint.h>
#include <time.h>
#include <signal.h>
#include "../../include/sokoban.h"

#define SIZE 4
#define _XOPEN_SOURCE 500

// My own defines
#define NO_BLOCK -1
#define LEFT_BOX_BLOCK 0
#define RIGHT_BOX_BLOCK 1
#define UP_BOX_BLOCK 2
#define DOWN_BOX_BLOCK 3
#define BOTH_BOX_BLOCK 4
#define WALL_BLOCK 5

#define VERTICAL 1
#define HORIZONTAL 0

/**
 * Data structure containing the information about the game state 
 * representing the state of the game.
 */
struct state_s{
    char **map;
	int player_x;
	int player_y;
};

typedef struct state_s state_t;


/**
* Move type
*/
typedef enum moves{
	left=0,
	right=1,
	up=2,
	down=3
} move_t;




/**
 * Helper functions
 */
bool execute_move_t(sokoban_t *init_data, state_t *state, move_t move);
bool simple_corner_deadlock(state_t *state);
bool winning_condition(sokoban_t *init_data, state_t *state);
void play_solution( sokoban_t init_data, char* solution );
void print_map(sokoban_t *init_data, state_t *state);

/* My own functions */

// My way to print the map of a state
void my_print_map(sokoban_t *init_data, state_t *state);

// free the dynamic memory allocated in init_data
void freeSokoban(sokoban_t *init_data);

// check if the state is a freeze deadlock
bool freeze_deadlock(sokoban_t *init_data, state_t *state, int moved_box_x, int moved_box_y);

#endif

