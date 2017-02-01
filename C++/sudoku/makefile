TARGET1 = sudoku_solver
TARGET2 = sudoku_player
DEPENDENCIES = 
SUFFIX = cpp
CC = g++
CFLAGS = -gdwarf-2 -g

all: $(TARGET1) $(TARGET2)

$(TARGET1): $(TARGET1:=.o) $(DEPENDENCIES:=.o)
	$(CC) $(CFLAGS) $^ -o $@

$(TARGET2): $(TARGET2:=.o) $(DEPENDENCIES:=.o)
	$(CC) $(CFLAGS) $^ -o $@

%.o: %.$(SUFFIX)
	$(CC) -c $(CFLAGS) $<

clean:
	rm -f *.o $(TARGET1) $(TARGET2)
