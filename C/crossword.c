#define _GNU_SOURCE
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>

// MACROS
#define BOARD_SIZE 15
#define MAX_WORDS 20
// Global Structs
typedef struct
{
	char let;
	char orientation;
} board_element;

typedef struct
{
	int row;
	int col;
	char orientation;
	_Bool found;
} location;

typedef struct
{
    char word[16];
    location first_let;
} word_struct;


// Prototypes
int receive_words(word_struct words[MAX_WORDS]);
void initialize_board(board_element board[BOARD_SIZE][BOARD_SIZE]);
_Bool fill_board(word_struct words[MAX_WORDS], int num_words, board_element board[BOARD_SIZE][BOARD_SIZE]);
int compare_string(const void* str1, const void* str2);
location find_loc(board_element board[BOARD_SIZE][BOARD_SIZE], char letter, location last_loc);
_Bool check_pos(location intersection_let, board_element board[BOARD_SIZE] [BOARD_SIZE], int let_num,  int word_length);
_Bool check_up(location current, board_element board[BOARD_SIZE][BOARD_SIZE]);
_Bool check_left(location current, board_element board[BOARD_SIZE][BOARD_SIZE]);
_Bool check_right(location current, board_element board[BOARD_SIZE][BOARD_SIZE]);
_Bool check_down(location current, board_element board[BOARD_SIZE][BOARD_SIZE]);
void add_letter(board_element board[BOARD_SIZE][BOARD_SIZE], int row, int col, char a, char orientation);
void add_word(location intersect_let, board_element board[BOARD_SIZE][BOARD_SIZE], int let_num, int word_length, word_struct* word, int word_num);
void print_board(board_element board[BOARD_SIZE][BOARD_SIZE]);
void print_shadow(board_element board[BOARD_SIZE][BOARD_SIZE]);
void print_clues(word_struct words[MAX_WORDS], int num_words);
int loc_cmpre(const void *loc1, const void *loc2);

int main(void)
{
	// Declare Variables
	board_element board[15][15];
	int num_words, i;
	_Bool success;
	
	// Make array of words and initilize them
	word_struct words[MAX_WORDS];
	for(i = 0; i < MAX_WORDS; i++)
	{
	    words[i].word[0]='\0';
	}
	// Display Opening of program
	puts("Anagram Crossword Puzzle Generator");
	printf("----------------------------------\n\n");
	
	
	num_words = receive_words(words);
	initialize_board(board);
	// Sort
	qsort(words, num_words, sizeof(word_struct),compare_string);
	success = fill_board(words, num_words, board);
	
	/*if(!success)
	{
		puts("Not all words could be placed in the crossword");
	}*/ // This was left here if a general message is desired instead of a message per word
	
	// Print output
	printf("\nSolution:\n\n");
	print_board(board);
    printf("\nCrossword Puzzle:\n\n");
	print_shadow(board);
	printf("\nClues:\n\n");
	print_clues(words, num_words);
	
}

int receive_words(word_struct words[MAX_WORDS])
{
	//Define Variables
	int c; // int to store chars
	int i, j; // iteration variables
	_Bool a;
	a = 1; // Boolean for ending the getting of words
	i = 0; //Iteration
	
	//Prompt User and obtain words
	printf("Please enter the words you want in the crossword Puzzle\n");
	printf("Max of MAX_WORDS words and 15 char each. Stop input with \".\"\n");
	while(a)
	{
	    j = 0;
		// Get word until enter
		while((c = getc(stdin)) != '\n')
		{
		    words[i].word[j] = toupper((char)c);
		    j++;
		}
		words[i].word[j] = '\0';
		
		if((words[i].word[0]) == '.')
		{
			a = 0;
		}
		if(i >= 19)
		{
			a = 0;
			i++;
		}
		
		i++;
	}
	return (i-1);
}

void initialize_board(board_element board[BOARD_SIZE][BOARD_SIZE])
{
	int i, j;
	for(i = 0; i < BOARD_SIZE; i++)
	{
		for(j = 0; j < BOARD_SIZE; j++)
		{
			board[i][j].let = '.';
			board[i][j].orientation = '\0';	
		}
	}
	return;
}

