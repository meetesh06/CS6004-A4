import sys

def read_data(file_path):
    data = []
    with open(file_path, 'r') as file:
        for line in file:
            line = line.strip()
            if line:  # Skipping empty lines
                static_calls, virtual_calls = map(int, line.split(','))
                data.append((static_calls, virtual_calls))
    return data

def calculate_column_sums(data):
    static_sum = sum(row[0] for row in data)
    virtual_sum = sum(row[1] for row in data)
    return static_sum, virtual_sum

def main():
    if len(sys.argv) != 2:
        print("Usage: python script_name.py file_path")
        return

    file_path = sys.argv[1]
    data = read_data(file_path)
    if data:
        static_sum, virtual_sum = calculate_column_sums(data)
        print(str(static_sum)+","+str(virtual_sum))
    else:
        print("No data found in the file.")

if __name__ == "__main__":
    main()
