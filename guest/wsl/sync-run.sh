#!/usr/bin/env bash
# Foreground supervised AOSP sync (run via wsl.exe -- bash <this file>).
# ASCII-only comments on purpose.
#
# - No retry loop, no pidfile games: this runs in the foreground of a live
#   session, so WSL keeps the VM alive for the whole run.
# - Output goes straight to the log (no pipes) so nothing is buffered away.
set -u
export HOME="$HOME"
export PATH="$HOME/bin:$HOME/opt/deps/usr/bin:$PATH"

LOG="$HOME"/sp/aosp-sync2.log
cd "$HOME"/sp/aosp || exit 1

echo "repo = $(command -v repo 2>&1)"
if ! command -v repo >/dev/null 2>&1; then
  echo "FATAL: repo not in PATH"
  exit 127
fi

echo "=== sync2 start $(date '+%F %T') ==="
df -m "$HOME"/sp/aosp | tail -1
repo sync -c -j4 --no-clone-bundle --fail-fast --optimized-fetch
RC=$?
echo "=== sync2 exit=$RC $(date '+%F %T') ==="
df -m "$HOME"/sp/aosp | tail -1
echo "projects=$(find "$HOME"/sp/aosp/.repo/projects -maxdepth 3 -name '*.git' -type d 2>/dev/null | wc -l)"
exit $RC
