# Plain text table formatter

## What PTT is?

This is a lightweight and highly customizable, flexible and extensible data to plain text table converter. 

**<u>Main functional features:</u>**

- **Direct user object to table conversion:** no wrapping or intermediate classes are required. Any valid Java class is accepted.
- **Separated configuration and processing:** easy reuse of configuration.
- **Separated conversion steps:** the formatting is done in separated and independent steps. Each step has a well defined scope of responsibility, keeping them simple and interoperable.
- **Different layout presets:** you can pick from one of the out-of-box layouts and use it as is, or tweak it for your needs.
- **Sensible default values for easy prototyping:** use defaults for quick output then fine tune it later.
- **Stateful columns and aggregation:** columns may maintain state information and provide aggregated values (such as sum).

**<u>Main architectural features:</u>**

- **Modular configuration**: splits the conversion process into steps, which makes it easy to add your implementation to the point you wish without much boiler code.
- Most of the configuration classes are **imutables** for wide and safe reusability.
- Configuration based on **builder pattern** and **fluent API** concepts to make configurations... well fluent. 
- Based on Java 8: **closures** makes configuration easy and flexible without restrictions or tight coupling.
- **Ultra lightweight:** the main jar is less than 100k, no external library requirements.

## What PTT isn't?

PTT follows a concept to allow easy user data to table conversion for any possible data and separates the processing and configuration phase. This brings many benefits, but brings up its own limits.

<u>**This library can't and won't in the near future:**</u>

- Handle cell spans either horizontal (column span) or vertical (row span).

## Changelog

You can find the version history and change log in [changelog.md](changelog.md).

## Roadmap

These are the planned features for the next releases:

- [ ] CSV export
- [ ] Ignore header (headless tables)
- [ ] Export/inport configuration
- [ ] Multiple aggregation lines/subtotal rows

## The concept behind

The concept behind the desing is to allow a fluent way to present any table-like data in plain text. To achieve flexibility while keeping simplicity, the formatting process is divided into several steps:

1. **Data extract**: the cell value is extracted from the input record (which may be any Java class). This is the only step depends on your business data structure.
2. **Data conversion**: Then convert this value to string while applying type specific formatting on it.
3. **Cell content formatting**: Decorate the converted value to match the column requirements.
4. **Table build:** build up the table from the cell contents and the border rules.

These four steps are independent and may be configured independently. You may use the default formatting of your numeric data, but apply special cell content formatting.

To understand the whole process helps configuring the table.

## A simple example

This example introduces the structure of the code, without the intention to be a complete overview of the features.

Let's start with creating a very simple business class.

```java
    private class JavaDocDemoRecord {
        private String fruit;
        private Double quantity;

        public JavaDocDemoRecord(String fruit, Double quantity) {
            super();
            this.fruit = fruit;
            this.quantity = quantity;
        }

        public String getFruit() {
            return fruit;
        }

        public Double getQuantity() {
            return quantity;
        }
    }
```

We also will need a state class to aggregate the quantities:

```java
    private class JavaDocDemoAggregator {
        public double sum;
    }

```

Now we can configure the table. Let's see the whole configuration first then we will walk over it:

```java
TableFormatter<JavaDocDemoRecord> formatter = new TableFormatter.Builder<JavaDocDemoRecord>()
    .withHeading("Java doc demo")
    .withShowAggregation(true)
    .withSeparateDataWithLines(true)
    .withBorderFormatter(new BorderFormatter.Builder(DefaultFormatters.ASCII_LINEDRAW_DOUBLE)
    			.build())
    .withColumn(new ColumnDefinition.StatelessBuilder<JavaDocDemoRecord, String>()
                .withTitle("Fruit")
                .withAggregateRowConstant("TOTAL")
                .withDataExtractor(o -> o.getFruit())
                .withCellContentFormatter(
                		new CellContentFormatter.Builder().withMinWidth(8).build())
                .build())
    .withColumn(new ColumnDefinition.StatefulBuilder<JavaDocDemoRecord,
                JavaDocDemoAggregator, Double>()
                .withTitle("Quantity")
                .withCellContentFormatter(CellContentFormatter.rightAlignedCell())
                .withDataConverter(NumberDataConverter.defaultDoubleFormatter())
                .withDataExtractor(new StatefulDataExtractor<>((o, s) -> {
                  double v = o.getQuantity();
                  s.sum += v;
                  return v;
                }, () -> new JavaDocDemoAggregator(), (s) -> s.sum))
                .build())
    .build();
```

Let's see this in details:

First of all, we will have a `TableFormatter` instance for our business class `JavaDocDemoRecord`. 

```java
	.withHeading("Java doc demo")
```

Each table may have a heading, which is technically a titlebar for the table. This heading is displayed at the top of the table, above the header line as a single column line. By giving it a value implicitly allowing it to be displayed.

```java
	.withShowAggregation(true)
```

By default, there is no aggregation line added to the table, but we would like to display a sum, so we turn it on.

```java
	.withSeparateDataWithLines(true)
```

