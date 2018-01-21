#include <stdio.h>
#include <x86intrin.h>
#include <wmmintrin.h>
#include <immintrin.h>

int main()
{
  unsigned long a=0x8000000000000001;
  unsigned long b=0x8000000000000001;
  __m128i r,v1,v2;
  v1 = _mm_cvtsi64_si128(a);
  v2 = _mm_cvtsi64_si128(b);
  r = _mm_clmulepi64_si128(v1, v2, 0x00);
  unsigned long i1=_mm_cvtsi128_si64(r);
  unsigned long i2=_mm_extract_epi64(r,1);

  printf("%016lx %016lx\n",i1,i2);
  return 0;
}
