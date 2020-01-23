ArgumentParser
=================================================

For this homework, you will create a parser for command-line arguments. The code should parse flag/value pairs and store them in a map. For example, consider the following command-line arguments:

```
"-a", "ant", "-b", "bee", "-b", "bat", "cat", "-d", "-e", "elk", "-f"
```

In this case, `-a` `-b` `-d` `-e` and `-f` are all flags since they start with a `-` dash followed by at least 1 non-digit character. The values are `ant` `bee` `bat` `cat` and `elk` since they do not start with a `-` and non-digit character. 

Note that `-42` is *not* a flag because the `-` dash is followed by a digit character. Instead it should be interpreted as a value representing a negative number.

Not all flags have values, not all values have associated flags, and values will be overwritten if there are repeated flags. For example, flag `-a` has value `ant`. Flag `-b` has initial value `bee`, but the value get replaced by the second occurrence of the `-b` flag with the value `bat` instead. The value `cat` has no associated flag and is ignored. The flags `-d` and `-f` have no associated value, but are still stored by the argument parser. The resulting map should look similar to:

```
{
  "-a" = "ant",
  "-b" = "bat",
  "-d" = null,
  "-e" = "elk",
  "-f" = null
}
```

Use an appropriate data structure to store the key/value pairs. The key/value pairs does *not* need to be stored in sorted order. 

Avoid looping more often than necessary. For example, `numFlags()` should not require a loop.

## Requirements ##

The official name of this homework is `ArgumentParser`. This should be the name of the associated Eclipse Java project and the name used when running the `homework` test script on the lab computers.

See the [Homework Guides](https://usf-cs212-2020.github.io/guides/homework/) for additional details on homework requirements and submission.

## Hints ##

Below are some hints that may help with this homework assignment:

- Many methods may be implemented with one line of code if you are familiar with the methods in [HashMap](https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/util/HashMap.html) or [TreeMap](https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/util/TreeMap.html).

- The `parse(...)` method is easier if you use a traditional `for` loop instead of an enhanced `for` loop.

These hints are *optional*. There may be multiple approaches to solving this homework.