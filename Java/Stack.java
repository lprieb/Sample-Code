/*
 * Name: Luis Prieb
 * Student ID: 2016843211
 */

public class Stack
{
    /*
     * Our instance variables
     * capacity is the maximum number of elements allowed in the stack
     * ll is the (singly) linked list you must use to build your stack
     */

    private int capacity;
    private SinglyLinkedList ll;

    public Stack(int capacity)
    {
        /*
         * Our constructor
         * Should initalize the instance variables to their default value
         */
        this.capacity =  capacity;
        ll = new SinglyLinkedList();
    }

    public int push(int data)
    {
        /*
         * Push the data to the top of the stack
         * Return the data
         * If the stack is full, return -1
         */
        if (ll.getSize() < this.capacity)
        {
            ll.add(data, ll.getSize());
            return data;
        }
        else
        {
            return -1;
        }

    }

    public int peek()
    {
        /*
         * Peek from the data at the top of the stack
         * If the stack is empty, return -1
         */
        SinglyLinkedListNode node;

        if (ll.isEmpty())
            return -1;
        else
        {
            // Uses minus 1 since index counts starts from zero
            node = ll.getNode(ll.getSize()-1);
            return node.getData();
        }
    }

    public int pop()
    {
        /*
         * Pop the data at the top of the stack
         * If the stack is empty, return -1
         */
        SinglyLinkedListNode node;

        if (ll.isEmpty())
            return -1;
        else
        {
            node = ll.delete(ll.getSize() -1);
            return node.getData();
        }
    }

    public void clear()
    {
        /*
         * Clear the stack
         */
        ll.clear();
    }

    public int getSize()
    {
        /*
         * Return the number of elements in the stack
         */
        return ll.getSize();
    }

    public int getCapacity()
    {
        /*
         * Return the capacity of the stack
         */
        return capacity;
    }

    public boolean isFull()
    {
        /*
         * Return whether or not the stack is full
         */
        return (ll.getSize() == capacity);
    }

    public boolean isEmpty()
    {
        /*
         * Return whether or not the stack is empty
         */
        return ll.isEmpty();
    }

    public String toString()
    {
        /*
         * Return the string representation of the stack
         * The string should be in order from the top of the stack down with
         * commas between each element
         * There should be no spaces between elements or commas
         * This should not alter the stack in any way
         */
        StringBuilder sb = new StringBuilder();
        String original_str = ll.toString();
        int size = ll.getSize();

        for(int i = size-1; i >= 0; i--)
        {
            sb.append(original_str.charAt(i));
            if(i != 0)
                sb.append(",");
        }

        return sb.toString();
        
    }
}