#include "sudoku_solver.h"
#include <string>
#include <cstdio>
#include <iostream>

using namespace std;

int main(void)
{
	FILE* puzzle_file;
	
	char* file_name = new char[30];	
	
	cout << "Please enter file name for the sudoku" << endl;
	cin >> file_name;
	

	if((puzzle_file = fopen(file_name, "r")) == NULL)
	{
		cout << "Could not open file" << endl;
		return 1;
	}
	
	char c;

	Puzzle<int> thePuzzle(puzzle_file);
	
	thePuzzle.solve();
	
	fclose(puzzle_file);
	
	delete [] file_name;
}
