

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
sort_types = data['Sort Type'].unique()

# Combined (all sort types) per comparator
for comparator in comparators:
	subset = data[data['Comparator'] == comparator]
	plt.figure(figsize=(10, 6))
	sns.lineplot(
		data=subset,
		x='Table Size',
		y='Average Time (ns)',
		hue='Sort Type'
	)
	plt.title(f'Sorting Performance: {comparator}')
	plt.xlabel('Table Size')
	plt.ylabel('Average Time (ns)')
	plt.legend(title='Sort Type')
	plt.tight_layout()
	safe_name = str(comparator).replace(' ', '_').replace('/', '_')
	plt.savefig(f'plots/combined/sorting_performance_{safe_name}.png', dpi=300)
	plt.close()

# Separate plots for quick and insertion
for sort_type in ['quick', 'insertion']:
	for comparator in comparators:
		subset = data[(data['Comparator'] == comparator) & (data['Sort Type'] == sort_type)]
		if subset.empty:
			continue
		plt.figure(figsize=(10, 6))
		sns.lineplot(
			data=subset,
			x='Table Size',
			y='Average Time (ns)'
		)
		plt.title(f'{sort_type.capitalize()} Sort Performance: {comparator}')
		plt.xlabel('Table Size')
		plt.ylabel('Average Time (ns)')
		plt.tight_layout()
		safe_name = str(comparator).replace(' ', '_').replace('/', '_')
		plt.savefig(f'plots/{sort_type}/sorting_performance_{safe_name}.png', dpi=300)
		plt.close()

