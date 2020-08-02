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

print('\nx in [%.5f, %.5f]' % (Fold.min(data, 'x'), Fold.max(data, 'x')))
print('y in [%.5f, %.5f]' % (Fold.min(data, 'y'), Fold.max(data, 'y')))
print('Avg dist = %.5f' % Fold.avg(data, 'dist'))

data.sort(getNum('dist'))
print('\nSorted by dist')
data.print()
