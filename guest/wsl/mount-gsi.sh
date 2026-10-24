#!/usr/bin/env bash
# Mount the GSI ext4 system.img read-only and report what is actually inside.
set -uo pipefail

IMG=/mnt/c/Users/hesti/gsi-win/gsi_arm64/system.img
MNT="$HOME/gsi-mount"

echo "=== free space ==="
df -h /mnt | tail -1

echo ""
echo "=== mount ro ==="
mkdir -p "$MNT"
sudo -n mount -o ro,loop "$IMG" "$MNT" 2>&1 || {
  echo "sudo mount failed, trying unshare user namespace"
  unshare -m -r bash -c "mount -o ro,loop '$IMG' '$MNT' && ls '$MNT' | head -40"
  exit 0
}

echo "=== top level ==="
ls "$MNT" | head -40

echo ""
echo "=== system/build.prop ABI + version ==="
grep -E "ro.product.cpu.abi|ro.build.version.release=|ro.build.version.sdk=|ro.zygote" "$MNT/system/build.prop" 2>/dev/null | head -10

echo ""
echo "=== init / linker present? ==="
ls -la "$MNT/system/bin/init" "$MNT/system/bin/linker64" "$MNT/system/bin/logcat" 2>/dev/null

echo ""
echo "=== framework jars count ==="
ls "$MNT/system/framework"/*.jar 2>/dev/null | wc -l

echo ""
echo "=== apks count ==="
ls "$MNT/system/app"/*.apk 2>/dev/null | wc -l

echo ""
echo "=== SELinux mode ==="
ls -la "$MNT/system/etc/selinux" 2>/dev/null | head -3

echo ""
echo "=== df inside image ==="
df -h "$MNT" | tail -1

echo ""
echo "=== unmount ==="
sudo -n umount "$MNT" 2>&1 && echo "unmounted"
