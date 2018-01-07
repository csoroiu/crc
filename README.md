#### CRC implementation in Java 
This project contains Java implementation for several CRC algorithms: 
byte-by-byte (Sarwate's algorithm), Slicing-by-8, Slicing-by-16.

I intend to port Interleaved Word By Word algorithm from **crcutil**, 
but performance might be worse than the other algorithms in Java. That algorithm is more suitable for C/C++ where 
we have native access to the data stream. 

Also it contains a factory which, based on JDK will try to provide the appropiate implementation.
This project was build for educational purposes and is far from being complete.  

#### References and resources
1. Sarwate, D.V., "Computation of Cyclic Redundancy Checks via Table Look-Up", Communications of the ACM, 31(8), pp.1008-1013, 1988.
2. Andrew Kadatch and Bob Jenkins. [High performance CRC implementation](https://code.google.com/archive/p/crcutil), 2010.
3. Mark Adler. [crcany and crcgen](https://github.com/madler/crcany) - Compute any CRC and generate C code for any CRC definition.
4. Mark Adler. [How CRC combine works](https://stackoverflow.com/a/23126768/2749648).
5. Ross Williams. [A PAINLESS GUIDE TO CRC ERROR DETECTION ALGORITHMS](https://zlib.net/crc_v3.txt), 1993.
6. Greg Cook. [Catalogue of parametrised CRC algorithms](http://reveng.sourceforge.net/crc-catalogue/).
7. [Highly optimized CRC32C lib and benchmark](https://github.com/htot/crc32c).
8. Michael E. Kounavis and Frank L. Berry. [A systematic approach to building high performance, software-based, CRC generators](https://static.aminer.org/pdf/PDF/000/432/446/a_systematic_approach_to_building_high_performance_software_based_crc.pdf), 2005. Slicing-by-4, Slicing-by-8.
   [original link](http://www.intel.com/technology/comms/perfnet/download/CRC_generators.pdf). (Slicing-by-4, Slicing-by-8).
9. Nicolai Stange. [Combining and splitting CRCs](https://www.nicst.de/crc.pdf), 2015.

10. Intel. [Choosing a CRC polynomial and associated method for Fast CRC Computation on Intel® Processors](https://www.intel.com/content/dam/www/public/us/en/documents/white-papers/fast-crc-computation-paper.pdf), 2012.
11. Intel. [Fast CRC Computation for iSCSI Polynomial Using CRC32 Instruction](https://www.intel.com/content/dam/www/public/us/en/documents/white-papers/crc-iscsi-polynomial-crc32-instruction-paper.pdf), 2011.
12. Intel. [Fast CRC Computation for Generic Polynomials Using PCLMULQDQ Instruction](https://www.intel.com/content/dam/www/public/us/en/documents/white-papers/fast-crc-computation-generic-polynomials-pclmulqdq-paper.pdf), 2009.
13. Intel. [PCLMULQDQ Based CRC PoC](https://github.com/intel/soft-crc).
14. Anton Blanchard. [Accelerated CRC32 for POWER8 using vpmsum instructions](https://github.com/antonblanchard/crc32-vpmsum).
15. [Linux kernel x86 SSE4.2 PCLMULQDQ and CRC32](https://github.com/torvalds/linux/tree/master/arch/x86/crypto).
16. [Linux kernel ARMv8 PMULL/NEON and CRC32/CRC32C - port of Intel's SSE4.2 algorithm](https://github.com/torvalds/linux/blob/master/arch/arm/crypto).
17. [Linux kernel POWER8 VPMSUM CRC](https://github.com/torvalds/linux/tree/master/arch/powerpc/crypto).
18. [Linux kernel SPARC CRC32C](https://github.com/torvalds/linux/tree/master/arch/powerpc/crypto).
19. [Linux kernel IBM/Z VGFMAG](https://github.com/torvalds/linux/tree/master/arch/s390/crypto).

20. [CRC Polynomials Evaluation Software](https://users.ece.cmu.edu/~koopman/crc/hdlen.html).
21. Generate Verilog or VHDL code for CRC. [CRC Generation Tool](http://www.easics.com/webtools/crctool).
22. Philip Koopman, Carnegie Mellon University. [CRC Polynomials Evaluation Software](https://users.ece.cmu.edu/~koopman/crc/hdlen.html).
23. [Fast CRC table construction and rolling CRC hash calculation](https://encode.ru/threads/1698-Fast-CRC-table-construction-and-rolling-CRC-hash-calculation).
24. [Fast CRC32](http://create.stephan-brumme.com/crc32/).