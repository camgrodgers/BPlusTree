import java.util.ArrayList;
import java.util.List;

public class BPTree {

	// Variables
	private int mways = 5;
	private Node root = new LeafNode();

	// Public Methods
	public void Initialize(int m) {
		root = new LeafNode();
		mways = m < 2 ? 2 : m;
	}

	public void Insert(int key, double value) {
		// Initializing with default or previous mways if not initialized
		if (root == null) { Initialize(mways); }
		root.Insert(key, value);
	}

	public void Delete(int key) {
		if (root == null) { return; }
		root.Delete(key);
	}

	public Double Search(int key){
		if (root == null) { return null; }
		return root.Search(key);
	}

	public List<Double> Search(int key1, int key2) {
		if (root == null) { return null; }

		// Swap keys if sent out of order
		if (key1 > key2) {
			int temp = key1;
			key1 = key2;
			key2 = temp;
		}
		return root.Search(key1, key2);
	}

	// Private methods
	private <T> List<T> copySubList(List<T> orig) {
		// Needed to get sublist values dissociated from original object
		List<T> copy = new ArrayList<T>(orig.size());
		for (int i = 0; i < orig.size(); ++i) {
			copy.add(i, orig.get(i));
		}
		return copy;
	}

	// Node Classes
	private interface Node {
		public double Search(int key);
		public List<Double> Search(int key1, int key2);
		public Node Insert(int key, double value);
		public int getLeftMost();
		public void Delete(int key);
		public Node GetParent();
		public void SetParent(InternalNode p);
	}

	private class InternalNode implements Node {
		// Variables
		List<Integer> keys = new ArrayList<Integer>();
		List<Node> children = new ArrayList<Node>();
		InternalNode parent = null;
		
		// Methods
		InternalNode() { keys = new ArrayList<Integer>(); children = new ArrayList<Node>(); }

		public void Delete(int key) {			
			// Search for child less than to call delete on
			for (int i = 0; i < keys.size(); ++i) {
				if (keys.get(i) > key) {
					children.get(i).Delete(key);
					return;
				}
			}
			// Otherwise, call delete command on last child
			children.get(children.size() - 1).Delete(key);
		}

		public Node GetParent() {
			return parent;
		}

		public void SetParent(InternalNode p) {
			parent = p;
		}

		public Node Insert(int key, double value) {
			int keyIndex = 0;
			while (keyIndex < keys.size()) {
				if (keys.get(keyIndex) > key) {
					break;
				}
				++keyIndex;
			}
			
			// Call insert on child node
			Node newNode = children.get(keyIndex).Insert(key, value);
			// Early return if the child was not split
			if (newNode == null) { return null;	}

			// Child was split, therefore must add a key and new child
			int newKey = newNode.getLeftMost();
			if (keyIndex == keys.size()) {
				keys.add(newKey);
				children.add(newNode);
			} else {
				keys.add(keyIndex, newKey);
				children.add(keyIndex + 1, newNode);
			}

			// Early return if the node does not need to be split up
			if (keys.size() != mways) { return null; }
			
			// Splitting
			List<Integer> rightKeys = copySubList(keys.subList(keys.size() / 2, keys.size() - 1));
			List<Node> rightChildren = copySubList(children.subList(children.size() / 2, children.size() - 1));
			List<Integer> leftKeys = copySubList(keys.subList(0, keys.size() / 2));
			List<Node> leftChildren = copySubList(children.subList(0, children.size() / 2));
			keys = leftKeys;
			children = leftChildren;

			// Decide which side to remove the odd out key from
			if (mways % 2 == 0) {
				keys.remove((int)keys.size() - 1);
			} else {
				rightKeys.remove(0);
			}

			// New node will be returned to parent to add to its child list
			InternalNode newIntern = new InternalNode();
			newIntern.keys = rightKeys;
			newIntern.children = rightChildren;
			for (int i = 0; i < newIntern.children.size(); ++i) {
				newIntern.children.get(i).SetParent(newIntern);
			}
			newIntern.parent = this.parent;
			
			// Handle root case
			if (this == root) {
				InternalNode temp = new InternalNode();
				temp.keys.add(newIntern.getLeftMost());
				temp.children.add(this);
				temp.children.add(newIntern);
				root = temp;
				this.parent = temp;
				newIntern.parent = temp;
				return null;
			}
			
			return newIntern;
		}

		public int getLeftMost() {
			return children.get(0).getLeftMost();
		}

		public double Search(int key) {
			for (int i = 0; i < keys.size(); ++i) {
				if (keys.get(i) > key) {
					return children.get(i).Search(key);
				}
			}
			return children.get(children.size() - 1).Search(key);
		}

		public List<Double> Search(int key1, int key2) {
			for (int i = 0; i < keys.size(); ++i) {
				if (keys.get(i) > key1) {
					return children.get(i).Search(key1, key2);
				}
			}
			return children.get(children.size() - 1).Search(key1, key2);
		}
	}

	private class LeafNode implements Node {
		// Variables
		List<Integer> keys = new ArrayList<Integer>();
		List<Double> values = new ArrayList<Double>();
		LeafNode next = null;
		LeafNode prev = null;
		InternalNode parent = null;

