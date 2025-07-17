#!/usr/bin/env bash
JDK21_BIN=/usr/lib/jvm/java-21-openjdk-amd64/bin/java
set -euo pipefail

DATASET_ZIP_URL="https://anonymous.4open.science/api/repo/mergedataset-9828/zip"
./get-dataset.sh "$DATASET_ZIP_URL"

chmod +x output/files/prepareDataset.sh
bash output/files/prepareDataset.sh

INPUT_CSV="experiment-1/results-with-build-information.csv"
OUTPUT_DIR="output/data"
OUTPUT_CSV="$OUTPUT_DIR/results-with-build-information.csv"
RESULTS_DIR="experiment-1"

mkdir -p "$RESULTS_DIR"

HEADER=$(head -n1 "$INPUT_CSV")

counter=2

while IFS= read -r line; do
  echo "=== Scenario #$counter ==="

  {
    echo "$HEADER"
    echo "$line"
  } > "$OUTPUT_CSV"

  rm -f out.json out.txt outConsole.txt

  ./gradlew --no-daemon --console=plain clean run \
    -DmainClass="services.outputProcessors.soot.Main" \
    --args="-icf -ioa -idfp -pdg" < /dev/null

  SCEN_DIR="$RESULTS_DIR/$counter"
  mkdir -p "$SCEN_DIR"

  cp out.json out.txt outConsole.txt "$SCEN_DIR/" || true

  echo "=> results in $SCEN_DIR/"

  SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
  
  "$JDK21_BIN" -jar dependencies/refactoring-analyser-1.0-SNAPSHOT-jar-with-dependencies.jar \
     "$SCRIPT_DIR/experiment-1" "$counter"

  echo
  ((counter++))
done < <(tail -n +2 "$INPUT_CSV")

echo "==> All ids processed."

echo "==> Running summary-results.py"
(cd "$RESULTS_DIR" && python3 summary-results.py)
