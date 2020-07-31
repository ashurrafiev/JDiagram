from jdiag import *

from java.util import Random

random = Random()
numFmt = '%.5f'

data = Data(['label', 'x', 'y'])
for i in range(10):
	data.addRow(
		chr(i+ord('A')),
		numFmt % random.nextDouble(),
		numFmt % random.nextDouble()
	)

def calcDist(row):
	x = row.getNum('x')
	y = row.getNum('y')
	return Math.sqrt(x*x+y*y)

data.addCol('dist', format(numFmt, fn(calcDist)))
	
data.print()

data.sort(getNum('dist'))
print('\nSorted by dist')
data.print()
