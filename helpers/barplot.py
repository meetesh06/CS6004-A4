import sys
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.ticker import ScalarFormatter

def plot_data(csv_file, name, output_path):
    # Load the CSV file into a DataFrame
    df = pd.read_csv(csv_file)

    # Check if the DataFrame has the expected columns
    if 'static' not in df.columns or 'virtual' not in df.columns:
        print("Error: CSV file does not have the required columns 'static' and 'virtual'.")
        return

    # Extract data for static and virtual columns
    baseline_data = df[df.index == 0].squeeze()
    transformed_data = df[df.index == 1].squeeze()

    # Set width of bar
    barWidth = 0.35
    br1 = [1, 2]  # Position for Baseline and Transformed
    br2 = [x + barWidth for x in br1]

    # Define shades of blue and a contrasting color (orange)
    blue_colors = ['#1f77b4', '#4c8bff']
    contrast_color = '#ff7f0e'

    # Make the plot with shades of blue and a contrasting color
    bars1 = plt.bar(br1, [baseline_data['static'], transformed_data['static']], color=blue_colors[0], width=barWidth, edgecolor='grey', label='Static')
    bars2 = plt.bar(br2, [baseline_data['virtual'], transformed_data['virtual']], color=contrast_color, width=barWidth, edgecolor='grey', label='Virtual')

    # Adding Xticks and labels
    plt.ylabel('#Calls', fontweight='bold', fontsize=15)
    plt.xticks([1.17, 2.17], ['Baseline', 'Transformed'])
    plt.title('Call site information for ' + name)
    plt.legend()

    # Set y-axis limits based on the maximum value in the DataFrame
    max_value = max(baseline_data.max(), transformed_data.max())  # Get the maximum value across both data series
    plt.ylim(0, max_value + 100000)  # Add some padding to the maximum value

    # Format y-axis tick labels to display in regular notation
    plt.gca().yaxis.set_major_formatter(ScalarFormatter(useMathText=False))

    # Add values on top of the bars
    for bar1, bar2 in zip(bars1, bars2):
        plt.text(bar1.get_x() + bar1.get_width() / 2, bar1.get_height() + 10000, f"{int(bar1.get_height()):,}", ha='center', va='bottom', fontsize=10)
        plt.text(bar2.get_x() + bar2.get_width() / 2, bar2.get_height() + 10000, f"{int(bar2.get_height()):,}", ha='center', va='bottom', fontsize=10)

    plt.savefig(output_path + str(name) + "_callsiteinfo.png", dpi=300)

if __name__ == "__main__":
    if len(sys.argv) != 4:
        print("Usage: python script_name.py <csv_path> <benchmark_name> <output_path>")
        sys.exit(1)

    csv_path = sys.argv[1]
    plot_data(csv_path, sys.argv[2], sys.argv[3])
