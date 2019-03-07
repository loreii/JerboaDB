# JerboaDB
Jerboa DB is a viewer that rely on JavaFX and JDBC for fetching data from a SQL Server and display themin a 3D chart based on jzy3d porject.

The application is alittle bit more than an example but mighr be useful if you want to understand performances of queries and plottign Numerical data in a chart. I performed a couple of tests with a 1Mln result and the plot goes smoothly. 

## Snapshots
![3d plot](media/plot.jpg?raw=true)
![custom sql](media/sql.jpg?raw=true)
![performance chart](media/perf.jpg?raw=true)


## Things to do 

* clean up the code
* refactor the package
* use dao for a more generic database support
* support for not numerical data in the plot
* automatic switch or selector between charts types
* ~~add custom query editor~~
* ~~add performance charts~~

