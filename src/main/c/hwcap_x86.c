#ifdef __linux__
#include <unistd.h>
#endif

#include <stdio.h>
#include <x86intrin.h>
#include <wmmintrin.h>
#include <immintrin.h>
#include <stdbool.h>

bool hasPMULL = false;
bool hasCRC32 = false;
bool hasCRC32C = false;

inline bool queryPMULL() {
#if defined(__PCLMUL__)
    if (_may_i_use_cpu_feature(_FEATURE_PCLMULQDQ))
        return true;
#else
#warning "-mpclmul flag not present"
#endif
    return false;
}

inline bool queryCRC32C() {
#if defined(__SSE4_2__)
    if (_may_i_use_cpu_feature(_FEATURE_SSE4_2))
        return true;
#else
#warning "-msse4.2 flag not present"
#endif
    return false;
}

void detectX86Features() {
    hasPMULL = queryPMULL();
    hasCRC32 = false;
    hasCRC32C = queryCRC32C();
}

int main()
{
    detectX86Features();
    printf("hasPMULL:  %d\n", hasPMULL);
    printf("hasCRC32:  %d\n", hasCRC32);
    printf("hasCRC32C: %d\n", hasCRC32C);
    return 0;
}
