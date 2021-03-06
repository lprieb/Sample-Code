#ifndef PUZZLE_H
#define PUZZLE_H


#include <vector>
#include <cstdio>
#include <cctype>
#include <iostream>
#include "puzzleElement.h"

using namespace std;

template <typename T>
class Puzzle
{
	public:
	Puzzle(FILE *); // constructor
	void interactive();
	void solve();
	private:
	FILE* file;
	vector< vector <puzzleElement> > thePuzzle;
	int size;
	bool checkCol(char, int);
	bool checkRow(char, int);
	bool checkSquare(char, int, int);
	bool checkValue(char, int, int);
	bool checkSolved();
	string check;
	bool checkAllowed(char);
	void setCheck();
	void display();
	// solver functions
	int singleton();
	int single_possibility();
};

template<>
void Puzzle<int>::setCheck()
{
	for(int i = 0; i < 10; i++)
	{
		check.push_back(i + '0'); // convert int to char
	}
}

template<>
void Puzzle<char>::setCheck()
{
	int c = 'a'; // dumb value to start loop
	check.push_back('0'); // Add 0 to the list of possible characters
	
	while(c != '\n')
	{
		c = fgetc(file);
		if(c != ' ')
		{
			check.push_back(c);
		}
	}
}

template<typename T>
Puzzle<T>::Puzzle(FILE* puzzle_file)
{
	file = puzzle_file;
	int c = 5; // Dumb value to initialize loop
	vector <puzzleElement> buffer;
	puzzleElement singleContainer;
	
	setCheck();
	
	for(int row = 0; row< 9; row++)
	{
		buffer.clear();
		c = fgetc(puzzle_file);
		for(int col = 0; c != '\n' && c != EOF; col++)
		{
			if(!checkAllowed(c)) // Check if value is appropriate
			{
				cout << "Wrong value in file" << endl;
				return;
			}
			
			if((char)c == '0')
			{
				singleContainer.setAll(c, 0); // This makes the value not constant
				buffer.push_back(singleContainer);
			}
			else
			{
				singleContainer.setAll(c, 1); // This makes the value constant
				buffer.push_back(singleContainer);
			}
			c = fgetc(puzzle_file); // Catch space
			if(c == ' ')
			{
				c = fgetc(puzzle_file);
			}
		}
		thePuzzle.push_back(buffer);
	}
	if(thePuzzle.size() != 9)
	{
		cout << " File is not formatted correctly" << endl;
		return;		
	}
}

template<typename T>
bool Puzzle<T>::checkCol(char value, int col)
{
		for(int i = 0; i < 9; i++)
		{
			if(value == thePuzzle[i][col].getValue())
				return false;
		}
		return true;
}

template<typename T>
bool Puzzle<T>::checkRow(char value, int row)
{
		for(int i = 0; i < 9; i++)
		{
			if(value == thePuzzle[row][i].getValue())
				return false;
		}
		return true;
}

template<typename T>
bool Puzzle<T>::checkSquare(char value, int row, int col)
{
		// Determine Square
		int i, j;
		
		int rowStart = ((row)/3)*3; // Determines what is the first row of the square in 0-8 range
													// Truncates value and then scales it again
		int colStart = ((col)/3)*3;
		
		for(int i = rowStart; i < (rowStart+3); i++)
		{
			for(int j = colStart; j< (colStart+3); j++)
			{
				if(value == thePuzzle[i][j].getValue())
					return false;
			}
		}
		return true;
}

template<typename T>
bool Puzzle<T>::checkValue(char value, int row, int col)
{
	if(checkCol(value, col) && checkRow(value, row) && checkSquare(value, row, col))
		return true;
	else
		return false;
}

template<typename T>
bool Puzzle<T>::checkSolved()
{
	for(int i = 0; i < 9; i++)
	{
		for(int j = 0; j <9; j++)
		{
			if(thePuzzle[i][j].getValue() == '0')
			{
				return false;
			}
		}
	}
	return true;
}

template<typename T>
void Puzzle<T>::interactive()
{
	char value = 'a'; // dumb value to start loop
	int row;
	int col;
	bool gameover = false;
	
	while((value != 'Q') && !gameover)
	{
		display();
		//prompt user
		cout << "Please Enter Value you wish to input Followed by coordinates" << endl;
		cout << "For Example, to put 5 in position [9,9] write 5 9 9" << endl;
		cout << "If you wish to quit enter Q" << endl;
		cin >> value;
		if(value == 'Q')
		{
			continue;
		}
		cin >> row;
		cin >> col;
		if(!checkAllowed(value))
		{
			cout << "Value is not allowed" << endl;
		}
		else if( thePuzzle[row - 1][col - 1].getIsConstant())
		{
			cout << "Sorry! You can't change the puzzle!" << endl;
		}
		else if (checkValue(value, row - 1, col - 1))
		{
			thePuzzle[row - 1][col - 1].setValue(value);
		}
		else
		{
			cout << "Value cannot be placed there" << endl;
		}
		if(checkSolved())
		{
			gameover = true;
			display();
			cout << "Congratulations! You solved the puzzle!" << endl;
		}
	}
}


