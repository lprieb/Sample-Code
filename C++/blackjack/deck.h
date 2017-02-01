#ifndef DECK_H
#define DECK_H
#include <deque>
#include <iostream>

using namespace std;

class cardDeck: public deque<int>
{
friend ostream& operator <<(ostream&, const cardDeck&);
public:
	cardDeck(int n = 52);
	int getSize() const;
	void shuffle();
	bool inOrder() const;
	void printReverse() const;
	void monkeySort();
	int deal();
	void newDeck(); // Function to add new deck at the end of the deck. Deck is automatically shuffled.
	void addCard(int); // Funtion to add card at the end of the deck
	void printRealCards() const;
	void clean();
	
private:
	int size;
	
};

#endif
