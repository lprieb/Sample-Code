/* 
 * Name: Luis Prieb
 * Student ID: 2016843211
 * Don't forget to remove the package line.
 */

public class AVLTree
{
    private AVLTreeNode root;
    private int size;
    private int index; // Variable used for indexing in internal functions

    /* 
     * Our instance variables.
     *
     * root - AVLTreeNode, root of our AVLTree
     * size - int, the number of elements in our AVLTree
     */

    public AVLTree()
    {
        /*
         * Our constructor. 
         * Initialize the instance variables to their default values
         */
        this.root = null;
        this.size = 0;
    }

    public AVLTreeNode insert(int data)
    {
        /*
         * Constructs a new AVLTreeNode and inserts it into our AVLTree
         *
         * return the inserted AVLTreeNode or null if a node with the same data
         * already exists
         */
        AVLTreeNode currentNode = root;
        AVLTreeNode insertedNode = null;
        boolean inserted = false;

        if(root == null)
        {
            insertedNode = new AVLTreeNode(data);
            root = insertedNode;
        }
        else
        {
            while(!inserted)
            {
                if(data < currentNode.getData())
                {
                    if(currentNode.getLeft() == null)
                    {
                        insertedNode = new AVLTreeNode(data);
                        currentNode.setLeft(insertedNode);
                        insertedNode.setParent(currentNode);
                        inserted = true;
                    }
                    else
                        currentNode = currentNode.getLeft();
                }
                else if(data > currentNode.getData())
                {
                    if(currentNode.getRight() == null)
                    {
                        insertedNode = new AVLTreeNode(data);
                        currentNode.setRight(insertedNode);
                        insertedNode.setParent(currentNode);
                        inserted = true;
                    }
                    else
                        currentNode = currentNode.getRight();
                }
                else
                    return currentNode; // Data in tree. Implicitly breaks loop

            }
        }
        size++;
        // Recalculate Height
        upHeightRecalculate(insertedNode);

        // Rebalance
        rebalance(currentNode);

        return currentNode;
    }
    private void upHeightRecalculate(AVLTreeNode pivot)
    {

        AVLTreeNode currentNode;
        currentNode = pivot;


        while(currentNode != root)
        {
            currentNode.setHeight(maxChildHeight(currentNode) + 1);
            currentNode = currentNode.getParent();
        }

        root.setHeight(maxChildHeight(currentNode) + 1);

    }

    private int downHeightRecalculate(AVLTreeNode node)
    {
        if( node == null)
            return -1;

        int newHeight = intMax( downHeightRecalculate(node.getLeft()), downHeightRecalculate(node.getRight())) + 1;
        node.setHeight(newHeight);
        return newHeight;
    }

    private int intMax(int a, int b)
    {
        if(a > b)
            return a;
        else
            return b;
    }

    private int maxChildHeight(AVLTreeNode node)
    {
        return (heightNode(node.getLeft()) > heightNode(node.getRight())) ? heightNode(node.getLeft()) : heightNode(node.getRight());
    }
    private int heightNode(AVLTreeNode node)
    {
        if(node == null)
            return -1;
        else
            return node.getHeight();
    }
    


    private void rebalance(AVLTreeNode pivot)
    {
        AVLTreeNode currentNode;
        boolean balanceBroken = false;
        currentNode = pivot;
        if(pivot == null)
            return;

        while(currentNode != root)
        {
            if(abs(heightNode(currentNode.getLeft()) - heightNode(currentNode.getRight())) >= 2)
            {
                restructure(currentNode);
                            }
            currentNode = currentNode.getParent();
        }

        if(abs(heightNode(currentNode.getLeft()) - heightNode(currentNode.getRight())) >= 2)
        {
            restructure(currentNode);
        }


    }

    private void restructure(AVLTreeNode imbalanceNode)
    {
        AVLTreeNode x,y,z;

        z = imbalanceNode;
        if (z == null)
            return;
        y = (heightNode(z.getLeft()) > heightNode(z.getRight())) ? z.getLeft() : z.getRight();

        if (y == null)
            return;

        x = (heightNode(y.getLeft()) > heightNode(y.getRight())) ? y.getLeft() : y.getRight();

        if(x == null)
            return;

        if(sameSide(z,y,x))
        {
            if(z.getRight() == y)
                leftRotate(z);
            else
                rightRotate(z);
        }
        else
        {
            if(z.getRight() == y)
            {
                rightRotate(y);
                leftRotate(z);
            }
            else
            {
                leftRotate(y);
                rightRotate(z);
            }
        }

    }

