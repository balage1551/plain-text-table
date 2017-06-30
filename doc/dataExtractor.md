# The data extractor

The purpose of the data extractor is to establish a bridge between your business objects and the table. 

Any implementation should extend the `DataExtractor<D, S, T>` abstract class. The data extractors came in two flavours: *stateless* and *stateful*. Stateless extractor keeps no state between processing each record of input, while the stateful extractor can update a state between each call. This allows defining moving windows and aggregated columns. 

## The stateless extractor

The stateless extractor is simple. It takes a 