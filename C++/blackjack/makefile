all: blackjack

blackjack: deck.o blackjack.o
	g++ -gwardf-2 -g $^ -o $@

%.o: %.cpp
	g++ -c  -gdwarf-2 -g  $<

clean:
	rm -f *.o blackjack
