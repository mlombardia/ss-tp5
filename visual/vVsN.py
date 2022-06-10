from os import times
import math
import numpy as np
from matplotlib import pyplot as plt
from statistics import mean 

file = open("../ContagiousVelocity.csv", 'r')
InputLines = file.readlines()

times = []
vz = []
sum = 0
count = 0

for line in InputLines:
    if count >= 1:
        str = line.strip().split(',')
        times.append(float(str[0]))
        str = line.strip().split(',')
        vz.append(float(str[1]))
        sum += float(str[1])
    count += 1

print(count)
print(sum)
vzProm = (sum / (count-1))

print(vzProm)


# Read input
file = open("vVsN.txt", 'r')
InputLines = file.readlines()

v0 = []
v1 = []
v2 = []
v3 = []
n = []

for line in InputLines:
    str = line.strip().split(' ')
    v0.append(int(str[0]))
    v1.append(int(str[1]))
    v2.append(int(str[2]))
    v3.append(int(str[3]))
    n.append(int(str[4]))


velocity0 = [v0[0],  v1[0] , v2[0] , v3[0]]
stdDev0 = np.std(velocity0)
velocity1 = [v0[1],  v1[1] , v2[1] , v3[1]]
stdDev1 = np.std(velocity1)
velocity2 = [v0[2] , v1[2] , v2[2] , v3[2]]
stdDev2 = np.std(velocity2)
velocity3 = [v0[3] , v1[3] , v2[3] , v3[3]]
stdDev3 = np.std(velocity3)
velocity4 = [v0[4] , v1[4] , v2[4] , v3[4]]
stdDev4 = np.std(velocity4)


averageVelocity = [np.average(velocity0), np.average(velocity1), np.average(velocity2), np.average(velocity3), np.average(velocity4)]
##print(n)
fig, ax = plt.subplots()
ax.scatter(n, averageVelocity)


##y_error = 20*0.10             ## El 10% de error
y_error = [stdDev0, stdDev1, stdDev2, stdDev3, stdDev4]


plt.errorbar(n,averageVelocity, yerr = y_error, capsize = 3)
##ax.set_title("Velocidad de contagio en funcion de la cantidad de humanos con vz=3??")
ax.set_xlabel('Cantidad de humanos')
ax.set_ylabel('Velocidad de contagio (m/s)')

plt.ticklabel_format(style='sci', axis='y', scilimits=(0,0))
ax.yaxis.major.formatter._useMathText = True
plt.show()
plt.close(fig)
