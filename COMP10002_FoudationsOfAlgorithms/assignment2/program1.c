/*Assignment 2
 *COMP10002 Foundations of Algorithms, Semester 1, 2021
 * 
 *Full Name: Thomas Choi    
 *Student Number: 1202247 
 *Date: 10.05.2021           
 */

/******Include libraries ******/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <assert.h>

/******Definitions of constants ******/
#define MAXROOTCHAR 22
#define MAXPOSTAGS 20
#define MAXVARIATIONSCHAR 25
#define MAXVARIATION 4
#define MAXDICTSIZE 100
#define MAXWORDCHAR 25
#define NOT_FOUND (-1)
#define FOUND 0

/******Struct definitions ******/
typedef char variation_t[MAXVARIATIONSCHAR + 1];
typedef struct node node_t;
typedef char data_t[MAXWORDCHAR + 1];

typedef struct
{
	char root[MAXROOTCHAR + 1];
	char POS_tags[MAXPOSTAGS + 5];
	variation_t variations[MAXVARIATION];
}
word_t;

struct node
{
	data_t data;
	node_t * next;
};

typedef struct
{
	node_t * head;
	node_t * foot;
}
list_t;

/******Function Prototypes ******/

// Stage 1
void stage1(word_t dict[], int *variations_total);
void read_POS_tags(char POS_tags[]);
int read_variations(variation_t variations[]);

// Stage 2
void stage2(word_t dict[], int *variations_total, int *dict_size);

// Stage 3
void stage3(list_t *sentence, word_t dict[], int dict_size);
list_t* make_empty_list(void);
list_t* insert_at_foot(list_t *list, data_t value);
void free_list(list_t *list);
int getword(char W[], int limit);
int binary_search(word_t A[], int lo, int hi, data_t key, int *locn);

// Stage 4
void stage4(list_t *sentence, word_t dict[], int dict_size);
int is_in_dict(data_t key, word_t dict[], int dict_size, int *locn);

/******Main function ******/
int main(int argc, char *argv[])
{
	word_t dict[MAXDICTSIZE];
	list_t *sentence;
	int variations_total = 0, dict_size;

	sentence = make_empty_list();

	stage1(dict, &variations_total);

	stage2(dict, &variations_total, &dict_size);

	stage3(sentence, dict, dict_size);

	stage4(sentence, dict, dict_size);

    free_list(sentence);
    sentence = NULL;

	return 0;
}

// perform stage1 - read one dictionary word
void stage1(word_t dict[], int *variations_total)
{
	int i;

	scanf("$%s\n", dict[0].root);
	read_POS_tags(dict[0].POS_tags);
	*variations_total += read_variations(dict[0].variations);

	printf("==========================Stage 1==========================\n");
	printf("Word 0: %s\n", dict[0].root);
	printf("POS: %s\n", dict[0].POS_tags);
	printf("Form: ");

	for (i = 0; i < MAXVARIATION; i++)
	{

		if (dict[0].variations[i][0])
		{
			printf("%d%s", i, dict[0].variations[i]);
		}

	}

	printf("\n");
}

// read the POS tags into a string
void read_POS_tags(char POS_tags[])
{
	char ch;
	int len = 0;

	while ((ch = getchar()) != '\n')
	{
		POS_tags[len] = ch;
		len++;
	}

	POS_tags[len] = '\0';
}

// read the variation forms into an array of strings
int read_variations(variation_t variations[])
{
	char ch;
	int num_variations = 0, form, variation_len;

	ch = getchar();          // skip the # sign
	while ((ch = getchar()) != '\n')
	{

		if (isdigit(ch))
		{
			if (num_variations != 0)
			{
				variations[form][variation_len] = '\0';
			}
			form = ch - '0';
			variation_len = 0;
			num_variations++;
		}
		else
		{
			variations[form][variation_len] = ch;
			variation_len++;
		}

	}

	if (num_variations != 0)
	{
		variations[form][variation_len] = '\0';
	}

	return num_variations;
}

// perform stage2 - read the whole dictionary
void stage2(word_t dict[], int *variations_total, int *dict_size)
{
	int nwords = 1;
	char ch;
	double variation_avg;

	while ((ch = getchar()) != '*')
	{
		scanf("%s\n", dict[nwords].root);
		read_POS_tags(dict[nwords].POS_tags);
		*variations_total += read_variations(dict[nwords].variations);

		nwords++;
	}

	variation_avg = (double) *variations_total / nwords;
	*dict_size = nwords;

	printf("==========================Stage 2==========================\n");
	printf("Number of words: %d\n", nwords);
	printf("Average number of variation forms per word: %.2f\n", variation_avg);
}

