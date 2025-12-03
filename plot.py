import matplotlib.pyplot as plt

weights = [2106.0, 2415.6, 2283.6]
cgs = [40.57, 41.52, 41.15]
labels = ['Zero Fuel', 'Takeoff', 'Landing']

plt.figure(figsize=(6,6))
plt.scatter(cgs, weights)

for cg, wt, lbl in zip(cgs, weights, labels):
    plt.text(cg, wt, lbl, fontsize=10, ha='left', va='bottom')

plt.vlines(x=35, ymin=1500, ymax=1950, color='black', linestyle='-', linewidth=2)
plt.vlines(x=40.5, ymin=1500, ymax=2200, color='black', linestyle='-', linewidth=2)
plt.vlines(x=47.5, ymin=1500, ymax=2550, color='black', linestyle='-', linewidth=2)

plt.hlines(y=2200, xmin=37.5, xmax=40.5, color='black', linestyle='-', linewidth=2)
plt.hlines(y=2550, xmin=41, xmax=47.5, color='black', linestyle='-', linewidth=2)

x = [35, 41, 47.5]
y = [1950, 2550, 2550]
plt.plot(x,y, color='black',linewidth=2)

plt.xlabel("CG Location (inches)")
plt.ylabel("Weight (lbs)")
plt.title("Weight vs CG (Auto-Generated)")
plt.xlim(34,48)
plt.ylim(1500,2600)
plt.grid(True)
plt.show()
plt.savefig('wb_plot.png', dpi=150, bbox_inches='tight')
