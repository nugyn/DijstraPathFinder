To compile on server:
javac -cp .:jopt-simple-5.0.2.jar PathFinderTester.java map/*.java pathFinder/*.java

To run on server, using example1 and all the optional files, apart from output file, specified:
java -cp .:jopt-simple-5.0.2.jar PathFinderTester -v -t terrain1.para -w waypoints1.para example1.para 

Install this for auto watch:
pip install https://github.com/joh/when-changed/archive/master.zip

1. run following command:
chmod +x recursiveWatch.bash
chmod +x simpleTest.bash

2. Run:
./recursiveWatch.bash

3. DijkstraPathFinder.java should be automatically compiled on save. Open a new terminal, run:
./simpleTest.bash
for the first test using example1.

CONTRIBUTORS

Austin Pham
Duy Linh Nguyen
