#include <string.h>
#include <stdio.h>
#include <ctype.h>
#include <stdarg.h>
#include <stdlib.h>
#include <errno.h>
#include "a1grader.h"

#define STAGE_HEADER "Stage %d\n==========\n" /* stage header format string */
#define WORDWRAP 80
#define C_RST "\x1B[0m"
#define C_RED "\x1B[0;31m"
#define CONTROL_CHAR "‧"
#define PROGRAM_OUTPUT_FILENAME "program1_output.txt"

static int internal_testing_stage;
static int testing_printf(const char *format, ...);
static int testing_putchar(char c);

static int should_execute_stage_output(int s);

static int testing_printf(const char *format, ...)
{
    int ret;
    va_list args_stdout;
    va_start(args_stdout, format);

    ret = vfprintf(stdout, format, args_stdout);
    va_end(args_stdout);

#ifndef GROK
    if (internal_testing_stage > -1)
    {
        va_list args_file;
        va_start(args_file, format);

        FILE *f = fopen(PROGRAM_OUTPUT_FILENAME, "a");
        if (f == NULL)
        {
            perror("Can't read output_fname\n");
            exit(1);
        }

        vfprintf(f, format, args_file);

        fflush(f);
        fclose(f);
        va_end(args_file);
    }
#endif
    return ret;
}

static int testing_putchar(char c)
{
    int ret;

    ret = putchar(c);
#ifndef GROK
    if (internal_testing_stage > -1)
    {

        FILE *f = fopen(PROGRAM_OUTPUT_FILENAME, "a");
        if (f == NULL)
        {
            perror("Can't read output_fname\n");
            exit(1);
        }

        fputc(c, f);

        fflush(f);
        fclose(f);
    }
#endif
    return ret;
}

__attribute__((constructor)) void start(int argc, char **argv)
{
    int *pc = (int *)argv - 2; // NASTY HACK TO GET C RUNTIME argc * ;)
    //printf("argc = %d \n", *pc);                     // the original argc, on most systems ;)
    enable_stage_testing(*pc, argv);
}

/* print stage header given stage number */
static void print_stage_header(int stage_num)
{
    testing_printf(STAGE_HEADER, stage_num);
}

// Prints the bytes of a 16-byte block in hexadecimal
// A space is put between each byte (2 hexadigits)
// Between each block of 8 bytes, a space is placed.
static void print_array_hex(byte_t a[], int len)
{
    for (int i = 0; i < len; i++)
    {
        if (i == 8)
            testing_putchar(' ');
        testing_printf("%02x ", a[i]);
    }
}

static void safe_putchar(char c)
{
    if (isprint(c))
    {
        testing_putchar(c);
    }
    else
    {
#ifdef COLOUR_CTRL_CHARS
        testing_printf(C_RED CONTROL_CHAR C_RST);
#else
        testing_printf(CONTROL_CHAR);
#endif
    }
}

/* Prints out the contents of a char array */
// Non-printable (control) characters are printed as ‧ in red.
static void print_array_char(byte_t a[], int len)
{
    for (int i = 0; i < len; i++)
    {
        safe_putchar(a[i]);
    }
}

// Prints the hexadigits, followed by the ASCII representation of the block
// (No address printed)
static void hexdump_block(block_t block)
{
    print_array_hex(block, BLOCKSIZE);
    testing_printf(" ");
    print_array_char(block, BLOCKSIZE);
}

// Prints a byte array of length len, in groups of 16 (BLOCKSIZE) bytes
// For each line:
// 1. Print the bit number of the first byte of each block, in hexadecimal
// 2. Print the hexadecimal and ASCII representation of each block.
// Example output for one block:
//  0x0000: 58 2f 50 37 97 39 c0 82  1c cc d2 73 3f 96 d5 ef   X/P7‧9‧‧‧‧‧s?‧‧‧
//  -> the byte 58 (in hexadecimal) is byte 0x0000 in the array, and prints to
//     the ASCII value X.
void hexdump(byte_t a[], int len)
{
    if (len == 0)
    {
        testing_printf("(Empty byte array given...)\n");
        return;
    }

    int i;
    for (i = 0; i < len / BLOCKSIZE; i++)
    {
        int a_pos = i * BLOCKSIZE;
        testing_printf("0x%04x: ", a_pos);
        hexdump_block(a + a_pos);
        testing_putchar('\n');
    }
    int rem;
    if ((rem = len % BLOCKSIZE) != 0)
    {
        int a_pos = i * BLOCKSIZE;
        testing_printf("0x%04x: ", a_pos);
        print_array_hex(a + a_pos, rem);
        testing_printf("(<- extra bytes not fitting a block)");
        putchar('\n');
    }
}

static void print_wrapped_string(char *str, int len, int wrap_width)
{
    if (len == 0)
        return;

    safe_putchar(str[0]);
    for (int i = 1; i < len; i++)
    {
        if (i % wrap_width == 0)
            testing_putchar('\n');
        safe_putchar(str[i]);
    }
}

static int string_is_printable(char *str, int len)
{
    for (int i = 0; i < len; i++)
    {
        if (!isprint(str[i]))
        {
            return 0;
        }
    }
    return 1;
}

static int should_execute_stage_output(int s)
{
    if (s != internal_testing_stage && internal_testing_stage > -1)
    {
        return 0;
    }
    return 1;
}

