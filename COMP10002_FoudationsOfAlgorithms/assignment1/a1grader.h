//// The bare minimum definitions needed for a1grader
//// To the student: Ignore the below code!
#define BOOK_LENGTH 1284    /* The length of a cipher book */
#define MAX_MSG_LENGTH 1024 /* The maximum length of an encrypted message */
#define BLOCKSIZE 16        /* The length of a block (key, output) */
#define N_TIMESTEPS 20      /* number of timesteps */
#define N_OUTPUT_BLOCKS 2   /* number of output blocks */

typedef char book_t[BOOK_LENGTH];     // A cipherbook (1284 bytes)
typedef unsigned char byte_t;         // A byte (8 bits)
typedef byte_t block_t[BLOCKSIZE];    // A cipher bitset (block) (16 bytes)
typedef byte_t msg_t[MAX_MSG_LENGTH]; // An encrypted message (l bytes)

//// To the student: Ignore the above code!

//////// Function definitions

// Useful debug functions

/* Uncomment the definition below to colourise your control characters to make 
   them easy to see in the hexdump.  If your output has a bunch of stuff below:
 *      [0;31m‧[0md[0;31m [0;31m‧[0md[0;31m[0;31m‧[0md[0;31m
 * you should comment the definition below. */
// #define COLOUR_CTRL_CHARS 1

// Prints a byte array of length len, in groups of 16 (BLOCKSIZE) bytes
// For each line:
// 1. Print the bit number of the first byte of each block, in hexadecimal
// 2. Print the hexadecimal and ASCII representation of each block.
// Example output for one block (len = 16)
//  0x0000: 58 2f 50 37 97 39 c0 82  1c cc d2 73 3f 96 d5 ef   X/P7‧9‧‧‧‧‧s?‧‧‧
//  -> the byte 2f (in hexadecimal) is byte 0x0001 in the array, and prints to
//     the ASCII value /.
// Control characters are converted to the symbol ‧ printed in red if
// COLOUR_CTRL_CHARS is defined (see the definition line above).
void hexdump(byte_t a[], int len);

//////// Submit functions
// Submits stage 0 and prints some useful debug information.
void submit_stage0(int cipher_length, msg_t ciphertext, block_t outputs[N_OUTPUT_BLOCKS],
                   block_t timesteps[N_TIMESTEPS], book_t cipherbook);
// Submits stage 1 and prints some useful debug information.
void submit_stage1(book_t stripped_book, int book_len);
// Submits stage 2 and prints some useful debug information.
void submit_stage2(block_t key2);
// Submits stage 3 and prints some useful debug information.
void submit_stage3(byte_t *key1);
// Submits stage 4 and prints some useful debug information.
void submit_stage4(msg_t plaintext);

void enable_stage_testing(int argc, char *argv[]);
