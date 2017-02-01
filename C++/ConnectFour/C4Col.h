// This Class defines a column to be used in the Connect Four Game Implementation
#ifndef C4COL_H
#define C4COL_H
class C4Col
{
 public:
  C4Col(); // constructor
  //~C4Col(); // Destructor
  int isFull(); // Function to know if column is full
  char getDisc(int); // Function to get a char from the column of chars
  int getMaxDisc(); // Get max number of disc in a column
  C4Col operator +=(char);
  void addDisc(char); // Add Disc to column
 private:
  int discsInCol; // Says the current number of discs in the column
  int maxDiscs; // Stores max number of discs in column
  char asciiDiscs[6]; // Pointer to dynamically allocated memory storing array of chars
};

#endif
