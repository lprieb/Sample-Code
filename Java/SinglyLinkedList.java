/*
 * Name: Luis Prieb
 * Student ID: 2016843211
 */

public class SinglyLinkedList
{
    /*
     * Our instance variables
     * size will hold the number of elements in the linked list
     * head references the head node in the linked list
     */

    private int size;
    private SinglyLinkedListNode head;

    public SinglyLinkedList()
    {
        /*
         * Our constructor
         * Should initalize the instance variables to their default value
         * Since it is empty at the start, head should be null
         */
        this.head = null;
    }

    public SinglyLinkedListNode add(char data, int index)
    {
        /*
         * Adds and returns a new node with the provided data at the specified index
         * Returns null if the index is not possible
         */
        int index_counter;
        SinglyLinkedListNode current;
        SinglyLinkedListNode previous;
        SinglyLinkedListNode new_node;

        index_counter = 0;
        current = this.head;
        previous = null;
        new_node = new SinglyLinkedListNode(data);

    	while ((current != null) && (index_counter < index))
    	{
    		previous = current;
    		current = current.getNext();
    		index_counter++;
    	}

    	if (index_counter == index)
    	{
    		if(current == this.head)
    		{
    			// Inserting at head
    			new_node.setNext(this.head);
    			this.head = new_node;
    		}
    		else 
    		{
    			// Inserting non-head Node
    			previous.setNext(new_node);
    			new_node.setNext(current);
    		}

    	}
    	else
    	{
    		new_node = null;
    	}


        return new_node;

    }

    public SinglyLinkedListNode delete(int index)
    {
        /*
         * Deletes and returns the node at the index specified
         * Returns null if the index does not exist
         */
        int index_counter;
        SinglyLinkedListNode current;
        SinglyLinkedListNode previous;
 

        index_counter = 0;
        current = this.head;
        previous = null;

    	while ((current != null) && (index_counter < index))
    	{
    		previous = current;
    		current = current.getNext();
    		index_counter++;
    	}

        if (current != null)
        {
        	if (current == this.head)
	    	{
	    		// Deleting head node
		    	this.head = this.head.getNext();
	    	}
	    	else if(current.getNext() == null)
	    	{
	    		// Deleting tail node
        		previous.clearNext();
        	}
        	else
        	{
        		// Deleting middle value
        		previous.setNext(current.getNext());
        	}
        }

        return current;

    }

    public SinglyLinkedListNode deleteItem(char data)
    {
        /*
         * Deletes and returns the first node with the specified data in the linked list
         * Returns null if the item doesn't exist
         */
        SinglyLinkedListNode current;
        SinglyLinkedListNode previous;
 
        current = this.head;
        previous = null;

    	while ((current != null) && (current.getData() != data))
    	{
    		previous = current;
    		current = current.getNext();
    	}

        if (current != null)
        {
        	if (current == this.head)
	    	{
	    		// Deleting head node
		    	this.head = this.head.getNext();
	    	}
	    	else if(current.getNext() == null)
	    	{
	    		// Deleting tail node
        		previous.clearNext();
        	}
        	else
        	{
        		// Deleting middle value
        		previous.setNext(current.getNext());
        	}
        }

        return current;
    }

    public boolean contains(char data)
    {
        /*
         * Checks if the linked list contains a node with the specified data
         */
        SinglyLinkedListNode current;
 
        current = this.head;

    	while ((current != null) && (current.getData() != data))
    	{
    		current = current.getNext();
    	}

        if (current == null)
        	return false;
        else
        	return true;
    }

    public int getSize()
    {
        /*
         * Returns the number of elements in the linked list
         */
        SinglyLinkedListNode current;
        int counter;
 
        current = this.head;
        counter = 0;

    	while (current != null)
    	{
    		current = current.getNext();
    		counter += 1;
    	}

    	return counter;
    }

    public SinglyLinkedListNode getHead()
    {
        /*
         * Returns the head of the linked list
         */
        return this.head;
    }

    public SinglyLinkedListNode getTail()
    {
    	SinglyLinkedListNode tail = this.head;

    	if (!this.isEmpty())
    	{
			while(tail.getNext() != null)
			{
				tail = tail.getNext();
			}
		}

		return tail;

    }

    public int getIndex(char data)
    {
        /*
         * Returns the index of the first node with the specified data
         * Returns -1 if the index does not exist
         */
        SinglyLinkedListNode current;
        int current_index;
 
        current = this.head;
        current_index = 0;

    	while ((current != null) && (current.getData() != data))
    	{
    		current = current.getNext();
    		current_index += 1;
    	}

        if (current != null)
        	return current_index;
        else
        	return -1;
    }

    public SinglyLinkedListNode getNode(int index)
    {
        /*
         * Returns the node at the specified index
         * Returns null if the index does not exist
         */

        // Check that value is valid
        if(index < 0)
        	return null;
        
        int index_counter;
        SinglyLinkedListNode current; 

        index_counter = 0;
        current = this.head;

        if (!(this.isEmpty()))
        {
        	while ((current != null) && (index_counter < index))
        	// While will return when it runs out of nodes, or when the index is reached
        	{
        		current = current.getNext();
        		index_counter++;
        	}
        }

        return current;
    }

    public boolean isEmpty()
    {
        /*
         * Returns whether or not the linked list is empty
         */
        return (this.head == null);
    }

    public void clear()
    {
        /*
         * Clears the linked list
         */
        this.head = null;
    }

    public String toString()
    {
        /*
         * Returns the linked list in string form
         * The format is just the data from each node concatenated together
         * See the tests for an example
         * There should be no trailing whitespace
         */
        SinglyLinkedListNode current;
        StringBuilder sb = new StringBuilder();
 
        current = this.head;

    	while (current != null)
    	{
    		sb.append(current.getData());
    		current = current.getNext();
    	}

    	return sb.toString();
    }
}

class SinglyLinkedListNode
{
    /*
     * Our instance variables
     * data will hold a char
     * next is the reference to the next element after this node (null if there is none)
     */

    private char data;
    private SinglyLinkedListNode next;

    public SinglyLinkedListNode(char data)
    {	
        /*
         * The constructor
         * Should initalize the instance variables to their default value
         */
        this.data = data;
    	this.next = null;
    }

    public char getData()
    {
        /*
         * Returns our data
         */
        return this.data;
    }

    public SinglyLinkedListNode getNext()
    {
        /*
         * Returns the SinglyLinkedListNode referenced by next
         */
        return this.next;
    }

    public void setData(char data)
    {
        /*
         * Allows us to change the data stored in our SinglyLinkedListNode
         */
        this.data = data;
    }

    public void setNext(SinglyLinkedListNode node)
    {

        /*
         * Allows us to change the next SinglyLinkedListNode
         */
        this.next = node;
    }

    public void clearNext()
    {
        /*
         * Removes the reference to the next SinglyLinkedListNode, replacing it with null
         */
        this.next = null;
    }

    public String toString()
    {
        /*
         * Returns our data in string form
         */
        return String.valueOf(this.data);
    }
}
