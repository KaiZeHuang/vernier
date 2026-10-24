#!/usr/bin/env bash
# Read the GSI ext4 image with debugfs (no mount, no root, no loop device).
set -uo pipefail

IMG=/mnt/c/Users/hesti/gsi-win/gsi_arm64/system.img
OUT="$HOME/gsi-dump.txt"

command -v debugfs >/dev/null 2>&1 || { echo "debugfs not installed"; exit 1; }

echo "=== filesystem superblock ==="
debugfs -R "stats" "$IMG" 2>/dev/null | grep -iE "Filesystem volume name|Inode count|Block count|Free blocks|Block size|Filesystem features|Revision level|Filesystem created|Last mount time"

echo ""
echo "=== root dir listing ==="
debugfs -R "ls -l /" "$IMG" 2>/dev/null | head -30

echo ""
echo "=== /system ==="
debugfs -R "ls -l /system" "$IMG" 2>/dev/null | head -40

echo ""
echo "=== /system/bin/init ==="
debugfs -R "stat /system/bin/init" "$IMG" 2>/dev/null | head -8

echo ""
echo "=== /system/build.prop (head) ==="
debugfs -R "cat /system/build.prop" "$IMG" 2>/dev/null | grep -iE "ro.product.cpu.abi|ro.build.version.release|ro.build.version.sdk|ro.zygote|ro.build.type|ro.debuggable|ro.secure" | head -12

echo ""
echo "=== framework jar count ==="
debugfs -R "ls /system/framework" "$IMG" 2>/dev/null | grep -c "\.jar"

echo ""
echo "=== app count ==="
debugfs -R "ls /system/app" "$IMG" 2>/dev/null | grep -c "\.apk"

echo ""
echo "=== SELinux ==="
debugfs -R "ls /system/etc/selinux" "$IMG" 2>/dev/null | head -6

echo ""
echo "=== apex count (APEX runtime mounts) ==="
debugfs -R "ls /system/apex" "$IMG" 2>/dev/null | grep -c "\.apex"