int compare_string(const void* str1, const void* str2)
{
	word_struct word1, word2;
	int size1, size2;
	
	word1 = *((word_struct*)str1);
	word2 = *((word_struct*)str2);
	
	size1 = strlen(word1.word);
	size2 = strlen(word2.word);
	if(size1 > size2)
		return -1;
	else if(size1 < size2)
		return 1;
	else
		return 0;
}

_Bool fill_board(word_struct words[MAX_WORDS], int num_words, board_element board[BOARD_SIZE][BOARD_SIZE])
{
	// Declare Variables
	int size, row, col, c, let_num, word_num, word_length;
	_Bool next_let; // Bool to test the end of word
	_Bool success;
	_Bool next_word; // Bool to test if it should proceed to the next word.
	location last_loc;
	
	//Place First Word in the middle
	size = strlen(words[0].word);
	row = BOARD_SIZE / 2;
	col = (BOARD_SIZE - size) / 2;
	
	c = 0;
	while(words[0].word[c] != '\0')
	{
		add_letter(board, row, col + c, words[0].word[c],'h');
		c++;
	}
	// Store info for first word
	words[0].first_let.found = 1; // First letter is placed
	words[0].first_let.row = row;
	words[0].first_let.col = col;
	words[0].first_let.orientation = 'h';
	
	// Add other words
	// Look for letter
	word_num = 1;
	success= 1; // Bool saying that all words were succesfully placed
	let_num = 0; // Letter in word counter
	next_let = 0;
	while((word_num < num_words))
	{
		word_length = strlen(words[word_num].word);
		// Initial location to look for letter
		last_loc.row = 0;
		last_loc.col = 0;
		let_num = 0;
		next_word = 0;
		while(words[word_num].word[let_num] != '\0' && !next_word)
		{
			last_loc = find_loc(board, words[word_num].word[let_num], last_loc);
			if(last_loc.found)
			{
					if(check_pos(last_loc, board, let_num,  word_length))
					{
					    words[word_num].first_let.found = 1;
						add_word(last_loc, board, let_num, word_length, &words[word_num], word_num);
						next_word = 1;
					}
					else // move location to next position to check
					{
					    if(last_loc.col == 14) // If its at the end of the row
					    {
					        if(last_loc.row == 14) // If its already in the last position
					        {
					        	next_let = 1;
					        }
					        else // move no next row
					        {
					            last_loc.col = 0;
					            last_loc.row++;
					        }
					    }
					    else //move to next column
					    {
					        last_loc.col++;
					    }
					}
			}
			else 
				next_let = 1;
			// Try next letter and initialize looking position. If we run out of letters, continue with next word and say we couldn't place all words
			
			if(next_let)
			{			
				if((++let_num) >= word_length)
				{
					printf("The word \"%s\" could not be placed in the crossword\n",words[word_num].word);
					success = 0;
					words[word_num].first_let.found = 0;
					next_word = 1;
				}
				else 
				{
					last_loc.row = 0;
					last_loc.col = 0;
				}
			}
		}
		word_num++;
	}
	return success;
}

void add_letter(board_element board[BOARD_SIZE][BOARD_SIZE], int row, int col, char a, char orientation)
{
	board[row][col].let = a;
	board[row][col].orientation = orientation;
	
}

