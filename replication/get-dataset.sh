#!/usr/bin/env bash
set -euo pipefail

# Uso: ./fetch-files.sh <zip_url>
if [ "$#" -ne 1 ]; then
  echo "Usage: $0 <zip_url>"
  exit 1
fi

ZIP_URL="$1"
OUTPUT_DIR="output/files"
ZIP_FILE="$(mktemp --suffix=-repo.zip)"
TMP_DIR="$(mktemp -d)"

trap 'rm -rf "$ZIP_FILE" "$TMP_DIR"' EXIT

echo "Preparing '$OUTPUT_DIR'..."
rm -rf "$OUTPUT_DIR"
mkdir -p "$OUTPUT_DIR"

echo "Downloading ZIP archive from $ZIP_URL..."
curl -sL "$ZIP_URL" -o "$ZIP_FILE"

echo "Unzipping archive..."
unzip -q "$ZIP_FILE" -d "$TMP_DIR"

echo "Copying files to '$OUTPUT_DIR/'..."
cp -a "$TMP_DIR/." "$OUTPUT_DIR/"

echo "✔ Done. All files are now in '$OUTPUT_DIR/'."