template <typename T>
void Puzzle<T>::display()
{
	for(int i = 0; i < 18; i++) // First dividing line
	{
		cout << "-";
	}
	puts(""); // Insert Newline
	for(int row = 0; row < 9; row++)
	{
		for(int col = 0; col < 9; col++)
		{
			cout << thePuzzle[row][col].getValue();
			if((col+ 1)%3 == 0)
			{
				cout << "|";
			}
			else
			{
		 		cout  << " ";
		 	}
			
		}
		cout << endl;
		if(((row + 1) % 3) == 0)
		{
			for(int i = 0; i < 18; i++)
			{
				cout << "-";
			}
			cout << endl;
		}
	}
}

template <typename T>
bool Puzzle<T>::checkAllowed( char c ) // test if value is in the string of possible chars
{
	bool isThere = 0;
	int size =check.size();
	
	for(int i = 0; i < size; i++)
	{
		if (check[i] == c)
		{
			isThere = 1;
		}
	}
	return isThere;
}

template <typename T>
int Puzzle<T>::singleton() // Checks whether a value is only possible in one of the boxes in a row, col, or square
{
	int row, col, inserts, test_num, count;
	int sqrow, sqcol;
	int rightRow, rightCol;
	
	
	inserts=0;
	
	// Check in Rows
	for(row = 0; row < 9; row++)
	{
		for(test_num = 1; test_num < 10; test_num++)
		{
			count = 0;
			for(col = 0; col < 9; col++)
			{
				if(thePuzzle[row][col].getValue() == '0') // Check that location is empty
				{
					if(checkValue(test_num + '0', row, col))
					{
						count++;
						// If there is only one, the following will contain the right position
						rightRow=row;
						rightCol=col;
					}
				}
			}
			if(count == 1) // If only one square can contain the tested number, place is there.
			{
				thePuzzle[rightRow][rightCol].setValue(test_num + '0');
				inserts++;
			}
		}
	}
	
	// Check in Columns
	for(col = 0; col < 9; col++)
	{
		for(test_num = 1; test_num < 10; test_num++)
		{
			count = 0;
			for(row = 0; row < 9; row++)
			{
				if(thePuzzle[row][col].getValue() == '0') // Check that location is empty
				{
					if(checkValue(test_num + '0', row, col))
					{
						count++;
						// If there is only one, the following will contain the right position
						rightRow = row;
						rightCol = col;
					}
				}
			}
			if(count == 1)
			{
				thePuzzle[rightRow][rightCol].setValue(test_num + '0');
				inserts++;
			}
		}
	}
	int rowmax, rowmin;
	int colmax, colmin;
	
	// Check in Square
	for(sqrow=0; sqrow < 3; sqrow++)
	{
		for(sqcol=0; sqcol < 3; sqcol++)
		{
			rowmin = 3*sqrow;
			rowmax = rowmin + 3;
			colmin = 3*sqcol;
			colmax = colmin + 3;
			for(test_num = 1; test_num < 10; test_num++)
			{
				count = 0;
				for(row=rowmin; row < rowmax; row++)
				{
					for(col=colmin; col < colmax; col++)
					{
						if(thePuzzle[row][col].getValue() == '0')
						{
							if(checkValue(test_num + '0', row, col))
							{
								count++;
								// If there is only one, the following will contain the right position
								rightRow=row;
								rightCol=col;
							}
						}
					}
				}
				if(count == 1)
				{
					thePuzzle[rightRow][rightCol].setValue(test_num + '0');
					inserts++;
				}
			}
		}
	}
	
	return inserts;
	
}


template<typename T>
int Puzzle<T>::single_possibility() // Checks 
{
	int row, col, test_num, count, inserts;
	int rightNum;
	inserts=0;
	
	for(row = 0; row < 9; row++)
	{
		for(col = 0; col < 9; col++)
		{
			count = 0;
			if(thePuzzle[row][col].getValue() == '0')
			{
				for(test_num = 1; test_num < 10; test_num++)
				{
					if(checkValue(test_num + '0', row, col))
					{
						count++;
						// If there is only one number that can be placed, the following will contain the right number after the loop
						rightNum=test_num;
					}
				}
				if(count == 1)
				{
					thePuzzle[row][col].setValue(rightNum + '0');
					inserts++;
				}
			}
		}
	}
	return inserts;
}


template<typename T>
void Puzzle<T>::solve()
{
	int inserts;
	do // Do loop until no more insertions are made by any of the algorithms
	{
		inserts = 0;
		inserts += singleton();
		inserts += single_possibility();

	} while (inserts > 0);
	
	if(checkSolved())
	{
		cout << "Here is the solution!" << endl;
		display();
	}
	else
	{
		cout << "A solution could not be found" << endl;
		cout << "This is what we got" << endl;
		display();
	}
}

#endif
	
