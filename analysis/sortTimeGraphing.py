

import os
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns


data = pd.read_csv('sorting_performance.csv')

# Ensure column names are stripped of whitespace
data.columns = data.columns.str.strip()


# Ensure output directories exist
os.makedirs('plots/combined', exist_ok=True)
os.makedirs('plots/quick', exist_ok=True)
os.makedirs('plots/insertion', exist_ok=True)

comparators = data['Comparator'].unique()
sort_types = data['Algorithm'].unique()

# Combined (all sort types) per comparator
for comparator in comparators:
	subset = data[data['Comparator'] == comparator]
	plt.figure(figsize=(10, 6))
	sns.lineplot(
		data=subset,
		x='Table Size',
		y='Average Time (ns)',
		hue='Algorithm'
	)
	plt.title(f'Sorting Performance: {comparator}')
	plt.xlabel('Table Size')
	plt.ylabel('Average Time (ns)')
	plt.legend(title='Algorithm')
	plt.tight_layout()
	safe_name = str(comparator).replace(' ', '_').replace('/', '_')
	plt.savefig(f'plots/combined/sorting_performance_{safe_name}.png', dpi=300)
	plt.close()

# Separate plots for each algorithm
for algorithm in sort_types:
	for comparator in comparators:
		subset = data[(data['Comparator'] == comparator) & (data['Algorithm'] == algorithm)]
		if subset.empty:
			continue
		# Ensure the output directory exists for this algorithm
		output_dir = f'plots/{algorithm}'
		os.makedirs(output_dir, exist_ok=True)
		plt.figure(figsize=(10, 6))
		sns.lineplot(
			data=subset,
			x='Table Size',
			y='Average Time (ns)'
		)
		plt.title(f'{algorithm.capitalize()} Sort Performance: {comparator}')
		plt.xlabel('Table Size')
		plt.ylabel('Average Time (ns)')
		plt.tight_layout()
		safe_name = str(comparator).replace(' ', '_').replace('/', '_')
		plt.savefig(f'{output_dir}/sorting_performance_{safe_name}.png', dpi=300)
		plt.close()

