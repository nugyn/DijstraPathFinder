echo "Watching DijkstraPathFinder.java";
echo "--------------------------------";
when-changed -r ./pathFinder/DijkstraPathFinder.java -c javac -cp .:jopt-simple-5.0.2.jar PathFinderTester.java map/*.java pathFinder/*.java && echo "--------------------------------";
when-changed -r ./pathFinder/Node.java -c javac -cp .:jopt-simple-5.0.2.jar PathFinderTester.java map/*.java pathFinder/*.java && echo "--------------------------------";