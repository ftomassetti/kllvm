#include <stdio.h>
#include <string.h>
#include <stdlib.h>

int i = 8;
float f = 0.3f;
int j;

int recognizeEvent(char* line) {
	if (strcmp(line, "time\n")==0) {
		return 0;
	}
	return 1;	
}

void processEvent(int event) {

}

int main(int argc, char* argv[]) {
	char *line = NULL;
	size_t size;
	int res;
	while (1) {
		getline(&line, &size, stdin);
		res = recognizeEvent(line);
		if (res != -1) {
			printf("");
		} else {
			processEvent(res);
		}
	}
	return 0;
}