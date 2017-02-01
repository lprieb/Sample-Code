/* 
 * Name: Luis Prieb
 * Student ID: 2016843211
 * Don't forget to remove the package line.
 */

import java.util.ArrayList;
import java.util.Random;

/* 
 * java.util.ArrayList is for the ArrayList
 * java.util.Random is so you can generate your own hash functions
 * You should not import anything else
 */

public class CuckooHash
{
    private int a1, b1, a2, b2, n, numElements, chainLength;
    private double threshold;
    private int[] array1, array2;
    private boolean resized;
    private ArrayList<Integer> elements;
    private int currentHash; // Variable to store current hash used

    /* 
     * Our instance variables.
     *
     * a1 - int, a in the first hash function
     * b1 - int, b in the first hash function
     * a2 - int, a in the second hash function
     * b2 - int, b in the second hash function
     * n - int, the initial size of each array
     * numElements - int, the number of elements that have been inserted
     * chainLength - int, the length of the allowed chain before we resize
     * threshold - double, the point at which our arrays are too full and we resize
     * array1 - int[], the first hash table
     * array2 - int[], the second hash table
     * resized - boolean, set to true if the previous insertion caused a resize
     *           and false otherwise
     */

    public CuckooHash(int a1, int b1, int a2, int b2, int n, int chainLength, double threshold)
    {
        /*
         * Our constructor. Initialize the instance variables to their default
         * value or the value passed as a parameter.
         *
         * array1, array2 should initially be filled with 0's
         */
        this.a1 = a1;
        this.b1 = b1;
        this.a2 = a2;
        this.b2 = b2;
        this.n = n;
        this.chainLength = chainLength;
        this.threshold = threshold;

        this.numElements = 0;
        this.resized = false;

        this.elements = new ArrayList<Integer> (n);
        this.array1 = new int[n];
        this.array2 = new int[n];
    }

    public int insert(int data)
    {
        /*
         * insert data into our CuckooHash if it is not already contained
         * be sure to update resized if necessary
         *
         * In this version you can use any method to generate the new hash
         * functions after resizing.
         *
         * Note that java.util.Random is imported.
         * 
         * return the value that is inserted or -1 if it already exists
         */
        boolean inserted = false;
        int position;
        int currentValue;
        int moves = 0;
        resized = false;
        currentHash = 1;

        if(data == 0)
            return -1; // invalid value

        if(contains(data))
            return -1;

        // Check if threshold would be reached
        if((numElements+1.0)/(2.0*n) >= threshold)
        {
            resize();
            resized = true;
        }

        elements.add(data);

        while(!inserted)
        {
            position = doHash(data);
            currentValue = getArrayValue(position);
            if(currentValue == 0)
            {
                setArrayValue(position, data);
                inserted = true;
                numElements++;
            }
            else
            {
                setArrayValue(position,data);
                data = currentValue;
                switchHashAndArray();
                moves += 1;
            }
            if(moves >= chainLength)
            {
                resize();
                resized = true;
                moves = 0;
            }

        }

        return data;

    }

    public int insert(int data, int a1, int b1, int a2, int b2)
    {
        /*
         * insert data into our CuckooHash if it is not already contained
         * be sure to update resized if necessary
         *
         * This version allows you to specify the new constants for the hash
         * function that will exist after resizing. If the insert does not cause
         * a resize, it should NOT change the hash functions.
         * 
         * return the value that is inserted or -1 if it already exists
         */

        boolean inserted = false;
        int position;
        int currentValue;
        int moves = 0;
        resized = false;
        currentHash = 1;

        if(data == 0)
            return -1; // invalid value

        if(contains(data))
            return -1;

        // Check if threshold would be reached
        if((numElements+1.0)/(2.0*n) >= threshold)
        {
            resize(a1,b1,a2,b2);
            resized = true;
        }

        elements.add(data);

        while(!inserted)
        {
            position = doHash(data);
            currentValue = getArrayValue(position);
            if(currentValue == 0)
            {
                setArrayValue(position, data);
                inserted = true;
                numElements++;
            }
            else
            {
                setArrayValue(position,data);
                data = currentValue;
                switchHashAndArray();
                moves += 1;
            }
            if(moves >= chainLength)
            {
                resize(a1, b1, a2, b2);
                resized = true;
                moves = 0;
            }
      
        }
        return data;

    }

