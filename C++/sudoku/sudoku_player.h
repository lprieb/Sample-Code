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
	private:
	FILE* file;
	vector< vector <puzzleElement> > thePuzzle;
	int size;
	bool checkCol(char, int);
	bool checkRow(char, int);
	bool checkSquare(char, int, int);
	bool checkSolved();
	string check;
	bool checkValue(char);
	void setCheck();
	void display();
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
			if(!checkValue(c)) // Check if value is appropriate
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
			if(value == thePuzzle[i][col - 1].getValue())
				return false;
		}
		return true;
}

template<typename T>
bool Puzzle<T>::checkRow(char value, int row)
{
		for(int i = 0; i < 9; i++)
		{
			if(value == thePuzzle[row - 1][i].getValue())
				return false;
		}
		return true;
}

template<typename T>
bool Puzzle<T>::checkSquare(char value, int row, int col)
{
		// Determine Square
		int i, j;
		
		int rowStart = ((row-1)/3)*3; // Determines what is the first row of the square in 0-8 range
													// Truncates value and then scales it again
		int colStart = ((col-1)/3)*3;
		
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
		if(!checkValue(value))
		{
			cout << "Value is not allowed" << endl;
		}
		else if( thePuzzle[row - 1][col - 1].getIsConstant())
		{
			cout << "Sorry! You can't change the puzzle!" << endl;
		}
		else if (checkCol(value,col) &&
						checkRow(value, row) &&
						checkSquare(value, row, col))
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
bool Puzzle<T>::checkValue( char c ) // test if value is in the string of possible chars
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


	