We could choose to add or omit lines between rows of data. By default, there will be no lines added, but we turn it on.

```java
	.withBorderFormatter(new BorderFormatter.Builder(DefaultFormatters.ASCII_LINEDRAW_DOUBLE)
    			.build())
```

For this example, we would use a border preset without any further customization.

```java
 	.withColumn(new ColumnDefinition.StatelessBuilder<JavaDocDemoRecord, String>()
                [...]
                .build())
```

Now we add our first column. It is a string column and we don't want to maintain any state information so we can use the simplified `StatelessBuilder`. Let's see the other configuration options of this column:

```java
                .withTitle("Fruit")
```

We give the column a title. This will be displayed in the header row.

```java
                .withAggregateRowConstant("TOTAL")
```

By default, the aggregated value of any non-stateful column is null. But we can specify a string constant instead as we do it here.

```java
              .withDataExtractor(o -> o.getFruit())  
```

Here is our connection to the business class. The data extractor is an implementation of the `DataExtractor` interface, but for stateless columns, we could inject our closure directly.

```java
	.withCellContentFormatter(
                		new CellContentFormatter.Builder().withMinWidth(8).build())
```

Finally, we define the cell formatter. We will use the default formatter (left aligned), but give the column a minimal width constraint.

Now, let's see the other column!

```java
	.withColumn(new ColumnDefinition.StatefulBuilder<JavaDocDemoRecord,
                JavaDocDemoAggregator, Double>()
                [...]
                .build())
```

This column will be stateful, so we have to use the `StatefulBuilder` class instead of the `StatelessBuilder`. We specified our `JavaDocDemoAggregator` class as the state class.

Let's look into the fields of this configuration!

```java
                .withTitle("Quantity")
```

There is nothing new in setting the title of the column.

```java
            	.withCellContentFormatter(CellContentFormatter.rightAlignedCell())
```
Here we will use the default right aligned cell formatter.

```java
            	.withDataConverter(NumberDataConverter.defaultDoubleFormatter())
```
We will use the default double formatter with its default settings (2 fractional digits).

```java
            .withDataExtractor(new StatefulDataExtractor<>((o, s) -> {
              double v = o.getQuantity();
              s.sum += v;
              return v;
            }, () -> new JavaDocDemoAggregator(), (s) -> s.sum))
```
The data extractor of this column is slightly more complex. We have to maintain the state object, by adding the quantity to it. Also a state object initilaizer and aggregation extraction closures had to be provided.

That's all! Our table is comnfigured and ready to be used.

Let's produce some input values:

```java
    List<JavaDocDemoRecord> data = new ArrayList<>();
    data.add(new JavaDocDemoRecord("apple", 120.5d));
    data.add(new JavaDocDemoRecord("banana", 20.119d));
    data.add(null);
    data.add(new JavaDocDemoRecord("cherry", 1551d));	
```

Note the third null value. This marks a place where separator line should be inserted. The separator line is differs from the inter-row line we allowed for the table. A separator line makes it possible to insert lines into the output between rows to split it into parts.

Now, we have a configured table formatter  and some input data. We have nothing else but to let the formatter to do its task:

```java
        String s = formatter.apply(data);
        System.out.println(s);	
```

We are ready. The output on the console would be something like this:

```
 +=====================+
 | Java doc demo       |
 +==========+==========+
 | Fruit    | Quantity |
 +==========+==========+
 | apple    |   120,50 |
 +----------+----------+
 | banana   |    20,12 |
 +==========+==========+
 | cherry   |  1551,00 |
 +==========+==========+
 | TOTAL    |  1691,62 |
 +==========+==========+
```

Let's just alter the configuration by choosing another border preset and removing the in-row lines:

```java
TableFormatter<JavaDocDemoRecord> formatter = new TableFormatter.Builder<JavaDocDemoRecord>()
	.withHeading("Java doc demo")
    .withShowAggregation(true)
//  .withSeparateDataWithLines(true)
    .withBorderFormatter(new BorderFormatter.Builder(DefaultFormatters.NO_VERTICAL).build())
```

The output would be this:

```
---------------------
 Java doc demo       
---------------------
 Fruit      Quantity 
---------- ----------
 apple        120,50 
 banana        20,12 
---------- ----------
 cherry      1551,00 
---------- ----------
 TOTAL       1691,62 
---------------------
```

Make one more run: if we have a unicode output, let's switch to the preset `DefaultFormatters.UNICODE_LINEDRAW` and run the formatter again.

```
╔═════════════════════╗
║ Java doc demo       ║
╠══════════╤══════════╣
║ Fruit    │ Quantity ║
╠══════════╪══════════╣
║ apple    │   120,50 ║
║ banana   │    20,12 ║
╠══════════╪══════════╣
║ cherry   │  1551,00 ║
╠══════════╪══════════╣
║ TOTAL    │  1691,62 ║
╚══════════╧══════════╝
```

*(Note: the break of the lines is due to the line spacing if Markdown)*

This demostration is only showed the basics of the framework. For more detailed guide see [Ptt Gide](doc/index.md).