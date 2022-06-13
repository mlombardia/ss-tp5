from os import times
import math
import numpy as np
from matplotlib import pyplot as plt
from statistics import mean 

# Read input
file = open("tVsN.txt", 'r')
InputLines = file.readlines()

t0 = []
t1 = []
t2 = []
t3 = []
n = []

for line in InputLines:
    str = line.strip().split(' ')
    t0.append(float(str[0]))
    t1.append(float(str[1]))
    t2.append(float(str[2]))
    t3.append(float(str[3]))
    n.append(int(str[4]))

print(t0)
print(t1)
print(t2)
print(t3)

time0 = [t0[0],  t1[0] , t2[0] , t3[0]]
stdDev0 = np.std(time0)
time1 = [t0[1],  t1[1] , t2[1] , t3[1]]
stdDev1 = np.std(time1)
time2 = [t0[2] , t1[2] , t2[2] , t3[2]]
stdDev2 = np.std(time2)
time3 = [t0[3] , t1[3] , t2[3] , t3[3]]
stdDev3 = np.std(time3)


averageTime = [np.average(time0), np.average(time1), np.average(time2), np.average(time3)]
##print(n)
fig, ax = plt.subplots()
ax.scatter(n, averageTime)


##y_error = 20*0.10             ## El 10% de error
y_error = [stdDev0, stdDev1, stdDev2, stdDev3]


plt.errorbar(n,averageTime, yerr = y_error, capsize = 3)
##ax.set_title("Tiempo de corte en funcion de la velocidad del zombie con n=140??")
ax.set_xlabel('Cantidad de partículas')
ax.set_ylabel('Tiempo en llegar a la condicion de corte (s)')

plt.ticklabel_format(style='sci', axis='y', scilimits=(0,0))
ax.yaxis.major.formatter._useMathText = True
plt.show()
plt.close(fig)