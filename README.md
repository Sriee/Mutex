# Mutex
Implementation of Lamport &amp; Ricarta, Agarwala mutual exclusion algorithm


Implement a mutual exclusion service using two different distributed mutual exclusion algorithm:

1. Lamport’s algorithm
2. Ricart and Agrawala’s algorithm.

Your service should provide two function calls to the application: *csEnter()* and *csLeave()*. The first function call *csEnter()* allows an application to request permission to start executing its critical section. The function call is blocking and returns only when the invoking application can execute its critical section. The second function call *csLeave()* allows an application to inform the service that it has finished executing its critical section.

## Implementation Details

Design your program so that each process or node consists of two separate modules. The top module implements the application (requests and executes critical sections). The bottom module implements the mutual exclusion service. The two modules interact using *csEnter()* and *csExit()* functions.

## Application

The application is responsible for generating critical section requests and then executing critical sections on receiving permission from the mutual exclusion service. Model your application using the following two parameters: **inter-request delay** and **cs-execution time**.

The first parameter denotes the time elapsed between when a node’s current request is satisfied and when it generates the next request. The second parameter denotes the time a node spends in its critical section. 

## Format of the configuration file

The configuration file will be a plain-text formatted file no more than *100KB* in size. Only lines which begin with an unsigned integer are considered to be valid. Lines which are not valid should be ignored. The configuration file will contain *n + 1* valid lines. The first valid line of the configuration file contains four tokens. 

The **first token** is the numberof nodes in the system. 
The **second token** is the mean value for inter-request delay (in milliseconds).
The **third token** is the mean value for cs-execution time (in milliseconds). 
The **fourth token** is the number of requests each node should generate.

After the first valid line, the next *n* lines consist of three tokens. The first token is the node ID. The second token is the host-name of the machine on which the node runs. The third token is the port on which the node listens for incoming connections. The parser is robust concerning leading and trailing white space or extra lines at the beginning or end of file, as well as interleaved with valid lines. The *‘#’* character will denote a comment. On any valid line, any characters after a *‘#’* character should be ignored.

## Launching the application

*‘launcher.sh’* will launch multiple instances of the application as nodes in the machine that you have entered in the configuration file. 

Run startup using the following syntax

`sh startup.sh <path/of/configuration/file.txt>`

## Terminating the application

Once the nodes have taken the required number of snapshots as mentioned in the configuration file, each node will start it’s execution. But if the application is stalled due to some failures, it is better to kill the processes. *‘cleanup’*  is the script to use while logs in to appropriate machines to kill the processes launched by the *‘launcher’* script. 

Run cleanup using the following syntax

`sh cleanup.sh <path/of/configuration/file.txt>`

**Note:** Both launcher and cleanup script depend on the correct configuration file to run.

## Supported Environments

The program is built, executed and tested on CentOS and Ubuntu machines. It won’t run for Windows and Mac Machines (though porting them to other operating system is trivial).    

## Testing Correctness:

The implementation and execution of mutual exclusion algorithm are checked using two methods

### Method 1

When a node enters its critical section it write 4 lines to a file. The lines are interleaved with cs-execution time. This is designed in such a way that if there is no mutual exclusion the lines in the resulting file will be interleaved. We can exploit this for writing an automation script to detect failures in mutual exclusion.

Use ‘mutex_checker.py’ script which checks for interleaving of lines by the nodes in the system. Execute the following syntax for running mutex checker  

`python mutex_checker.py <path/to/config/file.txt>`

#### Output


### Method 2	

Use *Fidge/Mattern vector clock* to check the mutual exclusion. All the nodes in the system write to a common file called ‘clock’. For mutual exclusion to work there should be *‘happened-before’* relation between the vector clock values. Presence of **concurrency** confirms the violation of dead lock. 

Use ‘clock_checker.py’ to check for concurrency. Execute the following syntax for running clock checker

`python clock_checker.py <number_of_nodes>`

#### Output

#### Corrupted Output 

## Licence 

This project is licensed under the MIT License - see the [LICENCE](../master/LICENSE) file for details
