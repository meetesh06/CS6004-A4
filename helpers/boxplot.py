import csv
import sys
import matplotlib.pyplot as plt

def read_data(file_path):
    baseline = []
    transformed = []
    with open(file_path, 'r') as csv_file:
        csv_reader = csv.reader(csv_file)
        next(csv_reader)  # Skip header
        for row in csv_reader:
            baseline.append(float(row[0]))
            transformed.append(float(row[1]))
    return baseline, transformed

def create_boxplot(baseline, transformed, name, output_path):
    # Create a boxplot
    plt.boxplot([baseline, transformed])
    plt.xticks([1, 2], ['Baseline', 'Transformed'])
    plt.title('Boxplot of ' + name)
    plt.ylabel('Seconds')
    plt.savefig(output_path + str(name) + "_runtimes.png", dpi=300)
    # plt.show()

if __name__ == "__main__":
    if len(sys.argv) != 4:
        print("Usage: python script_name.py <csv_path> <benchmark_name> <output_path>")
    else:
        file_path = sys.argv[1]
        baseline, transformed = read_data(file_path)
        create_boxplot(baseline, transformed, sys.argv[2], sys.argv[3])
