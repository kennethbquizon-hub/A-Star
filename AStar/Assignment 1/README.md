To compile from source run: `javac assignment1/*.java`

Then, to run the program do: `java assignment1.Main [grid file path] [flags]`

A random 100x50 grid is generated if no grid file is provided. The number of rows and columns can be set using the flags described below.

Valid flags include:
- `-c <numCols>` to indicate the number of columns when no grid file is provided (default 100)
- `-r <numRows>` to indicate the number of rows when no grid file is provided (default 50)
- `-d <distance>` to set the approximate distance between the start and goal vertex in terms of the shorter side of the grid (when no grid file is provided). The number provided should be in the range [0, 0.5] and the actual distance will be `d * the shorter side of the grid`. The start and goal vertex are placed randomly anywhere on the grid if this is not provided (and no grid file is provided as well).
- `-w <seconds>` to set how long the java process will wait, in integer seconds, before exiting. Used to get the final memory usage of the java process. This is only applicable if the program is in non-gui mode (see below).
- `-a` to run A*
- `-t` ro run Theta*

Providing either `-a` or `-t` runs the program without a GUI. Instead, diagnostics and information are printed to the terminal.

Note: click the repaint button to fix graphical artifacts (e.g. tearing) if necessary.

Just in case, nushell scripts used to test the running time and memory usage are included under `Scripts`.
Note that these cannot be run on ilab since it is necessary to have [nushell](https://www.nushell.sh/) installed.
