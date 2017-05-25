#include <stdio.h>

static const int F = 70;

int main(int argc, char* argv[]) {
	if (3 != argc) {
		fprintf(stderr, "ARGS: expected %d, found %d\n", 3, argc);
		return 1;
	}
	return 0;
}