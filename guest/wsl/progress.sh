#!/usr/bin/env bash
# Progress check for the AOSP sync, with a real growth measurement.
set -uo pipefail

echo "=== unit state ==="
systemctl --user is-active aosp-sync.service 2>&1
systemctl --user show aosp-sync.service -p MainPID -p NRestarts -p ActiveEnterTimestamp --no-pager 2>&1 | head -5

echo ""
echo "=== real repo processes ==="
ps -eo pid,etime,args | grep 'repo/main.py' | grep -v grep | wc -l
ps -eo pid,etime,args | grep 'git-remote-https\|git fetch' | grep -v grep | head -3

echo ""
echo "=== pidfile ==="
P=$(cat /home/dmond/sp/aosp-sync.pid 2>/dev/null || echo 0)
echo "pid=$P"
ps -p "$P" -o pid,etime --no-headers 2>/dev/null || echo "(pid not alive)"

echo ""
echo "=== heartbeat ==="
cat /home/dmond/sp/aosp-sync.hb 2>/dev/null

echo ""
echo "=== log: attempts and errors ==="
grep -cE '^--- attempt' /home/dmond/sp/aosp-sync.log 2>/dev/null | sed 's/^/attempts: /'
grep -cE 'SYNC OK' /home/dmond/sp/aosp-sync.log 2>/dev/null | sed 's/^/SYNC OK count: /'
echo "--- last 12 log lines ---"
tail -12 /home/dmond/sp/aosp-sync.log

echo ""
echo "=== growth over 60s ==="
a=$(du -sm /home/dmond/sp/aosp 2>/dev/null | cut -f1)
pa=$(find /home/dmond/sp/aosp/.repo/projects -maxdepth 3 -name '*.git' -type d 2>/dev/null | wc -l)
sleep 60
b=$(du -sm /home/dmond/sp/aosp 2>/dev/null | cut -f1)
pb=$(find /home/dmond/sp/aosp/.repo/projects -maxdepth 3 -name '*.git' -type d 2>/dev/null | wc -l)
echo "size ${a}MB -> ${b}MB  (delta $((b-a))MB/min)"
echo "projects ${pa} -> ${pb}  (delta $((pb-pa))/min)"

echo ""
echo "=== disk ==="
df -h /mnt/sp 2>/dev/null | tail -1 || df -h / | tail -1

echo ""
echo "=== key trees present? ==="
for d in build/soong system/core frameworks/native frameworks/base hardware/interfaces packages/modules/VirtualRaller packages/modules/Connectivity device/google; do
  if [ -d "/home/dmond/sp/aosp/$d" ]; then
    n=$(find "/home/dmond/sp/aosp/$d" -maxdepth 2 -type f 2>/dev/null | wc -l)
    echo "  OK   $d ($n files)"
  else
    echo "  MISS $d"
  fi
done
