from jdiag import *

# Public dataset by City of New York available at
# https://catalog.data.gov/dataset/most-popular-baby-names-by-sex-and-mothers-ethnic-group-new-york-city-8c742
data = Data.read(File('../example_inputs/Popular_Baby_Names.csv'))
data.renameHeaders(['Year', 'Gender', 'Ethnicity', 'Name', 'Count' , 'Rank'])
data.recalc('Name', uppercase(get('Name')))

data.replaceAll('Ethnicity', 'WHITE NON HISPANIC', 'WHITE');
data.replaceAll('Ethnicity', 'WHITE NON HISP', 'WHITE');
data.replaceAll('Ethnicity', 'ASIAN AND PACIFIC ISLANDER', 'ASIAN');
data.replaceAll('Ethnicity', 'ASIAN AND PACI', 'ASIAN');
data.replaceAll('Ethnicity', 'BLACK NON HISPANIC', 'BLACK');
data.replaceAll('Ethnicity', 'BLACK NON HISP', 'BLACK');
# print(Fold.listUniques('Ethnicity', '\n').fold(data))

names = data.groupBy('Name', ['Gender', 'Count', 'Ethnicity'], [Fold.listUniques('Gender', '/'), Fold.sumInt('Count'), Fold.listUniques('Ethnicity', '/')])

names.sort('Gender', 'Name')
names.print()
