#!/usr/bin/env bash
set -euo pipefail

SCRIPT="/home/ubuntu/app/deploy/backup-mysql-to-s3.sh"
LOG="/home/ubuntu/app/deploy/backup-cron.log"
CRON_LINE="0 3 * * * $SCRIPT >> $LOG 2>&1"

(crontab -l 2>/dev/null | grep -vF "$SCRIPT" || true; echo "$CRON_LINE") | crontab -
echo "Installed: $CRON_LINE"