    public int delete(int data)
    {
        /*
         * delete data from our CuckooHash
         *
         * return the deleted value or -1 if it is not in the CuckooHash
         */
        int position1 = hash1(data);
        int position2 = hash2(data);
        if(array1[position1] == data)
        {
            array1[position1] = 0;
            numElements -= 1;
            elements.remove(elements.indexOf(data));
            return data;
        }
        else if (array2[position2] == data)
        {
            array2[position2] = 0;
            numElements -= 1;
            elements.remove(elements.indexOf(data));
            return data;
        }
        else
        {
            return -1;
        }

    }

    public boolean contains(int data)
    {
        /*
         * checks containment
         *
         * return true if data is in the CuckooHash
         */

        // Checks both posible locations for data
        if(data == 0)
        {
            // 0 is invalid data
            return false;
        }

        if(array1[hash1(data)] == data || array2[hash2(data)] == data)
            return true;
        else
            return false;

    }

    public int hash1(int x)
    {
        /*
         * Our first hash function
         * Remember, hashes are defined as (a,b,N) = ax+b (mod N)
         *
         * return the value computed by the first hash function
         */
        return ((a1*(x)+b1)%n);
    }

    public int hash2(int x)
    {
        /*
         * Our second hash function
         * Remember, hashes are defined as (a,b,N) = ax+b (mod N)
         *
         * return the value computed by the second hash function
         */
        return ((a2*(x)+b2)%n);
    }

    private void resize()
    {
        // Find new hash function
        int [] result = new int[3];

        n *= 2; // resizing

        // Find first hash function;
        result = findHashValues(n);

        // if a hash function cannot be found with the current n, increase n by 1 until one can be found;
        while(result[2] == 0)
        {
            n += 1;
            result = findHashValues(n);
        }

        a1 = result[0];
        b1 = result[1];

        // Find second hash function
        if(a1 + 1 < n)
            result = findHashValues(a1 + 1, 0, n); // Ensure a different hash function is found
        else
            result = findHashValues(a1, b1 + 1, n);
        // Assume that another hash function can be found with the same n

        a2 = result[0];
        b2 = result[1];

        int [] temp1 = array1.clone();
        int [] temp2 = array2.clone();

        array1 = new int[n];
        array2 = new int[n];

        for(int elem : temp1)
            insert(elem);
        for(int elem : temp2)
            insert(elem);

    }
    private int [] findHashValues(int n)
    {
        return findHashValues(0, 0, n);
    }
    private int [] findHashValues(int startA, int startB, int n)
    {
        int [] result = new int[3]; // First position is a, second position is b, third says if value was found

        for(int i = startA; i < n; i++)
        {
            for(int j = startB; j < n; j++)
            {
                if(testHash(i,j,n, 5))
                {
                    result[0] = i;
                    result [1] = j;
                    result[2] = 1; // 1 is found, 0 is not found
                    return result; // Automatically breaks loop
                }
            }
        }

        return result;
        
    }

    private boolean testHash(int a, int b, int n, int k)
    {
        // Tests if a given group of values produces a hash function, 
        // such that value = (a*x + b)%n, produces a uniform distribution
        // k is used for the number of full ranges of n over which the test is done

        int [] testList = new int[n]; // Each value Automatically intialized to 0
        int hashValue;

        // Mark values reached by hash function
        for( int i = 0; i < k*n ; i++)
        {
            hashValue = (a*i + b)%n;
            testList[hashValue]++;
        }

        // Test if a uniform distribution is found
        for(int i = 0; i < n; i++)
            if(testList[i] != k)
                return false;

        return true;
    }
    public void resize(int a1, int b1, int a2, int b2)
    {
        /*
         * resize our CuckooHash and make new hash functions
         */
        this.a1 = a1;
        this.b1 = b1;
        this.a2 = a2;
        this.b2 = b2;

        n *= 2;

        int [] temp1;
        int [] temp2;

        temp1 = array1.clone();
        temp2 = array2.clone();

        array1 = new int[n];
        array2 = new int[n];

        for(int elem : temp1)
            insert(elem);
        for(int elem : temp2)
            insert(elem);

    }