location find_loc(board_element board[BOARD_SIZE][BOARD_SIZE], char letter, location last_loc)
{
	int row, col; // counter
	location found_let;
	
	for(row = last_loc.row; row < BOARD_SIZE; row++)
	{
		for(col = last_loc.col; col < BOARD_SIZE; col++)
		{
			if((board[row][col].let == letter))
			{
				found_let.row = row;
				found_let.col = col;
				found_let.found = 1;
				found_let.orientation = board[row][col].orientation;
				return found_let;
			}
		}
	}
	found_let.found = 0;
	return found_let;
}
_Bool check_pos(location intersection_let, board_element board[BOARD_SIZE][BOARD_SIZE], int let_num,  int word_length)
{
	location current, end;
	_Bool available = 1;
	
	// When inserting a horizontal word
	if(intersection_let.orientation == 'v')
	{
		current.row = intersection_let.row;
		
		// Check if it goes out of bounds
		if(((current.col = intersection_let.col - let_num) < 0) || ((end.col = current.col + word_length - 1) >= BOARD_SIZE))
		{
			available = 0;
			return available;
		}
		while(available && (current.col <= end.col))
		{
			// Perform Corresponding Check Depending on position in relation to intersection letter
			// Some Positions might be checked twice
			if(current.col < intersection_let.col - 1)
			{
				if( check_up(current, board)   ||
					check_down(current, board) ||
					check_left(current, board) ||
					check_right(current,board)
				  )
					{
						available = 0;
					}  
				else
					available = 1;
			}
			else if(current.col == intersection_let.col -1)
			{
				if( check_up(current, board)   ||
					check_down(current, board) ||
					check_left(current, board)
				  )
					{
						available = 0;
					}  
				else
					available = 1;
			}
			else if(current.col == intersection_let.col)
			{
				if( check_left(current, board) ||
					check_right(current,board)
				  )
					{
						available = 0;
					}  
				else
					available = 1;
			}
			else if(current.col == intersection_let.col + 1)
			{
				if( check_up(current, board)   ||
					check_down(current, board) ||
					check_right(current,board)
				  )
					{
						available = 0;
					}  
				else
					available = 1;
			}
			else if(current.col > intersection_let.col + 1)
			{
				if( check_up(current, board)   ||
					check_down(current, board) ||
					check_left(current, board) ||
					check_right(current,board)
				  )
					{
						available = 0;
					}  
				else
					available = 1;
			}
			// If we can place that letter, check next position
			current.col++;
		}
		
		return available;
		
	}
	// When inserting vertical word
	else if(intersection_let.orientation == 'h')
	{
		current.col = intersection_let.col;
		
		// Check if it goes out of bounds
		if(((current.row = intersection_let.row - let_num) < 0) || ((end.row = current.row + word_length - 1) >= BOARD_SIZE))
		{
			available= 0;
			return available;
		}
		while(available && (current.row <= end.row))
		{
			// Perform Corresponding Check Depending on position in relation to intersection letter
			// Some Positions might be checked twice
			if(current.row < intersection_let.row - 1)
			{
				if( check_up(current, board)   ||
					check_down(current, board) ||
					check_left(current, board) ||
					check_right(current,board)
				  )
					{
						available = 0;
					}  
				else
					available = 1;
			}
			else if(current.row == intersection_let.row -1)
			{
				if( check_up(current, board)   ||
					check_left(current, board) ||
					check_right(current,board)
				  )
					{
						available = 0;
					}  
				else
					available = 1;
			}
			else if(current.row == intersection_let.row)
			{
				if( check_up(current, board)   ||
					check_down(current, board)
				  )
					{
						available = 0;
					}  
				else
					available = 1;
			}
			else if(current.row == intersection_let.row + 1)
			{
				if( check_down(current, board) ||
					check_left(current, board) ||
					check_right(current,board)
				  )
					{
						available = 0;
					}  
				else
					available = 1;
			}
			else if(current.row > intersection_let.row + 1)
			{
				if( check_up(current, board)   ||
					check_down(current, board) ||
					check_left(current, board) ||
					check_right(current,board)
				  )
					{
						available = 0;
					}  
				else
					available = 1;
			}
			// If we can place that letter, check next position
			current.row++;
		}
		
		
		return available;
	}
	return 0; // REMOVE
}

_Bool check_up(location current, board_element board[BOARD_SIZE][BOARD_SIZE])
{
	_Bool used_space;
	// Check if its top letter
	if(current.row == 0)
	{
		used_space = 0;
	}
	// Check if there is letter above
	else if(board[current.row - 1][current.col].let == '.')
	{
		used_space = 0;
	}
	else
	{
		used_space = 1;
	}
	
	return used_space;
}
_Bool check_left(location current, board_element board[BOARD_SIZE][BOARD_SIZE])
{
	_Bool used_space;
	// Check if its leftmost letter
	if(current.col == 0)
	{
		used_space = 0;
	}
	// Check if there is letter to the left
	else if(board[current.row][current.col - 1].let == '.')
	{
		used_space = 0;
	}
	else
	{
		used_space = 1;
	}
	
	return used_space;
}

