Load Balancing for Traffic in Networks
=============================

CAS CS591 Advanced Topics in Data Mining, Spring 2014 @ Boston University
----------------------------------------------------------------------------------------------------------

Author: Feiyu Shi

Supervised by: Dóra Erdös, Dr. Evimaria Terzi

Course website: [link](http://www.cs.bu.edu/~evimaria/cs591-14.html).

This project solves the following problem in Directed Acyclic Graph (DAG): given a set of k nodes, 
partition it into two groups such that the difference of total number of shortest paths covered by each group 
is minimal.

I designed two greedy algorithms:

* Greedy 
* Greedy Search

and compared them to two baseline methods:

* Brute-force
* Full Search

Algorithms and results are showed in ./doc/poster.pdf or ./doc/slides.pdf. 
You can also modify or/and run  ./src/test/java to check results for yourself.

Reference:

Ishakian, Vatche, Dóra Erdös, Evimaria Terzi, and AzerBestavros. ?A Framework for the Evaluation and Managementof Network Centrality.? In SDM, pp. 427-438. 2012.

This project is implemented in Java using Eclipse. I also used a free graph library, JGraphT.

How to use the code:

In Eclipse:

File -> New -> Java Project. Then change the project location to where this code is downloaded.
The library dependencies will be set up automatically. If not, right click the project name and select
Build Path -> Configure Build Path. Then add all .jar files in ./lib to the library.

This code is only for study and research use.
If you have any questions, please contact me via fshi @ bu.edu.

Thank you!

May 9, 2014