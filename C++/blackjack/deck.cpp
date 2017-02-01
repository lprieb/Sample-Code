#include "deck.h"
#include <deque> 
#include <algorithm>
#include <cstdlib>
#include <ctime>
#include <iostream>
#include <iterator>

using namespace std;

cardDeck::cardDeck(int n)
{
	// Seed srand for shuffling
	srand(time(NULL));
	for(int i = 0; i < n; i++)
	{
		push_back(i);
	}
	size = n;
}

int cardDeck::getSize() const
{
	return size;
}

void cardDeck::shuffle()
{
	random_shuffle(begin(), end());
}

bool cardDeck::inOrder() const
{
	bool isInOrder = 1; //assume true until proven otherwise
	deque<int>::const_iterator it = begin();
	deque<int>::const_iterator finish = (end() - 1);
	while(it != finish && isInOrder)
	{
		if( *it > *(it+1))
		{
			isInOrder = 0;
		}
		else
		{
			it++;
		}
	}
	
	return isInOrder;
}


ostream& operator <<(ostream& s, const cardDeck& myDeck)
{
	ostream_iterator<int> output(s, ", ");
	copy(myDeck.begin(), myDeck.end(), output);
	s << endl;
	return s;
}

void cardDeck::printReverse() const
{
	cardDeck reverseDeck = *this; // Copy deck
	
	reverse(reverseDeck.begin(), reverseDeck.end());
	
	cout << reverseDeck; //Make use of overloaded function
}


void cardDeck::monkeySort()
{
	int shufflings = 0;
	//cout << "starting deck: " << *this << endl;
	// See note below
	
	while(!inOrder())
	{
		shuffle();
		shufflings++;
	}
	
	cout << shufflings << " shuffles were performed to sort the " << size << " member deck" << endl;
}

int cardDeck::deal()
{
	int dealtCard = (*this)[0];
	erase(begin());
	size--;
	if(size < 15)
		newDeck();
	return dealtCard;
}

void cardDeck::newDeck()
{
	deque<int>::iterator it = (end() - 1); // Store position of last int
	for(int i = 0; i < size; i++) // Create new deck
	{
		push_back(i);
	}
	// Shuffle new deck
	random_shuffle(it, end());
}

void cardDeck::addCard(int newCard)
{
	size++;
	push_back(newCard);
}

void cardDeck::printRealCards() const
{	
	for(int i = 0; i < size; i++)
	{
		
		cout << ((*this)[i]%13 + 1)  << ", ";
	}
	cout << endl;
}

void cardDeck::clean()
{
	clear();
	size = 0;
}