		// Methods
		LeafNode() { keys = new ArrayList<Integer>(); values = new ArrayList<Double>(); }

		public void Delete(int key) {
			// Check if there's a match
			boolean match = false;
			int index = 0;
			for (int i = 0; i < keys.size(); ++i) {
				if (keys.get(i) == key) {
					index = i;
					match = true;
					break;
				}
			}
			if (match == false) { return; }
			
//			if (mways == 2) {
//				if (parent.keys.get(0) > key) {
//					parent.children.add(0, null);
//				} else {
//					parent.children.add(1, null);
//				}
//				return;
//			}
			
			// If it's root, just remove
			if (this == root) {
				keys.remove(index);
				values.remove(index);
				return;
			}

			// First case this has more than 1 pair, just remove and no rotation
			if (keys.size() > 1) {
				keys.remove(index);
				values.remove(index);
				return;
			}

			// Second case, this has 1 pair, and neighbor has more than 1
			LeafNode neighbor = this == parent.children.get(parent.children.size() - 1) ? prev : next;
			if (neighbor.keys.size() > 1) {
				if (neighbor == next) {
					int removedKey = next.keys.remove(0);
					keys.add(0, removedKey);
					values.add(0, next.values.remove(0));
					for (int i = 0; i < parent.children.size(); ++i) {
						if (parent.children.get(i) == next) {
							parent.keys.add(i, next.keys.get(0));

						}
					}
				} else {
					keys.add(0, neighbor.keys.remove(neighbor.keys.size() - 1));
					values.add(0, neighbor.values.remove(neighbor.values.size() - 1));
					parent.keys.add(parent.keys.size() - 1, this.keys.get(0));

				}
				return;
			}

			// Third case: this and neighbor are only 1 pair
			if (neighbor.keys.size() == 1 && parent.keys.size() > 1) {
				for (int i = 0; i < parent.children.size(); ++i) {
					if (this == parent.children.get(i)) {
						parent.children.remove(i);
					}
				}
				for (int i = 0; i < parent.keys.size(); ++i) {
					if (neighbor.keys.get(0) == parent.keys.get(i)) {
						parent.keys.remove(i);
					}
				}
				return;
			}

			// Fourth case: this and neighbor are 1 pair, and parent is 1 key
			if (neighbor.keys.size() == 1 && parent.keys.size() == 1) {
				InternalNode grandparent = parent.parent;
				if (grandparent == null) {
					
				} else {
					for (int i = 0; i < grandparent.keys.size(); ++i) {
						if (grandparent.keys.get(i) == parent.keys.get(0)) {
							grandparent.keys.remove(i);
						}
					}
					for (int i = 0; i < grandparent.children.size(); ++i) {
						if (grandparent.children.get(i) == parent) {
							grandparent.children.remove(i);
						}
					}
				}
				
			}
		}

		public Node GetParent() {
			return parent;
		}

		public void SetParent(InternalNode p) {
			parent = p;
		}

		public int getLeftMost() {
			return keys.get(0);
		}

		public Node Insert(int key, double value) {
			boolean inserted = false;
			for (int i = 0; i < keys.size(); ++i) {
				// No duplicates
				if (keys.get(i) == key) { return null; }

				if (keys.get(i) > key) {
					keys.add(i, key);
					values.add(i, value);
					inserted = true;
					break;
				}
			}
			if (!inserted) {
				keys.add(key);
				values.add(value);
			}

			if (keys.size() == mways) {
				// Splitting
				List<Integer> rightKeys = copySubList(keys.subList(keys.size() / 2, keys.size()));
				List<Double> rightValues = copySubList(values.subList(values.size() / 2, values.size()));
				List<Integer> leftKeys = copySubList(keys.subList(0, keys.size() / 2));
				List<Double> leftValues = copySubList(values.subList(0, values.size() / 2));
				keys = leftKeys;
				values = leftValues;

				// New node can be returned to parent to add to child list
				LeafNode newLeaf = new LeafNode();
				newLeaf.keys = rightKeys;
				newLeaf.values = rightValues;
				newLeaf.next = next;
				newLeaf.prev = this;
				newLeaf.parent = this.parent;
				next = newLeaf;

				// Handle root case
				if (this == root) {
					InternalNode tempRoot = new InternalNode();
					root = tempRoot;
					tempRoot.keys.add(rightKeys.get(0));
					tempRoot.children.add(this);
					this.parent = tempRoot;
					tempRoot.children.add(newLeaf);
					newLeaf.parent = tempRoot;
					return null;
				}

				return newLeaf;
			}

			return null;
		}

		public double Search(int key) {
			int index = keys.indexOf(key);
			if (index == -1) {
				return index;
			}
			return values.get(index);
		}

		public List<Double> Search(int key1, int key2) {
			List<Double> results = new ArrayList<Double>();
			
			// Traverse linked list adding values with key in range
			LeafNode curr = this;
			boolean searching = true;
			while (searching) {
				if (curr == null) {
					return results;
				}

				for (int i = 0; i < curr.keys.size(); ++i) {
					if (key2 < curr.keys.get(i)) {
						return results;
					}
					if (key1 <= curr.keys.get(i)) {
						results.add(curr.values.get(i));
					}
				}
				curr = curr.next;
			}
			return results;
		}
	}
}
