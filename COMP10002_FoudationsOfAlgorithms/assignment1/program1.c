/* DUKH Attack 
 * COMP10002 Foundations of Algorithms, Semester 1, 2021
 * Skeleton code written by Shaanan Cohney, April 2021
 * 
 * Full Name: Thomas Choi    
 * Student Number: 1202247 
 * Date: 17.04.2021           
 */

/****** Include libraries ******/

#include <stdio.h>
#include <stdlib.h>
/* Do NOT use the following two libraries in stage 1! */
#include <string.h>
#include <ctype.h>

/* Provides functions AES_encrypt and AES_decrypt (see the assignment spec) */
#include "aes.h"
/* Provides functions to submit your work for each stage.
 * See the definitions in a1grader.h, they are all available to use.
 * But don't submit your stages more than once... that's weird! */
#include "a1grader.h"

/****** Definitions of constants ******/

#define BOOK_LENGTH 1284         /* The maximum length of a cipher book */
#define MAX_MSG_LENGTH 1024      /* The maximum length of an encrypted message */
#define BLOCKSIZE 16             /* The length of a block (key, output) */
#define N_TIMESTEPS 20           /* number of timesteps */
#define N_OUTPUT_BLOCKS 2        /* number of output blocks */

// TODO Add your own #defines here, if needed
#define LHS 1
#define RHS 0
#define OUTPUTS_9 0
#define OUTPUTS_10 1
#define START_OF_TIMESTEP 11

/****** Type definitions ******/
/* Recall that these are merely aliases, or shortcuts to their underlying types.
 * For example, block_t can be used in place of an array, length 16 (BLOCKSIZE)
 * of unsigned char, and vice versa. */

typedef char book_t[BOOK_LENGTH];     /* A cipherbook (1284 bytes) */
typedef unsigned char byte_t;         /* A byte (8 bits) */
typedef byte_t block_t[BLOCKSIZE];    /* A cipher bitset (block) (16 bytes) */
typedef byte_t msg_t[MAX_MSG_LENGTH]; /* An encrypted message (l bytes) */

// TODO Add your own type definitions here, if needed



/****** Function Prototypes ******
 * There are more functions defined in aes.h and grader.h */
// Scaffold

int read_hex_line(byte_t output[], int max_count, char *last_char);

// Hint: Variables passed by pointers should be modified in your stages' implementation!

void stage0(msg_t ciphertext, int *ciphertext_length, 
            block_t outputs[N_OUTPUT_BLOCKS], block_t timesteps[N_TIMESTEPS], 
            book_t cipherbook);
void stage1(book_t cipherbook, int *book_len);
void stage2(byte_t codebook[], int book_len, block_t outputs[N_OUTPUT_BLOCKS], 
            block_t timesteps[N_TIMESTEPS], block_t key2);
void stage3(block_t key2, block_t outputs[N_OUTPUT_BLOCKS], 
            block_t timesteps[N_TIMESTEPS], byte_t key1[], int cipher_length);
void stage4(byte_t key1[], byte_t ciphertext[], int cipher_length, 
            byte_t plaintext[]);

// TODO: Put your own function prototypes here! Recommended: separate into stages.
// Stage 1:
int is_alphanumeric(char ch);

// Stage 2:
void xor_byte_array(byte_t x[], byte_t y[], byte_t xor[], int length);
void find_eq1_one_side(block_t timesteps[], block_t key, block_t outputs[],
                       block_t result, int side);
int is_equal(block_t block1, block_t block2);
void copy_blocks(block_t dest, block_t src);

// Stage 3: 
void find_initial_state(block_t outputs[], block_t timesteps[],
                        block_t key2, block_t states[]);
void ran_number_gen(block_t generated_outputs[], block_t states[], block_t timesteps[],
                    block_t key2, int num_of_gen_outputs);
void copy_gen_outputs(byte_t key1[], block_t generated_outputs[], int ciphertext_length);

/* The main function of the program */
// It is strongly suggested you do NOT modify this function.
int main(int argc, char *argv[])
{   
    //// Stage 0
    /* These will store our input from the input file */
    msg_t ciphertext;                  // encrypted message, to be decrypted in the attack
    int ciphertext_length = 0;         // length of the encrypted message
    book_t cipherbook;                 // book used to make key k2
    block_t timesteps[N_TIMESTEPS];    // timesteps used to generate outputs (hex)
    block_t outputs[N_OUTPUT_BLOCKS];  // outputs from the random number generator (hex)

    // Run your stage 0 code
    stage0(ciphertext, &ciphertext_length, outputs, timesteps, cipherbook);
    // And submit the results.  Don't delete this...
    submit_stage0(ciphertext_length, ciphertext, outputs, timesteps, cipherbook);
    
    //// Stage 1
    int book_len = 0;    // length of the cipher book after having removed punctuation
    stage1(cipherbook, &book_len);
    submit_stage1(cipherbook, book_len);
    
    //// Stage 2
    block_t key2;        // the key k2 (hexadecimal)
    stage2((byte_t *) cipherbook, book_len, outputs, timesteps, key2);
    submit_stage2(key2);
    
    //// Stage 3
    byte_t key1[MAX_MSG_LENGTH];       // the key k2 (hexadecimal)
    stage3(key2, outputs, timesteps, key1, ciphertext_length);  
    submit_stage3(key1);
    
    //// Stage 4
    byte_t plaintext[MAX_MSG_LENGTH];  // the plaintext output
    stage4(key1, ciphertext, ciphertext_length, plaintext); 
    submit_stage4(plaintext);
    
    return 0;
}

