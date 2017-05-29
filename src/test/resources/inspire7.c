#include <stdio.h>
#include <string.h>
#include <stdlib.h>

char* stringConcat(char *s1, char *s2) {
	int length = strlen(s1) + strlen(s2);
	char* str = malloc(length + 1);
	snprintf(str, length+1, "%s%s", s1, s2);
	return str;
}

char* intToString(int value) {
	int length = snprintf(NULL, 0, "%d", value);
	char* str = malloc(length + 1);
	snprintf(str, length+1, "%d", value);
	return str;
}

char* floatToString(float value) {
	int length = snprintf(NULL, 0, "%d", value);
	char* str = malloc(length + 1);
	snprintf(str, length+1, "%f", value);
	return str;
}

int main(int argc, char* argv[]) {
	printf(intToString(10));
	printf(floatToString(10.2));
	printf(stringConcat("ciao","federico"));
	return 0;
}