# Deadlock and Optimizations

Explain your optimizations if applicable

I have implemented the freeze deadlocks detection because I think this detection can skip some states in test_maps3 and 
this is the easiest for me to understand and implement.
My ideas are from this website: http://sokobano.de/wiki/index.php?title=How_to_detect_deadlocks 

Freeze deadlocks occur when some boxes that are not on the goal squares are immoveable as they are blocked horizontally and vertically.

First, it checks if the just moved box is blocked both horizontally and vertically.
There are 3 scenarios for each axis: blocked by wall, blocked by box, free to move

If it is blocked by box, we have to check if the blocking box is also a frozen box. 
To do it, I use a recursive function to check it in the axis that is opposite to the previous neighbour box's checking axis
One axis is enough to determine if it is a frozen box as it was also blocked by the previous neighbour box in another axis.
(Ideas from the website)

If the just moved box is blocked in both axes and there is at least one frozen box that is not on the goal squares,
the state is a freeze deaadlock.

-------------------------------------------------
Other optimizations:
I made some changes to simple_corner_deadlock and corner_check according to what have discussed in the forum (#1383 and #1379)

-------------------------------------------------
Performance:
              *number of expanded nodes*         *Time(Expanded/sec) on my computer*
                Before          After               Before            After
test_map1 :     13              13(0)               0.091785(141)     0.096355(134)    Very close
test_map2 :     978745          929532(-49213)      10.677987(91660)  9.968696(93245)  Faster
test_map3 :     230041          74350(-155691)      2.299715(100030)  0.846277(87855)  Faster

The solution length of the 3 cases remain(6, 43, 292 respectively) but the path is slightly different for the last 2 cases.

The optimization is effective overall as we can see there was a large reduce in the number of expanded nodes for test_map2 and test_map3. 
Moreover, the computing time after the optimization decreased for the 2 cases.

-------------------------------------------------
Location of the optimization:
line 434, utils.c, freeze_deadlock() with some helper functions as well (including find_moved_box() in line 183 ai.c)
It is called in line 313 ai.c

-------------------------------------------------
Free all memory:
I added 2 functions to free all the memory allocated:
free_node() in line 147 ai.c (called within free_memory())
freeSokoban() in line 355 utils.c (called in line 363 ai.c)
and made changes to emptyPQ() in line 100 priority_queue.c and free_memory() in line 157 ai.c

