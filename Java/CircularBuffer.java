/*
 * Name: Luis Prieb
 * Student ID: 2016843211
 */

public class CircularBuffer
{
    /*
     * Our instance variables
     * size will hold the number of elements in the buffer
     * capacity will hold the max number of elements the buffer can have
     * head references the head node in the buffer
     */

    private int size;
    private int capacity;
    private CircularBufferNode head;
    private CircularBufferNode tail;
    private boolean overflow_flag;

    public CircularBuffer(int capacity)
    {
        /*
         * Our constructor
         * Should initalize the instance variables to their default value
         * Since it is empty at the start, head should be null, and overflow_flag should be false
         */
        this.size = 0 ;
        this.capacity = capacity;
        this.head = null;
        this.tail = null;
        this.overflow_flag = false;
    }

    public CircularBufferNode add(char data)
    {
        /*
         * Adds and returns a new node with the provided data to the end of the buffer
         */

        CircularBufferNode new_node;

        new_node = new CircularBufferNode(data);

        if (this.size < capacity)
        {
        	this.size += 1;
        	// Find tail
	       

	        if (this.size == 1)
	        {
	        	this.head = new_node;
	        	this.tail = new_node;

	        }
	        else
	        {
	        	this.tail.setNext(new_node);
	        	this.tail = new_node;
	        }

	        // Make circular again
	        new_node.setNext(this.head);

	    }
	    else
	    {
	    	// Remove current head and add new node to tail
	    	this.tail.setNext(new_node);
	    	this.head = this.head.getNext();
	    	new_node.setNext(this.head);
	    	this.tail = new_node;
	    }


        return new_node;
    }

    public CircularBufferNode delete()
    {
        /*
         * Deletes and returns the node at the front of the buffer
         */
        CircularBufferNode temp;

        if(this.size != 0)
        {
        	if(size == 1)
        	{
        		temp = this.head;
        		this.head = null;
        		this.tail = null;
        	}
        	else
        	{
        		temp = this.head;
        		this.tail.setNext(this.head.getNext());
        		this.head = this.head.getNext();
        	}
        	size -= 1;
        }
        else
        {
        	temp = null;
        }

        this.overflow_flag = false;
        return temp;

    }

    public boolean contains(char data)
    {
        /*
         * Checks if the buffer contains a node with the specified data
         */
        boolean found = false;
        int index = 0;
        CircularBufferNode current;

        current = this.head;

  		while (index != this.size && found == false)
  		{
  			if(current.getData() == data)
  			{
  				found = true;
  			}
  			index += 1;
  			current = current.getNext();
  		}

        this.overflow_flag = false;
        return found;
  		
    }

    public int getSize()
    {
        /*
         * Returns the number of elements in the buffer
         */
    
    	this.overflow_flag = false;
        return this.size;
    }

    public int getCapacity()
    {
        /*
         * Returns the capacity of the buffer
         */
        this.overflow_flag = false;
        return this.capacity;
    }

    public CircularBufferNode getHead()
    {
        /*
         * Returns the head of the buffer
         */
        this.overflow_flag = false;
        return this.head;
    }

    public CircularBufferNode getTail()
    {
        /*
         * Returns the tail of the buffer
         */
        this.overflow_flag = false;
        return this.tail;
    }

    public int getIndex(char data)
    {
        /*
         * Returns the index of the first node with the specified data
         * Returns -1 if the index does not exist
         */

        CircularBufferNode current;
        int current_index = 0;
        this.overflow_flag = false;
        current = this.head;

        while ((current_index != this.size) && (current.getData() != data))
        {
            current = current.getNext();
            current_index += 1;
        }

        if (current_index != this.size)
            return current_index;
        else
            return -1;  
    }

    public CircularBufferNode getNode(int index)
    {
        /*
         * Returns the node at the specified index
         * Returns null if the index does not exist
         */

        // Check if index 
        if (index >= this.size || index < 0)
        	return null;

        int index_counter;
        CircularBufferNode current; 

        index_counter = 0;
        current = this.head;

    	while ((index_counter < index))
    	{
    		current = current.getNext();
    		index_counter++;
    	}

    	this.overflow_flag = false;
        return current;
    }

    public boolean isEmpty()
    {
        /*
         * Returns whether or not the buffer is empty
         */
        this.overflow_flag = false;
        return this.head == null;
    }

    public boolean overflow()
    {
        /*
         * Returns whether or not previous operation caused an overflow
         */
        this.overflow_flag = false;
        return this.overflow_flag;
    }

    public void clear()
    {
        /*
         * Clears the buffer
         */
        this.overflow_flag = false;
        this.head = null;
        this.tail = null;
    }

    public String toString()
    {
        /*
         * Returns the buffer in string form
         * The format is just the data from each node concatenated together
         * See the tests for an example
         * There should be no trailing whitespace
         */
        this.overflow_flag = false;

        CircularBufferNode current;
        StringBuilder sb = new StringBuilder();
 
        current = this.head;

    	do
    	{
    		sb.append(current.getData());
    		current = current.getNext();
    	}while(current != this.head);

    	return sb.toString();
    }
}

class CircularBufferNode
{
    /*
     * Our instance variables
     * data will hold a char
     * next is the reference to the next element after this node (null if there is none)
     */

    private char data;
    private CircularBufferNode next;

    public CircularBufferNode(char data)
    {
        /*
         * The constructor
         * Should initalize the instance variables to their default value
         */
        this.data = data;
    }

    public char getData()
    {
        /*
         * Returns our data
         */
        return this.data;
    }

    public CircularBufferNode getNext()
    {
        /*
         * Returns the CircularBufferNode referenced by next
         */
        return this.next;
    }

    public void setData(char data)
    {
        /*
         * Allows us to change the data stored in our CircularBufferNode
         */
        this.data = data;
    }

    public void setNext(CircularBufferNode node)
    {
        /*
         * Allows us to change the next CircularBufferNode
         */
        this.next = node;
    }

    public void clearNext()
    {
        /*
         * Removes the reference to the next CircularBufferNode, replacing it with null
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
