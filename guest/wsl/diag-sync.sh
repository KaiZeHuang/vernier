#!/usr/bin/env bash
# Diagnostic for the supervised AOSP sync service.
# Written as a file on purpose: `wsl.exe -lc '...'` from an MSYS shell eats
# shell variables and nested quotes. Files avoid that entirely.
set -uo pipefail

# Resolve the repository root from this script, so the diagnostic works from any checkout.
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
S="$REPO_ROOT/guest/wsl/sync-aosp.sh"

echo "=== file exists / mtime ==="
ls -la "$S"

echo "=== bash -n syntax check ==="
if bash -n "$S"; then echo "SYNTAX OK"; else echo "SYNTAX ERROR"; fi

echo "=== non-ASCII bytes (should be none) ==="
LC_ALL=C grep -n '[^[:print:][:space:]]' "$S" || echo "(none - good)"

echo "=== repo reachable with our PATH ==="
# A login shell would set these; the diagnostic is often run through
# `wsl.exe -lc`, so set them explicitly. HOME is inherited as-is.
export PATH="$HOME/bin:$HOME/opt/deps/usr/bin:$PATH"
command -v repo || echo "REPO NOT FOUND"

echo "=== unit file on disk ==="
ls -la "$HOME"/.config/systemd/user/aosp-sync.service
echo "--- Environment lines ---"
grep -E '^(Environment|ExecStart)' "$HOME"/.config/systemd/user/aosp-sync.service

echo "=== systemd view ==="
systemctl --user show aosp-sync.service -p Environment -p ExecStart --no-pager

echo "=== service status ==="
systemctl --user status aosp-sync.service --no-pager -n 8 2>&1 | tail -10

echo "=== log tail ==="
tail -6 "$HOME"/sp/aosp-sync.log
