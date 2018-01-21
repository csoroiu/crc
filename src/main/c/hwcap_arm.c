#ifdef __linux__
#include <unistd.h>
#endif

#include <stdio.h>
#include <sys/auxv.h>
#include <asm/hwcap.h>
#include <stdbool.h>

bool hasPMULL = false;
bool hasCRC32 = false;
bool hasCRC32C = false;

#if defined(__arm64__) || defined(__aarch64__) || defined(_M_ARM64)
        #define __HW_ARM64__ 1
#elif defined(__arm__) || defined(__aarch32__) || defined(_M_ARM)
        #define __HW_ARM32__ 1
#endif

inline bool queryPMULL() {
#if defined(__linux__) && defined(__HW_ARM64__) && defined(__ARM_FEATURE_CRYPTO)
    if (getauxval(AT_HWCAP) & HWCAP_PMULL)
        return true;
#elif defined(__linux__) && defined(__HW_ARM32__) && defined(__ARM_FEATURE_CRYPTO)
    if (getauxval(AT_HWCAP2) & HWCAP2_PMULL)
        return true;
#endif
    return false;
}

inline bool queryCRC32() {
#if defined(__linux__) && defined(__HW_ARM64__) && defined(__ARM_FEATURE_CRC32)
    if (getauxval(AT_HWCAP) & HWCAP_CRC32)
        return true;
#elif defined(__linux__) && defined(__HW_ARM32__) && defined(__ARM_FEATURE_CRC32)
    if (getauxval(AT_HWCAP2) & HWCAP2_CRC32)
        return true;
#endif
    return false;
}

void detectArmFeatures() {
    hasPMULL = queryPMULL();
    hasCRC32 = queryCRC32();
    hasCRC32C = hasCRC32;
}

int main()
{
    detectArmFeatures();
    printf("hasPMULL:  %d\n", hasPMULL);
    printf("hasCRC32:  %d\n", hasCRC32);
    printf("hasCRC32C: %d\n", hasCRC32C);
    return 0;
}
