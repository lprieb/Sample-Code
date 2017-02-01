#include "deck.h"
#include <iostream>

using namespace std;

void display_cards(const cardDeck&, const cardDeck&);
bool hit_or_stand(bool* quit); // Takes input from user
void dealer_play(cardDeck& Dealer, cardDeck& Main); // Manages dealer's gameplay
int sum_cards(const cardDeck& current); // Gives you the current sum of the cards
void decide_and_display_winner(const cardDeck&, const cardDeck&, int* playerWins, int* dealerWins);
bool to_continue();

int main(void)
{
	int playerWins, dealerWins;
	cardDeck Dealer(0), Player(0);
	cardDeck main(52);
	main.shuffle();
	bool gameover = 0;
	playerWins = 0;
	dealerWins = 0;
	bool hit;
	
	while(!gameover)
	{
		// Clear decks
		Dealer.clean();
		Player.clean();
		//Deal two cards to dealer and player
		Dealer.addCard(main.deal());
		Dealer.addCard(main.deal());
		
		Player.addCard(main.deal());
		Player.addCard(main.deal());
		
		// Play game
		display_cards(Dealer, Player);	
		hit = hit_or_stand(&gameover);
		while(hit)
		{
			Player.addCard(main.deal());
			display_cards(Dealer, Player);
			hit = hit_or_stand(&gameover);
		}
		
		// Let the dealer play
		dealer_play(Dealer, main);
		decide_and_display_winner(Dealer, Player, &playerWins, &dealerWins);
		if(!gameover)
			gameover = to_continue();
	}
}

void display_cards(const cardDeck& Dealer, const cardDeck& Player)
{
	cout << "First of dealer's card : " << Dealer[0]%13 << endl;
	cout << "Your current cards: " << endl;
	Player.printRealCards();
}


bool hit_or_stand(bool * quit) // Returns true if player wishes to hit
{
	char input = 'n'; // allows the loop to start in case it was somehow initialiazed to h or s
	cout << "Write h to hit, s to stand, q to quit" << endl;
	cin >> input;
	
	while(input != 'h'&& input != 's' && input != 'q') // Check appropriate input
	{
		cout << "Please enter an appropripriate character" << endl << endl;
		cout << "Write h to hit, s to stand, q to " << endl;
		cin >> input;
	}
	
	if(input == 'h')
		return 1;
	else if(input == 'q')
	{
		*quit = 1;
		return 0;
	}
	else
		return 0;

}

int sum_cards(const cardDeck& current)
{
	int sum = 0;
	int size = current.getSize();
	int conversion;
	for(int i = 0; i < size; i++)
	{
		conversion = current[i] % 13;
		if(conversion >= 9) // The 9 card actually represents the value of card 10
			sum += 10;
		else if(conversion > 0)
			sum += conversion + 1;
		else
			sum += 11;
	}	
	return sum;
}

void dealer_play(cardDeck& Dealer, cardDeck& Main)
{
	int sum = sum_cards(Dealer);
	while(sum < 17)
	{
		Dealer.addCard(Main.deal());
		sum = sum_cards(Dealer);
	}
	cout << "The dealers hand is: " << endl;
	Dealer.printRealCards();
	return;
}

void decide_and_display_winner(const cardDeck& Dealer, const cardDeck& Player, int* playerWins, int* dealerWins)
{
	int Dealer_sum = sum_cards(Dealer);
	int Player_sum = sum_cards(Player);
	
	if(Dealer_sum == Player_sum || ((Dealer_sum > 21) && (Player_sum > 21)))
		cout << "Game was a tie!" << endl;
	else if(Player_sum == 21)
	{
		cout << "Player got a blackjack!" << endl;
		(*playerWins)++;
	}
	else if(Player_sum > 21)
	{
		cout << "Player got more than 21 and lost!" << endl;
		(*dealerWins)++;
	}
	else if((Player_sum < Dealer_sum)  && (Dealer_sum <= 21))
	{
		cout << "Dealer beat the player" << endl;
		(*dealerWins)++;
	}
	else
	{
		cout << "The player won!" << endl;
		(*playerWins)++;
	}
	cout << "Current Score: " << endl;
	cout << "Dealer has won " << *dealerWins << " times" << endl;
	cout << "Player has won " << *playerWins << " times" << endl;
	
}

bool to_continue()
{
	char input = 'n'; 
	cout << "Do you wish to end the game or continue?" << endl;
	cout << "Type 'c' to continue and 'q' to exit" << endl;
	cin >> input;
	while(input!= 'c' && input != 'q')
	{
		cout << "Please input an appropriate character" << endl;
		cout << "Type 'c' to continue and 'q' to exit" << endl;
		cin >> input;
	}
	
	if(input == 'c')
		return 0; // continue
	else
		return 1; // gameover
}
