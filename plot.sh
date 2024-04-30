#!/bin/bash
for folder in benchmarks/*/; do
  # Extract folder name from the full path
  folder_name=$(basename "$folder")
  echo "(>)[Processing benchmark: $folder_name]"
  python3 helpers/boxplot.py $folder/final_times.csv $folder_name $folder
  python3 helpers/barplot.py $folder/final_call_siteinfo.csv $folder_name $folder
  echo "(/>)[Successful]"
done