/********* Scaffold Functions *********/

/* Reads a line in from stdin, converting pairs of hexadecimal (0-F) chars to
 * byte_t (0-255), storing the result into the output array, 
 * stopping after max_count values are read, or a newline is read.
 *
 * Returns the number of *bytes* read.
 * The last char read in from stdin is stored in the value pointed to by last_char.
 * If you don't need to know what last_char is, set that argument to NULL
 */
int read_hex_line(byte_t output[], int max_count, char *last_char)
{
    char hex[2];
    int count;
    for (count = 0; count < max_count; count++)
    {
        /* Consider the first character of the hex */
        hex[0] = getchar();
        if (hex[0] == '\n')
        {
            if (last_char)
            {
                *last_char = hex[0];
            }
            break;
        }
        /* Now the second */
        hex[1] = getchar();
        if (last_char)
        {
            *last_char = hex[0];
        }
        if (hex[1] == '\n')
        {
            break;
        }

        /* Convert this hex into an int and store it */
        output[count] = hex_to_int(hex); // (defined in aes.h)
    }

    return count - 1;
}

/********* Stage 0 Functions *********/
// read the input file
void stage0(msg_t ciphertext, int *ciphertext_length, block_t outputs[N_OUTPUT_BLOCKS], 
            block_t timesteps[N_TIMESTEPS], book_t cipherbook) 
{
    // TODO: Implement stage 0!
    int i, ch;
    scanf("%d\n", ciphertext_length);
    
    read_hex_line(ciphertext, *ciphertext_length, NULL);
    ch = getchar(); // remove the newline between each line

    for (i = 0; i < N_OUTPUT_BLOCKS; i++)
    {
        read_hex_line(outputs[i], BLOCKSIZE, NULL);
    }
    ch = getchar(); // remove the newline between each line

    for (i = 0; i < N_TIMESTEPS; i++)
    {
        read_hex_line(timesteps[i], BLOCKSIZE, NULL);
    }
    ch = getchar(); // remove the newline between each line

    for (i = 0; i < BOOK_LENGTH; i++)
    {
        cipherbook[i] = getchar();
    }
    
    /* !! Submission Instructions !! Store your results in the variables:
     *      ciphertext, ciphertext_length, outputs, timesteps, cipherbook
     * These are passed to submit_stage0 for some useful output and submission. */
}

// TODO: Add functions here, if needed.

/********* Stage 1 Functions *********/
// Reminder: you *cannot* use string.h or ctype.h for this stage!

// strip punctuation in the cipherbook
void stage1(book_t cipherbook, int *book_len) 
{
    // TODO: Implement stage 1!
    int i;
    for (i = 0; i < BOOK_LENGTH; i++)
    {

        if (is_alphanumeric(cipherbook[i]))
        {
            cipherbook[*book_len] = cipherbook[i];
            *book_len += 1;
        }

    }

    /* !! Submission Instructions !! Store your results in the variables:
     *      cipherbook, book_len
     * These are passed to submit_stage1 for some useful output and submission. */
}

// TODO: Add functions here, if needed.

