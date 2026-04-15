#!/usr/bin/env bash
set -euo pipefail

BUCKET="${AWS_S3_BUCKET_NAME}"
REGION="${AWS_S3_REGION:-ap-northeast-2}"
STAMP="$(date +%Y%m%d-%H%M%S)"
KEY="mysql-weekly/caps-${STAMP}.sql.gz"

precheck() {
  [[ -n "${BUCKET:-}" ]] || {
    echo "ERROR: AWS_S3_BUCKET_NAME is empty." >&2
    exit 1
  }

  sudo -n docker ps --format '{{.Names}}' | grep -Fx 'caps-mysql' >/dev/null 2>&1 || {
    echo "ERROR: container 'caps-mysql' is not running." >&2
    exit 1
  }
}

precheck

sudo -n docker exec caps-mysql sh -c 'mysqldump -uroot -p"$MYSQL_ROOT_PASSWORD" --single-transaction --routines --triggers caps' | gzip | aws s3 cp - "s3://${BUCKET}/${KEY}" --region "$REGION"

echo "Uploaded s3://${BUCKET}/${KEY}"

# 30일이 지난 객체 중, 일요일 백업만 유지 (나머지 백업 삭제)
cleanup_old_non_sunday_backups() {
  local cutoff_ymd ymd dow key
  cutoff_ymd="$(date -d '30 days ago' +%Y%m%d)"

  while IFS= read -r key; do
    [[ -z "${key:-}" ]] && continue
    [[ "$key" =~ caps-([0-9]{8})- ]] || continue
    ymd="${BASH_REMATCH[1]}"
    (( 10#$ymd > 10#$cutoff_ymd )) && continue

    dow="$(date -d "${ymd}" +%w)"
    [[ "$dow" -eq 0 ]] && continue

    echo "Deleting (30d+ old, not Sunday): s3://${BUCKET}/${key}"
    if ! aws s3 rm "s3://${BUCKET}/${key}" --region "$REGION"; then
      echo "WARN: failed to delete s3://${BUCKET}/${key}" >&2
    fi
  done < <(aws s3 ls "s3://${BUCKET}/mysql-weekly/" --recursive --region "$REGION" 2>/dev/null \
    | awk '/caps-[0-9]{8}-/ {print $NF}' || true)
}

cleanup_old_non_sunday_backups
