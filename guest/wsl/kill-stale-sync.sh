#!/usr/bin/env bash
# Kill the stale sync-aosp.sh processes (PPID chain under PID 340/388-1),
# keeping only the systemd-managed one whose pid matches the pidfile.
#
# Why: the old nohup/setsid batches survived in a broken state and kept
# hitting "repo: command not found" against a stale copy of the script on
# /mnt/c, while competing with the real systemd run for the same repo dir.
set -uo pipefail

KEEP="$(cat "$HOME"/sp/aosp-sync.pid 2>/dev/null || echo 0)"
echo "keeping pid=$KEEP"

echo "=== all sync-aosp.sh before ==="
ps -eo pid,ppid,etime,args | grep 'sync-aosp.sh' | grep -v grep

echo ""
echo "=== killing the rest (whole trees, children first) ==="
# Collect every sync-aosp.sh pid except $KEEP, kill children before parents.
PIDS=$(ps -eo pid,args | grep 'sync-aosp.sh' | grep -v grep | awk '{print $1}')
for p in $PIDS; do
  [ "$p" = "$KEEP" ] && { echo "skip $p (systemd-managed)"; continue; }
  # Kill the process group / tree of this stale one
  CHILDREN=$(pgrep -P "$p" 2>/dev/null || true)
  for c in $CHILDREN; do
    GC=$(pgrep -P "$c" 2>/dev/null || true)
    [ -n "$GC" ] && kill -9 $GC 2>/dev/null
    kill -9 "$c" 2>/dev/null
  done
  kill -9 "$p" 2>/dev/null && echo "killed $p"
done

sleep 3
echo ""
echo "=== after ==="
ps -eo pid,ppid,etime,args | grep -E 'sync-aosp.sh|repo/main.py' | grep -v grep | head -10
echo ""
echo "=== real repo procs count ==="
ps -eo args | grep -c 'repo/main.py' || echo 0
echo "=== heartbeat ==="
cat "$HOME"/sp/aosp-sync.hb 2>/dev/null
