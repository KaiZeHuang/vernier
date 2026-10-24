#!/usr/bin/env bash
# 下载并校验官方 ARM64 GSI。
# 选 CP11.251209.009.A1（Android 16 QPR1）：基于 GKI 6.6，
# 与自编译内核 6.6.102-sp-quiet 同代，VNDK/分区布局匹配度最高。
#
# 关键发现（推翻此前"官方源不通"的判断）：
#   GSI 不在 dl.google.com/android/repository/ 下，而在
#   https://dl.google.com/developers/android/<codename>/images/gsi/
#   googledownloads.cn 是同一份内容的国内镜像域名，同样可用。

set -uo pipefail

CODENAME="baklava"
VER="CP11.251209.009.A1"
FILE="aosp_arm64-exp-${VER}-14840729-59a822d9.zip"
SHA256="59a822d9"

DEST="${DEST:-$HOME/gsi}"
mkdir -p "$DEST"
OUT="$DEST/$FILE"

# 两个等价源，主源失败自动切镜像
HOSTS=(
  "https://dl.google.com/developers/android"
  "https://googledownloads.cn/developers/android"
)

echo "=== GSI download $(date '+%F %T') ==="
echo "file : $FILE"
echo "dest : $OUT"

fetch() {
  for h in "${HOSTS[@]}"; do
    local url="$h/$CODENAME/images/gsi/$FILE"
    echo "--- try $url"
    # -C - 断点续传；--retry 抗瞬断；限速不限，只是加超时
    curl -L -C - --retry 8 --retry-delay 3 --retry-all-errors \
         --connect-timeout 20 --max-time 3600 \
         -o "$OUT" "$url" && return 0
    echo "    host failed, fallback next"
  done
  return 1
}

if [ -f "$OUT" ]; then
  echo "existing file: $(stat -c%s "$OUT") bytes"
else
  fetch || { echo "DOWNLOAD FAILED"; exit 1; }
fi

sz=$(stat -c%s "$OUT")
echo "=== downloaded: $sz bytes ==="

# 校验：官方给的 SHA-256 是文件名前缀的前 8 位
echo "--- sha256 (checking prefix $SHA256) ---"
full=$(sha256sum "$OUT" | cut -d' ' -f1)
echo "sha256 = $full"
case "$full" in
  "$SHA256"*) echo "CHECKSUM OK" ;;
  *) echo "CHECKSUM MISMATCH (expected prefix $SHA256)"; exit 1 ;;
esac

echo "=== unzip ==="
cd "$DEST" || exit 1
unzip -o "$FILE" -d gsi_arm64
echo "=== contents ==="
ls -la gsi_arm64
echo "=== DONE $(date '+%F %T') ==="
