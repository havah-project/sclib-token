# A Java Smart Contract Library for HAVAH Standard Tokens

This repository contains a Java Smart Contract library for HAVAH standard tokens like
HSP20,
HSP721, and
HSP1155.
Smart Contract developers are no longer required to write the whole things from scratch.
This project provides reusable Java classes to build custom user contracts conveniently.

## Usage

Soon you will be able to include this package from Maven Central 
by adding the following dependency in your `build.gradle`.

```groovy
implementation 'com.github.havah:token:0.0.1'
```

You need to create a entry Java class to inherit the attributes and methods from the basic token classes.
The example below would be the simplest HSP20 token Smart Contract with a fixed supply.

```java
public class HSP20FixedSupply extends HSP20Basic {
    public HSP20FixedSupply(String _name, String _symbol) {
        super(_name, _symbol, 3);
        _mint(Context.getCaller(), BigInteger.valueOf(1000000));
    HSP20
}
```

## License

This project is available under the [Apache License, Version 2.0](LICENSE).
