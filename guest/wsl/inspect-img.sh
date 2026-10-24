#!/usr/bin/env bash
# Inspect the GSI system.img container format and filesystem type.
# The first sectors are zeroed, so probe further in for the magic.
set -uo pipefail

# No default: point GSI_IMAGE at your extracted image, or pass it as $1.
IMG="${1:-${GSI_IMAGE:-}}"
if [ -z "$IMG" ]; then
  echo "usage: $0 /path/to/system.img  (or export GSI_IMAGE=...)" >&2
  exit 2
fi
echo "=== file: $IMG"
ls -la "$IMG"

echo ""
echo "=== ext4 magic 0xEF53 (offset 0x438 within superblock at 1024) ==="
od -A d -t x1 -j 1080 -N 2 "$IMG" 2>/dev/null

echo ""
echo "=== sparse magic 0xED26FF3A at offset 0 ==="
od -A d -t x4 -N 4 "$IMG" 2>/dev/null

echo ""
echo "=== android boot / avb footer search (last 64KiB, strings) ==="
tail -c 65536 "$IMG" 2>/dev/null | strings -n 6 | tail -20

echo ""
echo "=== qcow2 magic 'QFI' at 0 ==="
od -A d -t x1 -N 4 "$IMG" 2>/dev/null

echo ""
echo "=== first non-zero byte position ==="
od -A d -t x1 "$IMG" 2>/dev/null | awk '$2!="00" && $3!="00" {print "first nonzero at offset " $1; exit}'

echo ""
echo "=== partition table? (MBR/GPT) ==="
od -A d -t x1 -j 446 -N 16 "$IMG" 2>/dev/null
