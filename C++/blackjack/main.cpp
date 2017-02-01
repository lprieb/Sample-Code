#include "sudoku.h"
#include <vector>
#include <string>
#include <cstdio>
#include <ctype>


int main(int argc, char* argv)
{
	FILE* puzzle_file;
	puzzle_file = fopen(argv[1], "r");
	char c;
	
	string type;
	type.assign(argv[2]);
	
	
	if(typ == char)
	{
		for(int row = 0; c != EOF, row++)
		{
			for(int col; !isspace(c); col++)
			{
				puzzle[row][col] = c;
			}
			if(puzzle[row].size != 9)
			{
				cout << "File is not formatted correctly" << endl;
				return 1;
			}
		}
		if(puzzle.size != 0)
		{
			cout << " File is not formatted correctly" << endl;
			return 1;		
		}
	}
	
	if(typ == char)
	{
		for(int row = 0; c != EOF, row++)
		{
			for(int col; !isspace(c); col++)
			{
				puzzle[row][col] = c;
			}
			if(puzzle[row].size != 9)
			{
				cout << "File is not formatted correctly" << endl;
				return 1;
			}
		}
		if(puzzle.size != 0)
		{
			cout << " File is not formatted correctly" << endl;
			return 1;		
		}
	}
	
	
	if(type == "int")
	{
		puzzle<int>
	}
	else if(type == "char")
	{
		vector < vector <char> > puzzle;
	}

}
