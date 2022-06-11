from os import times
import math
import numpy as np
from matplotlib import pyplot as plt
from statistics import mean 

## FRACCION DE ZOMBIES A TRAVES DEL TIEMPO PARA DISTINTOS V

## velocidad del zombie = 2
file = open("../FractionZ2.csv", 'r')
InputLines = file.readlines()

times2 = []
fz2 = []
count2 = 0

for line in InputLines:
    if count2 >= 1:
        str = line.strip().split(',')
        times2.append(float(str[0]))
        str = line.strip().split(',')
        fz2.append(float(str[1]))
    count2 += 1


##  velocidad del zombie = 2.5
file = open("../FractionZ25.csv", 'r')
InputLines = file.readlines()

times80 = []
fz80 = []
count80 = 0

for line in InputLines:
    if count80 >= 1:
        str = line.strip().split(',')
        times80.append(float(str[0]))
        str = line.strip().split(',')
        fz80.append(float(str[1]))
    count80 += 1

##  velocidad del zombie = 3
file = open("../FractionZ3.csv", 'r')
InputLines = file.readlines()

times140 = []
fz140 = []
count140 = 0

for line in InputLines:
    if count140 >= 1:
        str = line.strip().split(',')
        times140.append(float(str[0]))
        str = line.strip().split(',')
        fz140.append(float(str[1]))
    count140 += 1


##  velocidad del zombie = 3.5
file = open("../FractionZ35.csv", 'r')
InputLines = file.readlines()

times200 = []
fz200 = []
count200 = 0

for line in InputLines:
    if count200 >= 1:
        str = line.strip().split(',')
        times200.append(float(str[0]))
        str = line.strip().split(',')
        fz200.append(float(str[1]))
    count200 += 1

fig, ax = plt.subplots()


##  velocidad del zombie = 4
file = open("../FractionZ4.csv", 'r')
InputLines = file.readlines()

times260 = []
fz260 = []
count260 = 0

for line in InputLines:
    if count260 >= 1:
        str = line.strip().split(',')
        times260.append(float(str[0]))
        str = line.strip().split(',')
        fz260.append(float(str[1]))
    count260 += 1


##  velocidad del zombie = 4.5
file = open("../FractionZ45.csv", 'r')
InputLines = file.readlines()

times320 = []
fz320 = []
count320 = 0

for line in InputLines:
    if count320 >= 1:
        str = line.strip().split(',')
        times320.append(float(str[0]))
        str = line.strip().split(',')
        fz320.append(float(str[1]))
    count320 += 1

##  velocidad del zombie = 5
file = open("../FractionZ5.csv", 'r')
InputLines = file.readlines()

times320 = []
fz320 = []
count320 = 0

for line in InputLines:
    if count320 >= 1:
        str = line.strip().split(',')
        times320.append(float(str[0]))
        str = line.strip().split(',')
        fz320.append(float(str[1]))
    count320 += 1


ax.plot(times2, fz2)
ax.plot(times80, fz80)
ax.plot(times140, fz140)
ax.plot(times200, fz200)
ax.plot(times260, fz260)
ax.plot(times320, fz320)


ax.set_xlabel('Tiempo (s)')
ax.set_ylabel('Fraccion de zombies')

ax.legend(('vz=2', 'vz=2.5', 'vz=3', 'vz=3.5', 'vz=4', 'vz=4.5', 'vz=5'))


plt.show()
plt.close(fig)