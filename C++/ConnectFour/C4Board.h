// This Class Creates a board to play connect four on it
#ifndef C4BOARD_H
#define C4BOARD_H


#include "C4Col.h"
#include <iostream>

using namespace std;

class C4Board
{
	friend ostream& operator<<(ostream&, C4Board&);
 public:
  C4Board(); // Constructor
  //~C4Board(); // Destructor
  void display(); // Display Current Board
  void play(); // Manage the game
  int checkWin(); // Checks if player won or not
 private:
  int checkRightDiag(); // Private function to aid checkWin
  int checkLeftDiag(); // Private functino to aid checkWin
  int checkVert(); // Private function to aid checkWin
  int checkHor();
  int numCol; // Stores number of columns in the game
  C4Col Board[7]; // Pointer to dynamically allocated data storing an array of C4Col
};

#endif
