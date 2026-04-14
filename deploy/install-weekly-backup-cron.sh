#!/usr/bin/env bash
set -euo pipefail

SCRIPT="/home/ubuntu/app/deploy/backup-mysql-to-s3.sh"
LOG="/home/ubuntu/app/deploy/backup-cron.log"
CRON_LINE="0 3 * * 0 $SCRIPT >> $LOG 2>&1"

if crontab -l 2>/dev/null | grep -F "$SCRIPT"; then
  echo "Cron entry for $SCRIPT already exists."
  exit 0
fi

(crontab -l 2>/dev/null | grep -vF "$SCRIPT" || true; echo "$CRON_LINE") | crontab -
echo "Installed: $CRON_LINE"
