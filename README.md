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
You can also modify and run  ./src/test/java to check results for yourself.

Reference:

Ishakian, Vatche, Dóra Erdös, Evimaria Terzi, and Azer
Bestavros. "A Framework for the Evaluation and Management
of Network Centrality." In SDM, pp. 427-438. 2012.

This project is implemented in Java using Eclipse. I also used a free graph library, JGraphT.

How to load the project in Eclipse:

File -> Import -> Existing Projects into Workspace -> Select root directory (loadbalancing) -> Finish.
 
This code is only for study and research use.
If you have any questions, please contact me via fshi @ bu.edu.

Thank you!

May 9, 2014
