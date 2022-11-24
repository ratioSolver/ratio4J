# ratio4J

Java API for the  [oRatio](https://github.com/ratioSolver/oRatio) solver.

## Installation

Recursively clone this repository through `git clone --recurse-submodules https://github.com/ratioSolver/ratio4J`.

Compile and install the native libraries.

```shell
cd src/main/cpp
mkdir build
cd build
cmake ..
sudo make install
```

Go back at ratio4J level and compile the Java code using `mvn install`.
