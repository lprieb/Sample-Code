#ifndef PUZZLEELEMENT_H
#define PUZZLEELEMENT_H

using namespace std;

class puzzleElement
{
	public:
	puzzleElement();
	void setValue(int); // Returns true if position could be placed.
	char getValue();
	void setAll(char, bool);
	bool getIsConstant(); 
	void operator =(char);
	void solve();
	
	private:

	bool isConstant; // Allows you to change or not the value if it is part of the puzzle
	char value;
};


puzzleElement::puzzleElement()
{
	value = '0';
	isConstant = 0;
}

void puzzleElement::setValue(int c)
{
	value = c;
}

char puzzleElement::getValue()
{
	return value;
}

bool puzzleElement::getIsConstant()
{
	return isConstant;
}

void puzzleElement::setAll(char c, bool nIsConstant)
{
	setValue(c);
	isConstant = nIsConstant;
}

#endif

