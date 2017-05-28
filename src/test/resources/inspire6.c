#include <stdio.h>
#include <string.h>
#include <stdlib.h>

void error(const char* topic, const char* message) {
	fprintf(stderr, "%s: %s\n", topic, message);
	exit(1);
}

int parseInt(char * inputName, char* s) {
	if (strlen(s) == 0) {
		error(inputName, "empty string");
	}
	int total = 0;
	for (int i=0;i<strlen(s);i++) {
		char c = s[i];
		if (c < '0' || c > '9') {
			error(inputName, "not a number");		
		}
		total = total * 10 + (c - '0');
	}
	return total;
}

float parseFloat(char * inputName, char* s) {
	if (strlen(s) == 0) {
		error(inputName, "empty string");
	}
	float total = 0;
	int foundDot = 0;
	int factor = 1;
	for (int i=0;i<strlen(s);i++) {
		char c = s[i];
		if (foundDot==0 && c == '.') {
			foundDot = 1;
		} else {
			if (c < '0' || c > '9') {
				error(inputName, "not a number");		
			}
			total = total * 10 + (c - '0');
			if (foundDot == 1) {
				factor = factor * 10;
			}
		}
	}
	return total / factor;
}

int main(int argc, char* argv[]) {
	int input_a = parseInt("input a", argv[1]);
	printf("input a: %d\n", input_a);
	float input_b = parseFloat("input b", argv[2]);
	printf("input b: %f\n", input_b);
	char* input_c = argv[3];
	printf("input c: %s\n", input_c);
	return 0;
}