    private boolean sameSide(AVLTreeNode z, AVLTreeNode y, AVLTreeNode x)
    {
        boolean sameSide = false;
        
        if(z.getLeft() == y)
        {
            if(y.getLeft() == x)
                sameSide = true;
        }
        else if(z.getRight() == y)
        {
            if(y.getRight() == x)
                sameSide = true;
        }
        
        return sameSide;
    }
    private int abs(int a)
    {
        if(a < 0)
            return a*-1;
        else
            return a;
    }

    public AVLTreeNode retrieve(int data)
    {
        /*
         * returns the node in the tree containing the desired data
         *
         * return null if it is not in the tree
         */
        boolean found = false;
        boolean ranOut = false;
        AVLTreeNode currentNode = root;
        if(size == 0)
            return null;

        while(!found & !ranOut)
        {
             if(data < currentNode.getData())
                if(currentNode.getLeft() == null)
                {
                    ranOut = true;
                }
                else
                    currentNode = currentNode.getLeft();
            else if(data > currentNode.getData())
                if(currentNode.getRight() == null)
                {
                    ranOut = true;
                }
                else
                    currentNode = currentNode.getRight();
            else
                found = true; // Implicitly breaks loop
        }

        if(found)
            return currentNode;
        else
            return null;

    }

    public boolean contains(int data)
    {
        /*
         * return whether or not the tree contains a node with the desired data
         */
        boolean found = false;
        boolean ranOut = false;
        AVLTreeNode currentNode = root;

        while(!found & !ranOut)
        {
             if(data < currentNode.getData())
                if(currentNode.getLeft() == null)
                {
                    ranOut = true;
                }
                else
                    currentNode = currentNode.getLeft();
            else if(data > currentNode.getData())
                if(currentNode.getRight() == null)
                {
                    ranOut = true;
                }
                else
                    currentNode = currentNode.getRight();
            else
                found = true; // Implicitly breaks loop
        }

        return found;
    }

    public AVLTreeNode delete(int data)
    {
        /*
         * remove and return the AVLTreeNode with the desired data
         *
         * return null if the data is not in the AVLTree
         */
        AVLTreeNode toDelete, toReplace, toReplaceParent, toDeleteParent, pivot;
        boolean downRecalculate = false; // Boolean to specify which way to recalculate height
        toReplaceParent = toReplace = null;
        
        toDelete = retrieve(data);



        if(toDelete == null)
        {
            return toDelete;
        }

        size--;
        toDeleteParent = toDelete.getParent();

        if(toDelete.getLeft() == null && toDelete.getRight() == null) // End leaf
        {
            if(toDeleteParent == null) // its the root
            {
                toDelete = null;
                pivot = null;
            }
            else
            {
                clearChild(toDeleteParent,toDelete);
                pivot = toDeleteParent;
            }
        }
        else if(toDelete.getLeft() == null)
        {
            toReplace = toDelete.getRight();
            replaceChildWithMax(toDeleteParent, toDelete, toReplace);
            pivot = toReplace;
            downRecalculate = true;
        }
        else if(toDelete.getRight() == null)
        {
            toReplace = toDelete.getLeft();
            replaceChildWithMax(toDeleteParent, toDelete, toReplace);
            pivot = toReplace;
            downRecalculate = true;
        }
        else
        {
            
            toReplace = findMax(toDelete.getLeft());
            toReplaceParent = toReplace.getParent();
            replaceChildWithMax(toDeleteParent, toDelete, toReplace);
            if(toReplaceParent == toDelete) // Case in which the maximum of the left subtree is a child of the node to be deleted
                pivot = toReplace; // external
            else
                pivot = toReplaceParent; // external

        }

        if(root == toDelete)
        {
            root = toReplace;
        }
        if(size == 0)
        {
            root = null;
        }

        if(pivot != null)
        {
            AVLTreeNode pivotParent = pivot.getParent();
            upHeightRecalculate(pivot);
            rebalance(pivot);
        }

        return toDelete;

    }

    private AVLTreeNode findMax(AVLTreeNode node)
    {
        // This function finds the maximum value in a subtree
        //This will be the rightmost node in the tree
        while(node.getRight() != null)
            node = node.getRight();

        return node;

    }
    
