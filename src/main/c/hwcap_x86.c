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

bool probeSSE2() {
    return false;
}

void detectX86Features() {
    word32 cpuid0[4]={0}, cpuid1[4]={0};
    if (!cpuId(1, 0, cpuid1)) {
        return;
    }
    bool hasSSE2 = false;
    // SSE2 supported by CPU
    if ((cpuid1[3] & (1 << 26)) != 0) {
        // XSAVE enabled by OS
        hasSSE2 = ((cpuid1[2] & (1 << 27)) != 0) || probeSSE2();
    }
    #if defined(__SSE4_2__)
        bool hasSSE2 && ((cpuid1[2] & (1<<20)) != 0);
    #else
        bool hasSSE42 = false;
        #warning "-msse4.2 flag not present"
    #endif

    #if defined(__PCLMUL__)
        hasPMULL = hasSSE2 && ((cpuid1[2] & (1<< 1)) != 0);
    #else
        hasPMULL = false;
        #warning "-mpclmul flag not present"
    #endif

    hasCRC32 = false;
    hasCRC32C = hasSSE42;
}

int main()
{
    detectX86Features();
    printf("hasPMULL:  %d\n", hasPMULL);
    printf("hasCRC32:  %d\n", hasCRC32);
    printf("hasCRC32C: %d\n", hasCRC32C);
    return 0;
}
