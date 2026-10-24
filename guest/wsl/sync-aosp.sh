#!/usr/bin/env bash
# Supervised AOSP sync: repo sync with crash retry + progress heartbeat.
#
# ASCII-only comments on purpose. Non-ASCII text in this file gets mangled
# when the script is executed through wsl.exe from an MSYS shell (the
# comment lines turn into garbage shell fragments and break parsing).
#
# Why not `pgrep -f 'repo sync'` to check liveness:
#   that pattern also matches the shell command line that invoked it,
#   producing false positives. Use the pidfile + kill -0 instead.
#
# Why the retry loop:
#   1) repo sync exits entirely when a single remote project fails;
#   2) the 33GB already fetched in .repo/project-objects is reused,
#      so re-running resumes instead of restarting.

set -uo pipefail

# systemd PATH is minimal: repo lives in ~/bin, zero-sudo deps in ~/opt/deps.
export HOME="${HOME:-/home/dmond}"
export PATH="/home/dmond/bin:/home/dmond/opt/deps/usr/bin:$PATH"

# Refuse to run if repo is still not resolvable, instead of burning 40 retries.
if ! command -v repo >/dev/null 2>&1; then
  echo "FATAL: repo not found in PATH: $PATH"
  exit 127
fi
echo "repo = $(command -v repo)"

AOSP="${AOSP:-/home/dmond/sp/aosp}"
LOG=/home/dmond/sp/aosp-sync.log
PIDFILE=/home/dmond/sp/aosp-sync.pid
HEARTBEAT=/home/dmond/sp/aosp-sync.hb

mkdir -p /home/dmond/sp
cd "$AOSP" || { echo "no such dir: $AOSP"; exit 1; }

# Avoid concurrent syncs.
if [ -f "$PIDFILE" ] && kill -0 "$(cat "$PIDFILE")" 2>/dev/null; then
  echo "already running pid=$(cat "$PIDFILE")"
  exit 0
fi
echo $$ > "$PIDFILE"

size_of()    { du -sm "$AOSP" 2>/dev/null | cut -f1; }
proj_count() { find "$AOSP/.repo/projects" -maxdepth 3 -name '*.git' -type d 2>/dev/null | wc -l; }

# Heartbeat loop: real progress for external polling. No non-ASCII.
(
  while kill -0 "$$" 2>/dev/null; do
    printf '%s size=%sMB projects=%s\n' \
      "$(date '+%F %T')" "$(size_of)" "$(proj_count)" > "$HEARTBEAT.tmp"
    mv "$HEARTBEAT.tmp" "$HEARTBEAT"
    sleep 120
  done
) &
HB=$!
trap 'kill $HB 2>/dev/null; rm -f "$PIDFILE"' EXIT

{
  echo ""
  echo "=== supervised sync start $(date '+%F %T') ==="
  echo "aosp=$AOSP"
  echo "free=$(df -m "$AOSP" | tail -1 | awk '{print $4}')MB"

  ATTEMPT=0
  MAX=40
  while [ "$ATTEMPT" -lt "$MAX" ]; do
    ATTEMPT=$((ATTEMPT + 1))
    echo ""
    echo "--- attempt $ATTEMPT @ $(date '+%F %T') ---"
    echo "before: size=$(size_of)MB projects=$(proj_count)"

    # -c                 current branch only (less time and disk)
    # -j4                parallelism
    # --no-clone-bundle  skip Git bundles (partial-clone already fetched)
    # --fail-fast        surface errors early
    # No pipe on purpose: a pipe buffers and swallows the error output.
    repo sync -c -j4 --no-clone-bundle --fail-fast
    RC=$?

    echo "--- attempt $ATTEMPT exit=$RC @ $(date '+%F %T') ---"
    echo "after: size=$(size_of)MB projects=$(proj_count)"

    if [ "$RC" -eq 0 ]; then
      echo "=== SYNC OK after $ATTEMPT attempts ==="
      break
    fi

    WAIT=$(( ATTEMPT < 10 ? ATTEMPT * 5 : 120 ))
    echo "retry in ${WAIT}s"
    sleep "$WAIT"
  done

  echo "=== supervised sync end $(date '+%F %T') ==="
} >> "$LOG" 2>&1
