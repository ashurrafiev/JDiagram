from jdiag import *

from com.xrbpowered.jdiagram.chart import ScatterChart
from com.xrbpowered.jdiagram.chart.ScatterChart import Population

data = Data.range('x', 0, 360)
data.addCol('sin', fn( lambda row : Math.sin(Math.toRadians(row.getNum('x'))) ))
data.addCol('cos', fn( lambda row : Math.cos(Math.toRadians(row.getNum('x'))) ))

chart = ScatterChart().setXRange(0, 360).setYRange(-1, 1)
chart.addPopulation(Population('cos', data, 'x', 'cos', 'fill:none;stroke:#777;stroke-width:1;stroke-dasharray:2 2'))
chart.addPopulation(Population('sin', data, 'x', 'sin', 'fill:none;stroke:#d00;stroke-width:2'))

chart.print(System.out)