    private void clearChild(AVLTreeNode parent, AVLTreeNode child)
    {
        if(parent.getLeft() == child)
            parent.clearLeft();
        else if(parent.getRight() == child)
            parent.clearRight();
    }

    private void replaceChildWithMax(AVLTreeNode parent, AVLTreeNode child, AVLTreeNode replacement)
    {
        // Replaces the child of a parent with the replacement node. If the replacement is null, 
        // all of child's own children, if any, are lost.

        if(parent !=  null)
        {
            if(parent.getLeft() == child)
            {
                parent.setLeft(replacement);
            }
            else if(parent.getRight() == child)
            {
                parent.setRight(replacement);
            }
        }
        if(replacement != null)
        {
            AVLTreeNode childLeftChild, childRightChild;
            AVLTreeNode replacementLeftChild, replacementParent;
            replacementParent = replacement.getParent();
            replacementLeftChild = replacement.getLeft();

            replacement.setParent(parent);
            childLeftChild = child.getLeft();
            childRightChild = child.getRight();


            if(childLeftChild != replacement) // Prevent self linking
            {
                replacement.setLeft(childLeftChild);
                if(childLeftChild != null)
                    childLeftChild.setParent(replacement);
            }
            if(childRightChild != replacement)
            {
                replacement.setRight(childRightChild);
                if(childRightChild != null)
                    childRightChild.setParent(replacement);
            }
            if(replacementLeftChild != null) 
            {
                if(replacementParent != child)
                {
                    replacementParent.setLeft(replacementLeftChild);
                    replacementLeftChild.setParent(replacementParent);
                }
                else // replacement is a child of child and is just moving one up
                {
                    replacement.setLeft(replacementLeftChild);
                    replacementLeftChild.setParent(replacement);
                }
            }
        }
    }

    public AVLTreeNode leftRotate(AVLTreeNode node)
    {
        /*
         * Perform a left rotate on the subtree rooted at node
         *
         * return the new root of the subtree
         */
        AVLTreeNode right = node.getRight();
        node.setRight(right.getLeft());
        right.setLeft(node);
      

        if(root == node)
        {
            root = right;
        }
        else
        {
            AVLTreeNode nodeParent = node.getParent();
            if(nodeParent.getLeft() == node)
            {
                nodeParent.setLeft(right);
                right.setParent(nodeParent);
            }
            else if(nodeParent.getRight() == node)
            {
                nodeParent.setRight(right);
                right.setParent(nodeParent);
            }
        }

        node.setParent(right);

        downHeightRecalculate(right); // O(n)
        upHeightRecalculate(right);  // O(log(n))

        return right;
    }

    public AVLTreeNode rightRotate(AVLTreeNode node)
    {
        /*
         * Perform a right rotate on the subtree rooted at node
         *
         * return the new root of the subtree
         */
        AVLTreeNode left = node.getLeft();
        node.setLeft(left.getRight());
        left.setRight(node);

        if(root == node)
        {
            root = left;
            left.setParent(null);
        }
        else
        {
            AVLTreeNode nodeParent = node.getParent();
            if(nodeParent.getLeft() == node)
            {
                nodeParent.setLeft(left);
                left.setParent(nodeParent);
            }
            else if(nodeParent.getRight() == node)
            {
                nodeParent.setRight(left);
                left.setParent(nodeParent);
            }
        }

        node.setParent(left);

        downHeightRecalculate(left); // O(n)
        upHeightRecalculate(left);  // O(log(n))

        return left;
    }

    public AVLTreeNode[] preorder()
    {
        /*
         * return an array of AVLTreeNodes in preorder
         */
        AVLTreeNode[] cArray = new AVLTreeNode[size];
        index = 0;

        preorder(cArray, root);

        return cArray;

    }
    private void preorder(AVLTreeNode[] container, AVLTreeNode currentNode)
    {
        if(currentNode == null)
            return;

        container[index] = currentNode;
        index++;

        preorder(container, currentNode.getLeft());
        preorder(container, currentNode.getRight());


    }


    public AVLTreeNode[] postorder()
    {
        /*
         * return an array of AVLTreeNodes in postorder
         */
        AVLTreeNode[] cArray = new AVLTreeNode[size];
        index = 0;

        postorder(cArray, root);

        return cArray;
    }

