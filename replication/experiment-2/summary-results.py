#!/usr/bin/env python3
import os
import csv
import json
import re
import sys

ALL_SCENARIOS_CSV = "results-with-build-information.csv"
RESULTS_DIR      = "."
OUTPUT_CSV       = "summary_experiment.csv"

def parse_numbers(s):
    return [int(n) for n in re.findall(r"\d+", s)]

def load_conflicts(base_dir='.'):
    result = {}
    for dirname in os.listdir(base_dir):
        dir_path = os.path.join(base_dir, dirname)
        # consider only subdirectories whose names are all digits
        if os.path.isdir(dir_path) and dirname.isdigit():
            out_file = os.path.join(dir_path, 'out.txt')
            if os.path.isfile(out_file):
                with open(out_file, 'r', encoding='utf-8') as f:
                    content = f.read().strip()
                result[int(dirname)] = 1 if content else 0
            else:
                result[int(dirname)] = 0
    return result

def load_scenarios(path):
    out = []
    with open(path, newline='', encoding='utf-8') as f:
        reader = csv.DictReader(f, delimiter=';')
        for idx, row in enumerate(reader, start=1):
            row['id'] = idx
            out.append(row)
    return out

def is_real_interference(group, left, right):
    has_left = False
    has_right = False
    for item in group:
        ln_str = item.get('line')
        if ln_str is None:
            continue
        try:
            ln = int(ln_str)
        except (ValueError, TypeError):
            continue
        if ln in left:
            has_left = True
        if ln in right:
            has_right = True
        if has_left and has_right:
            return True
    return False

def main():    
    if not os.path.isfile(ALL_SCENARIOS_CSV):
        print(f"Error: File '{p}' not found.", file=sys.stderr)
        sys.exit(1)

    conflicts = load_conflicts()
    scenarios = load_scenarios(ALL_SCENARIOS_CSV)
    summary = []

    for scen in scenarios:
        i = int(scen['id'])+1
        leftChanges  = parse_numbers(scen.get('left modifications', '[]'))
        rightChanges = parse_numbers(scen.get('right modifications', '[]'))
        has_conf = conflicts.get(i, 0)

        rec = {
            'id': i,
            'project': scen.get('project',''),
            'merge commit': scen.get('merge commit',''),
            'className': scen.get('className',''),
            'method': scen.get('method',''),
            'leftChanges': leftChanges,
            'rightChanges': rightChanges,
            'reportedInterferences': has_conf,
            'reported-interferences-count': 0,
            'noRefactoringInterference': 0,
            'isRefactoring': 0 
        }

        if has_conf == 1:
            # Paths
            pi = os.path.join(RESULTS_DIR, str(i), 'potential_interferences.json')
            rif = os.path.join(RESULTS_DIR, str(i), 'refactoring_interferences.json')

            potentials = []
            if os.path.isfile(pi):
                with open(pi, encoding='utf-8') as f:
                    potentials = json.load(f)
            rec['reported-interferences-count'] = len(potentials)

            refints = []
            if os.path.isfile(rif):
                with open(rif, encoding='utf-8') as f:
                    refints = json.load(f)

            if not refints:
                rec['noRefactoringInterference'] = 1
                final = potentials
            else:
                nr = os.path.join(RESULTS_DIR, str(i), 'no_refactoring_interferences.json')
                no_ref_groups = []
                if os.path.isfile(nr):
                    with open(nr, encoding='utf-8') as f:
                        no_ref_groups = json.load(f)
                final = []
                for group in no_ref_groups:
                    if is_real_interference(group, leftChanges, rightChanges):
                        final.append(group)
                rec['noRefactoringInterference'] = 1 if final else 0

            rec['isRefactoring'] = 1 if (has_conf == 1 and rec['noRefactoringInterference'] == 0) else 0

            out_final = os.path.join(RESULTS_DIR, str(i), 'final_interferences.json')
            with open(out_final, 'w', encoding='utf-8') as f_out:
                json.dump(final, f_out, ensure_ascii=False, indent=2)

        summary.append(rec)

    fields = [
        'id','project','merge commit','className','method',
        'leftChanges','rightChanges',
        'reportedInterferences','reported-interferences-count',
        'noRefactoringInterference','isRefactoring'
    ]
    with open(OUTPUT_CSV, 'w', newline='', encoding='utf-8') as f:
        writer = csv.DictWriter(f, fieldnames=fields, delimiter=';')
        writer.writeheader()
        for r in summary:
            row = r.copy()
            row['leftChanges']  = json.dumps(r['leftChanges'], ensure_ascii=False)
            row['rightChanges'] = json.dumps(r['rightChanges'], ensure_ascii=False)
            writer.writerow(row)

    print(f"Finished {OUTPUT_CSV}")

if __name__ == '__main__':
    main()

