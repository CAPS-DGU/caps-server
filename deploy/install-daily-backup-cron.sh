#!/usr/bin/env bash
set -euo pipefail

SCRIPT="/home/ubuntu/app/deploy/backup-mysql-to-s3.sh"
LOG="/home/ubuntu/app/deploy/backup-cron.log"
# 매일 03:00 (서버 로컬 타임존). 기존 동일 스크립트 줄은 제거 후 한 줄만 유지
CRON_LINE="0 3 * * * $SCRIPT >> $LOG 2>&1"

(crontab -l 2>/dev/null | grep -vF "$SCRIPT" || true; echo "$CRON_LINE") | crontab -
echo "Installed: $CRON_LINE"