    private void postorder(AVLTreeNode[] container, AVLTreeNode currentNode)
    {
        if(currentNode == null)
            return;

        postorder(container, currentNode.getLeft());
        postorder(container, currentNode.getRight());

        container[index] = currentNode;
        index++;
    }

    public AVLTreeNode[] inorder()
    {
        /*
         * return an array of AVLTreeNodes in inorder
         */
        AVLTreeNode[] cArray = new AVLTreeNode[size];
        index = 0;

        inorder(cArray, root);

        return cArray;
    }

    private void inorder(AVLTreeNode[] container, AVLTreeNode currentNode)
    {
        if(currentNode == null)
            return;

        inorder(container, currentNode.getLeft());

        container[index] = currentNode;
        index++;

        inorder(container, currentNode.getRight());


    }

    public void clear()
    {
        /*
         * clear the AVLTree
         */
        this.root = null; // Let Javas garbage collector do the rest
        this.size = 0;
    }

    public boolean isEmpty()
    {
        /*
         * return whether or not the AVLTree is empty
         */
        return(size == 0);
    }

    public AVLTreeNode getRoot()
    {
        /*
         * return the root of the AVLTree
         */
        return root;
    }

    public int getHeight()
    {
        /*
         * return the height of the AVLTree
         */
        return root.getHeight();
    }

    public String toString()
    {
        /*
         * return a string representation of the AVLTree.
         *
         * The format is the elements of the tree in preorder, each separated
         * by a comma. There should be no spaces and no trailing commas.
         */
        AVLTreeNode [] container = preorder();
        StringBuilder s = new StringBuilder();
        String elem;
        AVLTreeNode test;
        if(container == null)
        {
            return "";
        }

        for(int i = 0 ; i < size ; i++)
        {
            test = container[i];
            if(test == null)
            {
                elem = "-1";
            }
            else
            {
                elem = Integer.toString(test.getData());
            }

            s.append(elem);
            if(i < (size-1))
                s.append(',');
        }

        return s.toString();
    }
}

class AVLTreeNode
{
    private int data, height, balanceFactor;
    private AVLTreeNode left, right, parent;

    /* 
     * Our instance variables.
     *
     * data - int, the data the AVLTreeNode will hold
     * height - int, the height of the AVLTreeNode
     * balanceFactor - int, the balance factor of the node
     * left - AVLTreeNode, the left child of the node
     * right - AVLTreeNode, the right child of the node
     */

    public AVLTreeNode(int data)
    {
        /*
         * Our constructor. 
         * Initialize the instance variables to their default values or the
         * values passed as paramters
         */
        this.data = data;
        height = -1;
        balanceFactor = 0;
        left = null;
        right = null;
    }
    public void setParent(AVLTreeNode parent)
    {
        this.parent = parent;
    }
    public AVLTreeNode getParent()
    {
        return parent;
    }
    public void setData(int data)
    {
        /*
         * Set the value stored in data
         */
        this.data = data;
    }

    public void setHeight(int height)
    {
        /*
         * Set the value stored in height
         */
        this.height = height;
    }

    public void setBalanceFactor(int balanceFactor)
    {
        /*
         * Set the value stored in balanceFactor
         */
        this.balanceFactor = balanceFactor;
    }

    public void setLeft(AVLTreeNode left)
    {
        /*
         * Set the left child
         */
        this.left = left;
    }

    public void setRight(AVLTreeNode right)
    {
        /*
         * Set the right child
         */
        this.right = right;
    }

    public void clearLeft()
    {
        /*
         * clear the left child
         */
        this.left = null;
    }

    public void clearRight()
    {
        /*
         * clear the right child
         */
        this.right = null;
    }

    public int getData()
    {
        /*
         * get the data stored in the AVLTreeNode
         */
        return this.data;
    }

    public int getHeight()
    {
        /*
         * get the height of the AVLTreeNode
         */
        return this.height;
    }

    public int getBalanceFactor()
    {
        /*
         * get the balanceFactor of the AVLTreeNode
         */
        return this.balanceFactor;
    }

    public AVLTreeNode getLeft()
    {
        /*
         * get the left child
         */
        return left;
    }

    public AVLTreeNode getRight()
    {
        /*
         * get the right child
         */
        return right;
    }

    public String toString()
    {
        /*
         * return the string value of the data stored in the node
         */
        return Integer.toString(data);
    }
}