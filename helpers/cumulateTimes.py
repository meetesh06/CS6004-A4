import re
import csv
import argparse

def extract_running_time(filename):
    with open(filename, 'r') as file:
        lines = file.readlines()
        times = []
        for line in lines:
            match = re.search(r'real\t(\d+m\d+\.\d+s)', line)
            if match:
                time_str = match.group(1)
                minutes, seconds = map(float, time_str.replace('m', ' ').replace('s', '').split())
                total_seconds = (minutes * 60) + seconds
                times.append(total_seconds)
        return times

def write_to_csv(times1, times2, output_filename):
    with open(output_filename, 'w', newline='') as csvfile:
        csvwriter = csv.writer(csvfile)
        csvwriter.writerow(['Baseline', 'Transformed'])
        for t1, t2 in zip(times1, times2):
            csvwriter.writerow([t1, t2])

def main(file1, file2, output_file):
    times1 = extract_running_time(file1)
    times2 = extract_running_time(file2)
    write_to_csv(times1, times2, output_file)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Extract running times from two files and save to a CSV.')
    parser.add_argument('file1', help='Path to the first file')
    parser.add_argument('file2', help='Path to the second file')
    parser.add_argument('output_file', help='Path to the output CSV file')
    args = parser.parse_args()

    main(args.file1, args.file2, args.output_file)
