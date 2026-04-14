#!/usr/bin/env bash
set -euo pipefail

BUCKET="${AWS_S3_BUCKET_NAME}"
REGION="${AWS_S3_REGION:-ap-northeast-2}"
STAMP="$(date +%Y%m%d-%H%M%S)"
TMP="/tmp/caps-${STAMP}.sql.gz"

sudo docker exec caps-mysql sh -c \
  'mysqldump -uroot -p"$MYSQL_ROOT_PASSWORD" --single-transaction --routines --triggers caps' \
  | gzip > "$TMP"

aws s3 cp "$TMP" "s3://${BUCKET}/mysql-weekly/caps-${STAMP}.sql.gz" --region "$REGION"
rm -f "$TMP"

echo "Uploaded s3://${BUCKET}/mysql-weekly/caps-${STAMP}.sql.gz"

# 30일이 지난 객체 중, 일요일 백업본만 유지 (나머지 백업본 삭제)
# caps- (운영) 및 예전 caps_dev- 접두어 모두 처리
cleanup_old_non_sunday_backups() {
  local cutoff_ymd ymd dow key
  cutoff_ymd="$(date -d '30 days ago' +%Y%m%d)"

  while IFS= read -r key; do
    [[ -z "${key:-}" ]] && continue
    if [[ "$key" =~ caps-([0-9]{8})- ]]; then
      ymd="${BASH_REMATCH[1]}"
    elif [[ "$key" =~ caps_dev-([0-9]{8})- ]]; then
      ymd="${BASH_REMATCH[1]}"
    else
      continue
    fi
    (( 10#$ymd > 10#$cutoff_ymd )) && continue

    dow="$(date -d "${ymd}" +%w)"
    [[ "$dow" -eq 0 ]] && continue

    echo "Deleting (30d+ old, not Sunday): s3://${BUCKET}/${key}"
    aws s3 rm "s3://${BUCKET}/${key}" --region "$REGION" || true
  done < <(aws s3 ls "s3://${BUCKET}/mysql-weekly/" --recursive --region "$REGION" 2>/dev/null \
    | awk '/(caps|caps_dev)-[0-9]{8}-/ {print $NF}' || true)
}

cleanup_old_non_sunday_backups
