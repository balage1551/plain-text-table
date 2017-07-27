# Plain text table formatter

## Change log

### Version 2.0.0 (2017-07-27 16:54:15.53)

#### New features

- New preset: empty formatting (no border, padding, lines)
- Adding headerConverter to formatter
- CsvExample added
- Support for turning off (hiding) header line
- InputBuilder introduced

#### Backward incompabilities

- Multiple aggregation rows (subtotals)

### Version 1.3.2 (2017-07-03 15:28:51.251)

#### Bugfixes

- NPE on column width calculation is fixed
- Empty elipsis sign is now allowed
- TrivialDataConverter is now returns null for nulls, instead of empty string for consistency

### Version 1.3.1 (2017-06-30 12:18:49.857)

#### Bugfixes

- minimal bugfixes in build script

### Version 1.3.0 (2017-06-30 12:10:17.023)

#### New features

- BorderFormatter.fromPreset shortcut function added
- ColumnDefinition.createSimpleStateless function overload with alignment

### Version 1.2.0

#### New features

-  `BorderFormatter.fromPreset` shortcut function added
- `ColumnDefinition.createSimpleStateless` function overload with alignment

### Version 1.1.0

*I was tool lazy to make one.  :-(*
