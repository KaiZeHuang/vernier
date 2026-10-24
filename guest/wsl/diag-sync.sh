#!/usr/bin/env bash
# Diagnostic for the supervised AOSP sync service.
# Written as a file on purpose: `wsl.exe -lc '...'` from an MSYS shell eats
# shell variables and nested quotes. Files avoid that entirely.
set -uo pipefail

S=/mnt/c/Users/hesti/Desktop/Vernier-Detector-Refactoring/guest/wsl/sync-aosp.sh

echo "=== file exists / mtime ==="
ls -la "$S"

echo "=== bash -n syntax check ==="
if bash -n "$S"; then echo "SYNTAX OK"; else echo "SYNTAX ERROR"; fi

echo "=== non-ASCII bytes (should be none) ==="
LC_ALL=C grep -n '[^[:print:][:space:]]' "$S" || echo "(none - good)"

echo "=== repo reachable with our PATH ==="
export HOME=/home/dmond
export PATH="/home/dmond/bin:/home/dmond/opt/deps/usr/bin:$PATH"
command -v repo || echo "REPO NOT FOUND"

echo "=== unit file on disk ==="
ls -la /home/dmond/.config/systemd/user/aosp-sync.service
echo "--- Environment lines ---"
grep -E '^(Environment|ExecStart)' /home/dmond/.config/systemd/user/aosp-sync.service

echo "=== systemd view ==="
systemctl --user show aosp-sync.service -p Environment -p ExecStart --no-pager

echo "=== service status ==="
systemctl --user status aosp-sync.service --no-pager -n 8 2>&1 | tail -10

echo "=== log tail ==="
tail -6 /home/dmond/sp/aosp-sync.log
