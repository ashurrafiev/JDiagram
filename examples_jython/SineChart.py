from jdiag import *

from com.xrbpowered.jdiagram.chart import ScatterChart
from com.xrbpowered.jdiagram.chart.ScatterChart import Population

data = Data.range('x', -180, 180)
data.addCol('sin', fn( lambda row : Math.sin(Math.toRadians(row.getNum('x'))) ))
data.addCol('cos', fn( lambda row : Math.cos(Math.toRadians(row.getNum('x'))) ))

chart = ScatterChart()
chart.axisx.setRange(-180, 180, 45).setNumberFmt('%.0f&#xb0;')
chart.axisy.setRange(-1, 1, 0.5).setNumberFmt('%.1f')
chart.addPopLegend(Population(data, 'x', 'cos', 'fill:none;stroke:#777;stroke-width:1;stroke-dasharray:2 2'))
chart.addPopLegend(Population(data, 'x', 'sin', 'fill:none;stroke:#d00;stroke-width:2'))

chart.printPage(System.out)
