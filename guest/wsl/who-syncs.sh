#!/usr/bin/env bash
set -uo pipefail

echo "=== who is actually syncing ==="
ps -eo pid,ppid,etime,args | grep -E 'repo/main.py|repo sync|sync-aosp' | grep -v grep | head -10

echo ""
echo "=== systemd unit state ==="
systemctl --user is-active aosp-sync.service 2>&1
systemctl --user status aosp-sync.service --no-pager -n 5 2>&1 | tail -7

echo ""
echo "=== pidfile vs actual ==="
echo "pidfile: $(cat "$HOME"/sp/aosp-sync.pid 2>/dev/null || echo none)"
ps -p "$(cat "$HOME"/sp/aosp-sync.pid 2>/dev/null || echo 0)" -o pid,args --no-headers 2>/dev/null || echo "(pid from pidfile not alive)"

echo ""
echo "=== heartbeat ==="
cat "$HOME"/sp/aosp-sync.hb 2>/dev/null

echo ""
echo "=== full log (last 25) ==="
tail -25 "$HOME"/sp/aosp-sync.log 2>/dev/null

echo ""
echo "=== size now vs 60s ==="
a=$(du -sm "$HOME"/sp/aosp 2>/dev/null | cut -f1)
sleep 45
b=$(du -sm "$HOME"/sp/aosp 2>/dev/null | cut -f1)
echo "size ${a}MB -> ${b}MB   delta=$((b-a))MB in 45s"
