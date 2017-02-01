all: spirals

spirals: spirals.c gfx4.o
	gcc $^ -o $@ -lX11 -lm

clean:
	rm spirals
