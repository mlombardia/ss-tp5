from os import times
import math
import numpy as np
from matplotlib import pyplot as plt
from statistics import mean 

file = open("../FractionZ.csv", 'r')
InputLines = file.readlines()

times = []
fz = []

count = 0

for line in InputLines:
    if count >= 1:
        str = line.strip().split(',')
        times.append(float(str[0]))
        str = line.strip().split(',')
        fz.append(float(str[1]))
    count += 1


fig, ax = plt.subplots()

ax.scatter(times, fz)


ax.set_xlabel('Tiempo (s)')
ax.set_ylabel('Fraccion de zombies')



plt.show()
plt.close(fig)