    public void setA1(int a1)
    {
        /*
         * set the value a1
         */
        this.a1 = a1;
    }

    public void setB1(int b1)
    {
        /*
         * set the value b1
         */
        this.b1 = b1;
    }

    public void setA2(int a2)
    {
        /*
         * set the value a2
         */
        this.a2 = a2;
    }

    public void setB2(int b2)
    {
        /*
         * set the value b2
         */
        this.b2 = b2;
    }

    public void setThreshold(double t)
    {
        /*
         * set the threshold
         */
        this.threshold = t;
    }

    public void setChainLength(int c)
    {
        /*
         * set the threshold
         */
        this.chainLength = c;
    }

    public int getElementArray1(int index)
    {
        /*
         * return element at index from array1
         */
        return array1[index];
    }
    public String getHash1String()
    {
        String result = String.format("(x*%d + %d) %%%d",a1,b1,n);
        return result;
    }
    public String getHash2String()
    {
        String result = String.format("(x*%d + %d) %%%d",a2,b2,n);
        return result;
    }

    public int getElementArray2(int index)
    {
        /*
         * return element at index from array2
         */
        return array2[index];
    }

    public int getA1()
    {
        /*
         * return a1
         */
        return a1;
    }

    public int getB1()
    {
        /*
         * return b1
         */
        return b1;
    }

    public int getA2()
    {
        /*
         * return a2
         */
        return a2;
    }

    public int getB2()
    {
        /*
         * return b2
         */  
         return b2;      
    }

    public int getN()
    {
        /*
         * return n
         */
        return n;
    }

    public double getThreshold()
    {
        /*
         * return threshold
         */
        return threshold;
    }

    public int getChainLength()
    {
        /*
         * return chainLength
         */
        return chainLength;
    }

    public int[] getArray1()
    {
        /*
         * return array1
         */
        return array1;
    }

    public int[] getArray2()
    {
        /*
         * return array2
         */
        return array2;
    }

    public int getNumElements()
    {
        /*
         * return the number of elements in the CuckooHash
         */
        return numElements;
    }

    public ArrayList<Integer> getElements()
    {
        /*
         * return all of the elements that are in the CuckooHash in their
         * inserted order
         */
        return this.elements;
    }

    public boolean getResized()
    {
        /*
         * return the resized variable
         */
        return resized;
    }

    public String toString()
    {
        /*
         * return the string version of the CuckooHash
         *
         * the required format is: 
         * all values in array1 (including 0's) separated by commas followed by
         * a bar | followed by all values of array2 (including 0's) separated by
         * commas
         *
         * there should be no spaces or trailing commas
         */

        String elem;
        StringBuilder s = new StringBuilder();
        for(int i = 0 ; i < n ; i++)
        {
            elem = Integer.toString(array1[i]);
            s.append(elem);
            if(i < (n-1))
                s.append(',');
        }
        s.append('|');
        for(int i = 0 ; i < n ; i++)
        {
            elem = Integer.toString(array2[i]);
            s.append(elem);
            if(i < (n-1))
                s.append(',');
        }
        return s.toString();
    }

    private void switchHashAndArray()
    {
        if(currentHash == 1)
        {
            currentHash = 2;
        }
        else
        {
            currentHash = 1;
        }
    }
    private int doHash(int data)
    {
        int position;
        if(currentHash == 1)
            position = hash1(data);
        else
            position = hash2(data);

        return position;
    }
    private int getArrayValue(int position)
    {
        // Returns value at the position specified for the current array that is being attempted to be inserted to
        if(currentHash == 1)
            return array1[position];
        else
            return array2[position];
    }
    private void setArrayValue(int position, int value)
    {
        if(currentHash == 1)
            array1[position] = value;
        else
            array2[position] = value;
    }

}