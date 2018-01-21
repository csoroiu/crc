#include <stdio.h>
#include <arm_acle.h>
#include <arm_neon.h>

int main()
{
  //compile with -march=armv8-a+crc -mtune=cortex-a53 -mfpu=crypto-neon-fp-armv8 -mfloat-abi=hard
  const poly64_t a={0x8000000000000001};
  const poly64_t b={0x8000000000000001};
  uint64x2_t r;
  const poly128_t rr =  vmull_p64(a, b);
  r = (uint64x2_t) rr;
  unsigned long long i1 = vgetq_lane_u64(r,0);
  unsigned long long i2 = vgetq_lane_u64(r,1);
  printf("%016llx %016llx\n",i1,i2);
  return 0;
}
