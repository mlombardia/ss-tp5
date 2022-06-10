from os import times
import math
import numpy as np
from matplotlib import pyplot as plt
from statistics import mean 

# Read input
file = open("tVsVz.txt", 'r')
InputLines = file.readlines()

t0 = []
t1 = []
t2 = []
t3 = []
vz = []

for line in InputLines:
    str = line.strip().split(' ')
    t0.append(int(str[0]))
    t1.append(int(str[1]))
    t2.append(int(str[2]))
    t3.append(int(str[3]))
    vz.append(int(str[4]))


velocity0 = [t0[0],  t1[0] , t2[0] , t3[0]]
stdDev0 = np.std(velocity0)
velocity1 = [t0[1],  t1[1] , t2[1] , t3[1]]
stdDev1 = np.std(velocity1)
velocity2 = [t0[2] , t1[2] , t2[2] , t3[2]]
stdDev2 = np.std(velocity2)
velocity3 = [t0[3] , t1[3] , t2[3] , t3[3]]
stdDev3 = np.std(velocity3)
velocity4 = [t0[4] , t1[4] , t2[4] , t3[4]]
stdDev4 = np.std(velocity4)


averageVelocity = [np.average(velocity0), np.average(velocity1), np.average(velocity2), np.average(velocity3), np.average(velocity4)]
##print(n)
fig, ax = plt.subplots()
ax.scatter(vz, averageVelocity)


##y_error = 20*0.10             ## El 10% de error
y_error = [stdDev0, stdDev1, stdDev2, stdDev3, stdDev4]


plt.errorbar(vz,averageVelocity, yerr = y_error, capsize = 3)
##ax.set_title("Tiempo de corte en funcion de la velocidad del zombie con n=140??")
ax.set_xlabel('Velocidad activa del zombie')
ax.set_ylabel('Tiempo en llegar a la condicion de corte (s)')

plt.ticklabel_format(style='sci', axis='y', scilimits=(0,0))
ax.yaxis.major.formatter._useMathText = True
plt.show()
plt.close(fig)