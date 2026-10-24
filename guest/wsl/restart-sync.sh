#!/usr/bin/env bash
# Reset and restart the supervised AOSP sync service cleanly.
# Run as a file, never as `wsl.exe -lc '...'`.
set -uo pipefail

echo "=== stop service ==="
systemctl --user stop aosp-sync.service 2>&1
sleep 2
systemctl --user reset-failed aosp-sync.service 2>&1

echo "=== clear stale state ==="
rm -f /home/dmond/sp/aosp-sync.pid
rm -f /home/dmond/sp/aosp-sync.hb
# Truncate the noisy retry log so the next run is unambiguous.
: > /home/dmond/sp/aosp-sync.log

echo "=== confirm nothing left ==="
systemctl --user is-active aosp-sync.service 2>&1 || true
ps -eo pid,args | grep -E 'sync-aosp|repo sync' | grep -v grep || echo "(no stale procs)"

echo "=== reset systemd unit env (drop WSL inherited PATH junk) ==="
systemctl --user daemon-reload

echo "=== start fresh ==="
systemctl --user start aosp-sync.service
sleep 20

echo "=== state after 20s ==="
systemctl --user is-active aosp-sync.service
echo "--- pidfile ---"; cat /home/dmond/sp/aosp-sync.pid 2>/dev/null || echo "(none)"
echo "--- heartbeat ---"; cat /home/dmond/sp/aosp-sync.hb 2>/dev/null || echo "(none)"
echo "--- log ---"; cat /home/dmond/sp/aosp-sync.log
echo "--- procs ---"
ps -eo pid,etime,args | grep -E 'repo|_git' | grep -v grep | head -6 || echo "(none)"
