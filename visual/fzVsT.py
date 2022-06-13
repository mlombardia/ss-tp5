from os import times
import math
import numpy as np
from matplotlib import pyplot as plt
from statistics import mean 

## FRACCION DE ZOMBIES A TRAVES DEL TIEMPO PARA DISTINTOS N

## 40 humanos
file = open("../FractionZ40.csv", 'r')
InputLines = file.readlines()

times40 = []
fz40 = []
count40 = 0

for line in InputLines:
    if count40 >= 1:
        str = line.strip().split(',')
        times40.append(float(str[0]))
        str = line.strip().split(',')
        fz40.append(float(str[1]))
    count40 += 1


## 80 humanos
file = open("../FractionZ80.csv", 'r')
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

## 140 humanos
file = open("../FractionZ140.csv", 'r')
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


## 200 humanos
file = open("../FractionZ200.csv", 'r')
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


## 260 humanos
file = open("../FractionZ260.csv", 'r')
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


## 320 humanos
file = open("../FractionZ320.csv", 'r')
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



ax.plot(times40, fz40)
ax.plot(times80, fz80)
ax.plot(times140, fz140)
ax.plot(times200, fz200)
ax.plot(times260, fz260)
ax.plot(times320, fz320)


ax.set_xlabel('Tiempo (s)')
ax.set_ylabel('Fraccion de zombies')

ax.legend(('n=40', 'n=80', 'n=140', 'n=200', 'n=260', 'n=320'))


plt.show()
plt.close(fig)