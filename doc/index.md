# Plain-text Table Formatter Guide

## The concept behind

The concept behind the desing is to allow a fluent way to present any table-like data in plain text. To achieve flexibility while keeping simplicity, the formatting process is divided into several steps:

1. **Data extract**: the cell value is extracted from the input record (which may be any Java class). This is the only step depends on your business data structure.
2. **Data conversion**: Then convert this value to string while applying type specific formatting on it.
3. **Cell content formatting**: Decorate the converted value to match the column requirements.
4. **Table build:** build up the table from the cell contents and the border rules.

These four steps are independent and may be configured independently. You may use the default formatting of your numeric data, but apply special cell content formatting.

To read more about the above steps, follow the links below:

- [Data extraction](dataExtractor.md)
- ​