// perform stage3 - POS-Tag the words in sentence
void stage3(list_t *sentence, word_t dict[], int dict_size)
{
	data_t word;
	node_t *new;
	int locn;

	while (getword(word, MAXWORDCHAR) != EOF)
	{
		sentence = insert_at_foot(sentence, word);
	}

	printf("==========================Stage 3==========================\n");

	new = sentence->head;
	while (new)
	{

		printf("%-26s", new->data);

		if (binary_search(dict, 0, dict_size, new->data, &locn) == NOT_FOUND)
		{
			printf("NOT_FOUND");
		}
		else
		{
			printf("%s", dict[locn].POS_tags);
		}

		printf("\n");
		new = new->next;

	}
}

// perform stage4 - Stem the words in sentence
void stage4(list_t *sentence, word_t dict[], int dict_size)
{
	node_t *new;
	int locn;

	printf("==========================Stage 4==========================\n");

	new = sentence->head;
	while (new)
	{

		printf("%-26s", new->data);

		if (is_in_dict(new->data, dict, dict_size, &locn) == FOUND)
		{
			printf("%-26s", dict[locn].root);
			printf("%s", dict[locn].POS_tags);
		}
		else
		{
			printf("%-26s", new->data);
			printf("NOT_FOUND");
		}
		printf("\n");

		new = new->next;

	}
}

/* find if the key word is in dictionary (root form and variation forms) 
   and find the location if it is. */
int is_in_dict(data_t key, word_t dict[], int dict_size, int *locn)
{
	int i, j;

	for (i = 0; i < dict_size; i++)
	{

		if (strcmp(key, dict[i].root) == 0)
		{ 
            *locn = i;
			return FOUND;
		}

		for (j = 0; j < MAXVARIATION; j++)
		{
			if (strcmp(key, dict[i].variations[j]) == 0)
			{ 	
                *locn = i;
				return FOUND;
			}
		}

	}

	return NOT_FOUND;
}

// the following function comes from getword.c provided by the subject
int getword(char W[], int limit)
{
	int c, len = 0;
	/*first, skip over any non alphabetics */
	while ((c = getchar()) != EOF && !isalpha(c))
	{
		/*do nothing more */
	}
	if (c == EOF)
	{
		return EOF;
	}
	/*ok, first character of next word has been found */
	W[len++] = c;
	while (len < limit && (c = getchar()) != EOF && isalpha(c))
	{
		/*another character to be stored */
		W[len++] = c;
	}
	/*now close off the string */
	W[len] = '\0';
	return 0;
}

/* the following functions come from listops.c provided by the subject; 
insert_at_foot has been modified to do string copying */
list_t* make_empty_list(void)
{
	list_t * list;
	list = (list_t*) malloc(sizeof(*list));
	assert(list != NULL);
	list->head = list->foot = NULL;
	return list;
}

void free_list(list_t *list)
{
	node_t *curr, *prev;
	assert(list != NULL);
	curr = list->head;
	while (curr)
	{
		prev = curr;
		curr = curr->next;
		free(prev);
	}
	free(list);
}

list_t* insert_at_foot(list_t *list, data_t value)
{
	node_t * new;
	new = (node_t*) malloc(sizeof(*new));
	assert(list != NULL && new != NULL);
	strcpy(new->data, value);
	new->next = NULL;
	if (list->foot == NULL)
	{
		/*this is the first insertion into the list */
		list->head = list->foot = new;
	}
	else
	{
		list->foot->next = new;
		list->foot = new;
	}
	return list;
}

/* the following function comes from binarysearch.c provided by the subject; 
it has been modified to do string comparison */
int binary_search(word_t A[], int lo, int hi, data_t key, int *locn)
{
	int mid, outcome;
	/*if key is in A, it is between A[lo] and A[hi-1] */
	if (lo >= hi)
	{
		return NOT_FOUND;
	}
	mid = (lo + hi) / 2;
	if ((outcome = strcmp(key, A[mid].root)) < 0)
	{
		return binary_search(A, lo, mid, key, locn);
	}
	else if (outcome > 0)
	{
		return binary_search(A, mid + 1, hi, key, locn);
	}
	else
	{
		*locn = mid;
		return FOUND;
	}
}

// algorithms are awesome

/* 
worst-case time complexity: O(sdfm)
because each variation comparison is m time,
since applying sequential search with f variation forms,
comparison for one dictionary word is f*m time. 
For d dictionary words, it takes d*f*m time. (sequential search)
For s words in sentence, it takes s*d*f*m time. (sequential search)
*/