_Bool check_right(location current, board_element board[BOARD_SIZE][BOARD_SIZE])
{
	_Bool used_space;
	// Check if its rightmost letter
	if(current.col >= BOARD_SIZE - 1)
	{
		used_space = 0;
	}
	// Check if there is letter to the right
	else if(board[current.row][current.col + 1].let == '.')
	{
		used_space = 0;
	}
	else
	{
		used_space = 1;
	}
	
	return used_space;
}
_Bool check_down(location current, board_element board[BOARD_SIZE][BOARD_SIZE])
{
	_Bool used_space;
	// Check if it its bottom letter
	if(current.row >= BOARD_SIZE - 1)
	{
		used_space = 0;
	}
	// Check if there is letter below
	else if(board[current.row + 1][current.col].let == '.')
	{
		used_space = 0;
	}
	else
	{
		used_space = 1;
	}
	
	return used_space;
}

void add_word(location intersect_let, board_element board[BOARD_SIZE][BOARD_SIZE], int let_num, int word_length, word_struct *word, int word_num)
{
	location current;
	int i; // iteration
	
	//For word being inserted horizontally
	if(intersect_let.orientation == 'v')
	{
		
		//Define initial location
		current.row = intersect_let.row;
		current.col = intersect_let.col - let_num;
		
		
        // Store initial Location and orientation
        word->first_let.row = current.row;
        word->first_let.col = current.col;
        word->first_let.orientation = 'h';
        
		// Write in letters
		for(i = 0; i < word_length; i++)
		{
			board[current.row][current.col + i].let = word->word[i];
			board[current.row][current.col + i].orientation = 'h';
		}
		
	}
	// For word being inserted vertically
	else if(intersect_let.orientation == 'h')
	{
		
		//Define initial location
		current.col = intersect_let.col;
		current.row = intersect_let.row - let_num;
		
		// Store initial Location and orientation
        word->first_let.row = current.row;
        word->first_let.col = current.col;
        word->first_let.orientation = 'v';
		
		// Write in letters
		for(i = 0; i < word_length; i++)
		{
			board[current.row + i][current.col].let = word->word[i];
			board[current.row + i][current.col].orientation = 'v';
		}
	}
	
}

void print_board(board_element board[BOARD_SIZE][BOARD_SIZE])
{
	int row, col;
	
	for(row = 0; row < BOARD_SIZE; row++)
	{
		for(col = 0; col < BOARD_SIZE; col++)
		{
			putchar(board[row][col].let);
		}
		putchar('\n');
	}
	return;
}

void print_shadow(board_element board[BOARD_SIZE][BOARD_SIZE])
{
	int row, col;
	
	for(row = 0; row < BOARD_SIZE; row++)
	{
		for(col = 0; col < BOARD_SIZE; col++)
		{
			if((board[row][col].let) == '.')
				putchar('#');
			else
				putchar(' ');
		}
		putchar('\n');
	}
	return;
}

void print_clues(word_struct words[MAX_WORDS], int num_words)
{
    int i;
    qsort(words, num_words, sizeof(word_struct), loc_cmpre);
    
    for(i = 0; i < num_words; i++)
    {
        if(words[i].first_let.found)
        {
            if(words[i].first_let.orientation == 'v')
            {
                printf("%2i,%2i %6s %s\n", words[i].first_let.col + 1, words[i].first_let.row + 1, "Down", strfry(words[i].word));
            }
            else if(words[i].first_let.orientation == 'h')
            {
                printf("%2i,%2i %6s %s\n",words[i].first_let.col + 1, words[i].first_let.row + 1, "Across", strfry(words[i].word));
            }
        }
    }
}

int loc_cmpre(const void *loc1, const void *loc2)
{
    word_struct word1, word2;
    
    word1 = *((word_struct *)loc1);
    word2 = *((word_struct *)loc2);
    
    // Check if word was found
    if(!word1.first_let.found)
        return 1;
    if(!word2.first_let.found)
        return -1;
      
    // Check Location
    // Order by increasing row and col
    if(word1.first_let.col < word2.first_let.col)
        return -1;
    else if(word1.first_let.col > word2.first_let.col)
        return 1;
    else if(word1.first_let.col == word2.first_let.col)
    {
        if(word1.first_let.row > word2.first_let.row)
            return 1;
        else if(word1.first_let.row < word2.first_let.row)
            return -1;
        else if(word1.first_let.row == word2.first_let.row)
            return 0;
    }
    
    return 0; // DELETE
}