// check if the char is alphanumeric
int is_alphanumeric(char ch)
{
    
    if ( (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') )
    {
        return 1;
    }

    return 0;
}

/********* Stage 2 Functions *********/

// guess the key k2
void stage2(byte_t codebook[], int book_len, block_t outputs[N_OUTPUT_BLOCKS], 
            block_t timesteps[N_TIMESTEPS], block_t key2) 
{
    // TODO: Implement stage 2!
    int i;
    byte_t *key_ptr;

    for (i = 0; i < book_len; i += 16)
    {
        key_ptr = codebook + i;

        block_t eq1_right, eq1_left;
        find_eq1_one_side(timesteps, key_ptr, outputs, eq1_right, RHS);
        find_eq1_one_side(timesteps, key_ptr, outputs, eq1_left, LHS);

        if (is_equal(eq1_left, eq1_right))
        {
            copy_blocks(key2, key_ptr);
        }
        
    }
    
    /* !! Submission Instructions !! Store your results in the variable:
     *      key2
     * These will be passed to submit_stage2 to let you see some useful output! */
}

// TODO: Add functions here, if needed.

// calculate the xor of two blocks and save to block_t xor
void xor_byte_array(byte_t x[], byte_t y[], byte_t xor[], int length)
{
    int i;

    for (i = 0; i < length; i++)
    {
        xor[i] = x[i] ^ y[i];
    }

}

// calculate the single side of equation 1
void find_eq1_one_side(block_t timesteps[], block_t key, block_t outputs[],
                       block_t result, int side)
{
    block_t aes_encrypt_output;

    if (side == LHS)
    {
        // calculate the left hand side of equation 1

        block_t aes_decrypt_output, decrypt_encrypt_xor;

        AES_decrypt(outputs[OUTPUTS_10], key, aes_decrypt_output);
        AES_encrypt(timesteps[10], key, aes_encrypt_output);

        xor_byte_array(aes_decrypt_output, aes_encrypt_output, decrypt_encrypt_xor, BLOCKSIZE);

        AES_decrypt(decrypt_encrypt_xor, key, result);

    }
    else
    {             
        // calculate the right hand side of equation 1

        AES_encrypt(timesteps[9], key, aes_encrypt_output);
        xor_byte_array(outputs[OUTPUTS_9], aes_encrypt_output, result, BLOCKSIZE);

    }

}

// check if two blocks are equal
int is_equal(block_t block1, block_t block2)
{
    int i;

    for (i = 0; i < BLOCKSIZE; i++)
    {

        if (block1[i] != block2[i])
        {
            return 0;
        }

    }

    return 1;

}

// copy the block from src to dest
void copy_blocks(block_t dest, block_t src)
{
    int i;

    for (i = 0; i < BLOCKSIZE; i++)
    {
        dest[i] = src[i];
    }

}
/********* Stage 3 Functions *********/

// generate the key k1
void stage3(block_t key2, block_t outputs[N_OUTPUT_BLOCKS], 
            block_t timesteps[N_TIMESTEPS], byte_t key1[], int ciphertext_length) 
{
    // TODO: Implement stage 3!

    // ciphertext_length bytes of output, each output is 16-bytes in length
    int num_of_gen_outputs = ciphertext_length / BLOCKSIZE;  

    // states has num_of_gen_outputs+1 because of the extra initial state
    block_t states[num_of_gen_outputs+1], generated_outputs[num_of_gen_outputs];  

    find_initial_state(outputs, timesteps, key2, states);
    
    ran_number_gen(generated_outputs, states, timesteps, key2, num_of_gen_outputs);

    copy_gen_outputs(key1, generated_outputs, ciphertext_length);

    /* !! Submission Instructions !! Store your results in the variable:
     *      key1
     * These will be passed to submit_stage3 to let you see some useful output! */
}

// TODO: Add functions here, if needed.

// calculate the initial state of the generator
void find_initial_state(block_t outputs[], block_t timesteps[], block_t key2, block_t states[])
{
    block_t aes_encrypt_output, output10_encrypt_xor;

    AES_encrypt(timesteps[10], key2, aes_encrypt_output);
    xor_byte_array(outputs[OUTPUTS_10], aes_encrypt_output, output10_encrypt_xor, BLOCKSIZE);

    AES_encrypt(output10_encrypt_xor, key2, states[0]);  // initial state is stored in states[0]
}

// implement the random number generator and generate output of length num_of_gen_outputs
void ran_number_gen(block_t generated_outputs[], block_t states[], block_t timesteps[],
                    block_t key2, int num_of_gen_outputs)
{
    block_t intermediates[num_of_gen_outputs];
    int i;

    for (i = 0; i < num_of_gen_outputs; i++)
    {
        block_t intermediate_state_xor, output_intermediate_xor;

        AES_encrypt(timesteps[START_OF_TIMESTEP+i], key2, intermediates[i]);
        xor_byte_array(intermediates[i], states[i], intermediate_state_xor, BLOCKSIZE);

        AES_encrypt(intermediate_state_xor, key2, generated_outputs[i]);
        xor_byte_array(generated_outputs[i], intermediates[i], 
                       output_intermediate_xor, BLOCKSIZE);

        AES_encrypt(output_intermediate_xor, key2, states[i+1]);

    }

}

// copy the generated outputs to k1
void copy_gen_outputs(byte_t key1[], block_t generated_outputs[], int ciphertext_length)
{
    int i, row = 0, col = 0;

    for (i = 0; i < ciphertext_length; i++)
    {

        if (col % BLOCKSIZE == 0 && col != 0)
        {
            col = 0;
            row++;
        }

        key1[i] = generated_outputs[row][col];
        col++;

    }

}
/********* Stage 4 Functions *********/

// decrypte the original message
void stage4(byte_t key1[], byte_t ciphertext[], int cipher_length, byte_t plaintext[])
{
    // TODO: Implement stage 4!
    xor_byte_array(ciphertext, key1, plaintext, cipher_length);

    /* !! Submission Instructions !! Store your results in the variable:
     *      plaintext
     * These will be passed to submit_stage4 to let you see some useful output! */
}

// TODO: Add functions here, if needed.

/********* END OF ASSIGNMENT! *********/
/* If you would like to try the bonus stage, attempt it in a new file, bonus.c */
// Feel free to write a comment to the marker or the lecturer below...

// algorithms are awesome
