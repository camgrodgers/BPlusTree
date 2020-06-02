This is an implementation of a B+ Tree.
https://en.wikipedia.org/wiki/B%2B_tree

The B+ Tree is a balanced tree that contains multiple keys per node in internal nodes (in contiguous memory), and stores all values in leaf nodes (again, multiple per node). The B+ Tree and its slightly simpler cousin, the B-Tree tend to have superior performance characteristics to Red-and-Black trees in computing environments where there is a memory hierarchy with certain types of memory and/or storage being faster than others. Modern CPU speeds, slow RAM, and cache techniques mean that these types of multi-key/value-per-node trees even outperform traditional balanced trees for regular use.

This is not a complete, formally verified, thoroughly tested, or generic implementation of the B+ Tree, and was written for learning purposes. I do not recommend that anyone use it.
