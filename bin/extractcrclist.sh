#!/bin/sh
#inspired from https://github.com/madler/crcany/blob/master/getcrcs
curl http://reveng.sourceforge.net/crc-catalogue/all.htm | grep -ioP "<code>\s*\Kwidth=.*(?=\s*</code>)"