// To make submit_stage4 look a little nicer, we'll just store the length
// submitted in stage 0.
static int _ciphertext_length;

void submit_stage0(int cipher_length, msg_t ciphertext,
                   block_t outputs[N_OUTPUT_BLOCKS], block_t timesteps[N_TIMESTEPS],
                   book_t cipherbook)
{
    _ciphertext_length = cipher_length;

    int stage = 0;
    if (!should_execute_stage_output(stage))
    {
        return;
    }

    // The code for grading this stage is hidden to students.
    // The code below is to help with your development.
    print_stage_header(stage);

    testing_printf("Length of encrypted ciphertext (bytes): %d\n", cipher_length);

    testing_printf("Encrypted ciphertext, as hexadecimal bytes and ASCII: (below)\n");
    hexdump(ciphertext, cipher_length);
    testing_putchar('\n');

    testing_printf("Outputs, as hexadecimal bytes and ASCII: (below)\n");
    for (int i = 0; i < N_OUTPUT_BLOCKS; i++)
    {
        testing_printf("  O_%2d: ", i + 9);
        hexdump_block(outputs[i]);
        testing_putchar('\n');
    }
    putchar('\n');

    testing_printf("Timesteps, as hexadecimal bytes and ASCII: (below)\n");
    for (int i = 0; i < N_TIMESTEPS; i++)
    {
        testing_printf("  T_%2d: ", i);
        hexdump_block(timesteps[i]);
        testing_putchar('\n');
    }
    testing_putchar('\n');

    testing_printf("Cipherbook: (below, wordwrapped to %d chars)\n", WORDWRAP);
    if (!string_is_printable(cipherbook, BOOK_LENGTH))
    {
        testing_printf("(WARN! Cipherbook has unprintable / invalid characters, replaced with " CONTROL_CHAR ")\n");
    }
    print_wrapped_string(cipherbook, BOOK_LENGTH, WORDWRAP);
    testing_putchar('\n');
}

void submit_stage1(book_t cipherbook, int book_len)
{
    int stage = 1;
    if (!should_execute_stage_output(stage))
    {
        return;
    }

    // The code for grading this stage is hidden to students.
    // The code below is to help with your development.
    if (internal_testing_stage == -1)
    {
        testing_putchar('\n');
    }
    print_stage_header(stage);
    testing_printf("Punctuation stripped cipherbook length: %d\n", book_len);
    testing_printf("Punctuation stripped cipherbook, as hexadecimal bytes and ASCII: (below)\n");
    hexdump((byte_t *)cipherbook, book_len);
}

void submit_stage2(block_t key2)
{
    int stage = 2;
    if (!should_execute_stage_output(stage))
    {
        return;
    }

    // The code for grading this stage is hidden to students.
    // The code below is to help with your development.
    if (internal_testing_stage == -1)
    {
        testing_putchar('\n');
    }
    print_stage_header(stage);

    testing_printf("Key k2: ");
    hexdump_block(key2);
    testing_putchar('\n');
}

void submit_stage3(byte_t *key1)
{
    int stage = 3;
    if (!should_execute_stage_output(stage))
    {
        return;
    }

    // The code for grading this stage is hidden to students.
    // The code below is to help with your development.
    if (internal_testing_stage == -1)
    {
        testing_putchar('\n');
    }
    print_stage_header(stage);
    testing_printf("Key k1, as hexadecimal bytes and ASCII: (below)\n");
    hexdump(key1, _ciphertext_length);
}

void submit_stage4(byte_t *plaintext)
{
    int stage = 4;
    if (!should_execute_stage_output(stage))
    {
        return;
    }

    // The code for grading this stage is hidden to students.
    // The code below is to help with your development.
    if (internal_testing_stage == -1)
    {
        testing_putchar('\n');
    }
    print_stage_header(stage);
    testing_printf("Decrypted ciphertext, as hexadecimal bytes and ASCII: (below)\n");
    hexdump(plaintext, _ciphertext_length);
    testing_putchar('\n');

    testing_printf("Decrypted ciphertext, as plaintext: (below)\n");
    print_array_char(plaintext, _ciphertext_length);
    testing_putchar('\n');
}

void invalid_arguments()
{
    fprintf(stderr, "Invalid argument. Usage: ./program1 <stage to check>"); // <output file to compare>");
    exit(1);
}

void enable_stage_testing(int argc, char *argv[])
{
    FILE *f;

#ifdef GROK
    char *gargs = "grok_args.txt";

    f = fopen(gargs, "r");
    if (f == NULL)
    {
        internal_testing_stage = -1;
    }
    else
    {
        if (fscanf(f, "%d", &internal_testing_stage) != 1)
        {
            printf("Invalid file provided to Grok: %s", gargs);
        }
        fclose(f);
    }
    return;
#endif

    if (argc == 1)
    {
        internal_testing_stage = -1;
        return;
    }
    if (argc != 2)
    {
        return;
    }

    if ((f = fopen(PROGRAM_OUTPUT_FILENAME, "r")) != NULL)
    {
        remove(PROGRAM_OUTPUT_FILENAME);
        fclose(f);
    }

    /* First argument is the stage we want to check */
    if (sscanf(argv[1], "%d", &internal_testing_stage) != 1)
    {
        invalid_arguments();
    }
}