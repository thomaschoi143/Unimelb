voronoi1: main.o input.o dcel.o summary.o
	gcc -g -Wall -o voronoi1 main.o input.o dcel.o summary.o
main.o: main.c input.h dcel.h summary.h
	gcc -g -Wall -o main.o main.c -c
input.o: input.c input.h dcel.h
	gcc -g -Wall -o input.o input.c -c
dcel.o: dcel.c dcel.h
	gcc -g -Wall -o dcel.o dcel.c -c
summary.o: summary.c input.h dcel.h summary.h
	gcc -g -Wall -o summary.o summary.c -c