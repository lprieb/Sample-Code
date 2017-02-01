/*
 * Name: Luis Prieb
 * Student ID: 2016843211
 */

public class DoublyLinkedList
{
    /*
     * Our instance variables
     * size will hold the number of elements in the linked list
     * head references the head node in the linked list
     * tail references the tail node in the linked list
     */

    private int size;
    private DoublyLinkedListNode head;
    private DoublyLinkedListNode tail;

    public DoublyLinkedList()
    {
        /*
         * Our constructor
         * Should initalize the instance variables to their default value
         * Since it is empty at the start, head and tail should be null
         */
        this.head = null;
        this.tail = null;
    }

    public DoublyLinkedListNode add(char data, int index)
    {
        /*
         * Adds and returns a new node with the provided data at the specified index
         * Returns null if the index is not possible
         */
        int index_counter;
        DoublyLinkedListNode current;
        DoublyLinkedListNode previous;
        DoublyLinkedListNode new_node;

        index_counter = 0;
        current = this.head;
        previous = null;
        new_node = new DoublyLinkedListNode(data);

        while ((current != null) && (index_counter < index))
        {
            previous = current;
            current = current.getNext();
            index_counter++;
        }

        if (index_counter == index)
        {
            size += 1;
            if((current == this.head) || (previous == this.tail))
            {
                if(current == this.head)
                {
                    // Inserting new head
                    if (this.head != null)
                        this.head.setPrev(new_node);
                    new_node.setNext(this.head);
                    this.head = new_node;
                }
                if(previous == this.tail)
                {
                    // Inserting new tail
                    if (this.tail != null)
                        this.tail.setNext(new_node);
                    new_node.setPrev(this.tail);
                    this.tail = new_node;
                }
            }
            else
            {
                // Inserting non-head and non-tail Node
                previous.setNext(new_node);
                new_node.setPrev(previous);
                new_node.setNext(current);
                current.setPrev(new_node);
            }

        }
        else
        {
            new_node = null;
        }


        return new_node;
    }

    public DoublyLinkedListNode delete(int index)
    {
        /*
         * Deletes and returns the node at the index specified
         * Returns null if the index does not exist
         */
        int index_counter;
        DoublyLinkedListNode current;
        DoublyLinkedListNode previous;
 

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
            this.size -= 1;
            if((current == this.head) || (current == this.tail))
            {
                if (current == this.head)
                {
                    // Deleting head node
                    this.head = this.head.getNext();
                    if (this.head != null)
                        this.head.clearPrev();
                }
                if(current == this.tail)
                {
                    // Deleting tail node
                    this.tail = this.tail.getPrev();
                    if (this.tail != null)
                        this.tail.clearNext();
                }
            }
            else
            {
                // Deleting middle value
                previous.setNext(current.getNext());
                previous.getNext().setPrev(previous);
            }
        }

        return current;
    }

    public DoublyLinkedListNode deleteItem(char data)
    {
        /*
         * Deletes and returns the first node with the specified data in the linked list
         * Returns null if the item doesn't exist
         */
        DoublyLinkedListNode current;
        DoublyLinkedListNode previous;
 
        current = this.head;
        previous = null;

        while ((current != null) && (current.getData() != data))
        {
            previous = current;
            current = current.getNext();
        }

        if (current != null)
        {
            this.size -= 1;
            if((current == this.head) || (current == this.tail))
            {
                if (current == this.head)
                {
                    // Deleting head node
                    this.head = this.head.getNext();
                    if (this.head != null)
                        this.head.clearPrev();
                }
                if(current == this.tail)
                {
                    // Deleting tail node
                    this.tail = this.tail.getPrev();
                    if (this.tail != null)
                        this.tail.clearNext();
                }
            }
            else
            {
                // Deleting middle value
                previous.setNext(current.getNext());
                previous.getNext().setPrev(previous);
            }
        }

        return current;
    }

    public boolean contains(char data)
    {
        /*
         * Checks if the linked list contains a node with the specified data
         */
        DoublyLinkedListNode current;
 
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
        return this.size;
    }

    public DoublyLinkedListNode getHead()
    {
        /*
         * Returns the head of the linked list
         */
        return this.head;
    }

    public DoublyLinkedListNode getTail()
    {
        /*
         * Returns the tail of the linked list
         */
         return this.tail;  
    }

    public int getIndex(char data)
    {
        /*
         * Returns the index of the first node with the specified data
         * Returns -1 if the index does not exist
         */
        DoublyLinkedListNode current;
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

    public DoublyLinkedListNode getNode(int index)
    {
        /*
         * Returns the node at the specified index
         * Returns null if the index does not exist
         */

        // Check that value is apropriate
        if(index < 0)
            return null;

        int index_counter;
        DoublyLinkedListNode current; 

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
        return this.head == null;
    }

    public void clear()
    {
        /*
         * Clears the linked list
         */
        this.head = null;
        this.tail = null;
    }

    public String toString()
    {
        /*
         * Returns the linked list in string form
         * The format is just the data from each node concatenated together
         * See the tests for an example
         * There should be no trailing whitespace
         */
        DoublyLinkedListNode current;
        StringBuilder sb = new StringBuilder();
 
        current = this.head;

        while (current != null)
        {
            sb.append(current.getData());
            current = current.getNext();
        }

        return sb.toString();
    }

    public String toStringReverse()
    {
        /*
         * Returns the linked list in string form in reverse
         * The format is just the data from each node concatenated together
         * There should be no trailing whitespace
         * Do not just call toString and reverse it, this will receive no points
         */
        DoublyLinkedListNode current;
        StringBuilder sb = new StringBuilder();
 
        current = this.tail;

        while (current != null)
        {
            sb.append(current.getData());
            current = current.getPrev();
        }

        return sb.toString();
    }
}

class DoublyLinkedListNode
{
    /*
     * Our instance variables
     * data will hold a char
     * next is the reference to the next element after this node (null if there is none)
     */

    private char data;
    private DoublyLinkedListNode next;
    private DoublyLinkedListNode prev;

    public DoublyLinkedListNode(char data)
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

    public DoublyLinkedListNode getNext()
    {
        /*
         * Returns the DoublyLinkedListNode referenced by next
         */
        return this.next;
    }

    public DoublyLinkedListNode getPrev()
    {
        /*
         * Returns the DoublyLinkedListNode referenced by prev
         */ 
        return this.prev; 
    }

    public void setData(char data)
    {
        /*
         * Allows us to change the data stored in our DoublyLinkedListNode
         */
        this.data = data;
    }

    public void setNext(DoublyLinkedListNode node)
    {
        /*
         * Allows us to change the next DoublyLinkedListNode
         */
        this.next = node;
    }

    public void setPrev(DoublyLinkedListNode node)
    {
        /*
         * Allows us to change the prev DoublyLinkedListNode
         */
        this.prev = node;
    }

    public void clearNext()
    {
        /*
         * Removes the reference to the next DoublyLinkedListNode, replacing it with null
         */
        this.next = null;
    }

    public void clearPrev()
    {
        /*
         * Removes the reference to the prev DoublyLinkedListNode, replacing it with null
         */
        this.prev = null;
    }

    public String toString()
    {
        /*
         * Returns our data in string form
         */
        return String.valueOf(this.data);
    }
}
