/**@author sandy
 *
 */
package minesweeper;
import java.util.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class to represent a board in a game of minesweeper.
 */
public class Board {
    private int m,n;
    private ConcurrentHashMap<AbstractMap.SimpleImmutableEntry<Integer,Integer>,Integer> board;
    // Rep invariant:
    // The hash map contains exactly m*n squares at any point of time with each square having state "untouched","dug" or "flagged" and each square either having or not having a bomb
    // Abstraction function:
    //  represents a board in a game of minesweeper with board.get(Entry(0,0)) containing the element at position 0,0 and board.get(Entry(m,n)) containing the element at position n,m
    // Thread safety argument:
    //   This class is threadsafe because it is immutable and synchronized-
    //     board is a threadsafe datatype and ensures atomic operations.
    //     There are no setter/mutator methods.
    //     There is no rep exposure.
    //     Methods cannot be overridden by subclasses as it has a private constructor.
    //     All accesses to board happen from within Board methods which are guarded by Board's lock

    /**
     *
     * @param x The number of columns of the board
     * @param y The number of rows of the board
     */
    private Board(int x,int y)
    {
        this.m=y;
        this.n=x;
        board=new ConcurrentHashMap<>(m*n);
        for(int i=0;i<m;i++)
            for(int j=0;j<n;j++) {
                AbstractMap.SimpleImmutableEntry<Integer,Integer> e=new AbstractMap.SimpleImmutableEntry<>(i,j);
                if (Math.random() < 0.75)
                    board.put(e,0);
                else
                    board.put(e,-2);
            }

    }

    /**
     *
     * @param file The file from which to load the board
     */
    private Board(File file)
    {
        try(
            BufferedReader in=new BufferedReader(new FileReader(file))
         ){
         String line;
         int nl =0;
         while((line=in.readLine())!=null)
          {
            if(nl==0) {
                this.n=Integer.parseInt(line.substring(0,line.indexOf(' ')));
                this.m=Integer.parseInt(line.substring(line.indexOf(' ')+1,line.length()));
                board=new ConcurrentHashMap<>(m*n);
                nl++;
                continue;
               }
            for(int i=0;i<(line.length()+1)/2;i++) {
                AbstractMap.SimpleImmutableEntry<Integer,Integer> e=new AbstractMap.SimpleImmutableEntry<>(nl-1,i);
                if (line.charAt(2 * i) - '0' == 1)
                    board.put(e,-2);
                else
                    board.put(e,0);
            }
            nl++;
          }
       }catch(IOException ioe)
        {
            ioe.getStackTrace();
        }
    }

    /**
     * A static factory method to return a Board instance based on the given size
     */
    public static Board createBoard(int x,int y)
    {
        return new Board(x,y);
    }

    /**
     * A static factory method to return a Board instance based on the given file
     */
    public static Board createBoard(File file)
    {
        return new Board(file);
    }

    /**
     *
     * @return The dimensions of the current board
     */
    public int[] getSize()
    {
        return new int[]{m,n};
    }

    /**
     *
     * @param i The y coordinate
     * @param j The x coordinate
     * @return True if the coordinate is valid else false
     */
    private boolean isValid(int i,int j)
    {
        return i>=0&&i<m&&j>=0&&j<n;
    }

    /**
     *
     * @param x The y coordinate
     * @param y The x coordinate
     * @return The number of neighbors that contain bombs
     */
    private int neighborsWithBomb(int x,int y)
    {
        int count=0;
        for(int i=x-1;i<=x+1;i++)
            for(int j=y-1;j<=y+1;j++)
                if(!(i==x&&j==y)&&isValid(i,j)&&board.get(new AbstractMap.SimpleImmutableEntry<>(i,j))<0)
                    count++;
        return count;
    }

    /**
     * A private utility method to recursively dig the neighbors of a dug square if they do not contain a bomb
     */
    private void digBFS(int x,int y)
    {
        if(neighborsWithBomb(x,y)==0) {
            for (int i = x - 1; i <= x + 1; i++)
                for (int j = y - 1; j <= y + 1; j++) {
                    if (!(i == x && j == y) && isValid(i,j) && board.get(new AbstractMap.SimpleImmutableEntry<>(i, j)) == 0) {
                        board.replace(new AbstractMap.SimpleImmutableEntry<>(i, j), 2);
                        digBFS(i,j);
                    }
                }
        }
    }

    /**
     * A method to perform the dig operation
     * @param x The x coordinate
     * @param y The y coordinate
     * @throws NoSuchElementException if a bomb is found
     */
    public synchronized void dig(int x,int y)throws NoSuchElementException
    {
        if(!isValid(y,x))
            return;
        AbstractMap.SimpleImmutableEntry e=new AbstractMap.SimpleImmutableEntry<>(y,x);
        if(board.get(e)==-2) {
            board.replace(e,2);
            digBFS(y,x);
            throw new NoSuchElementException("Bomb Found!!!");
        }
        if(board.get(e)==0) {
            board.replace(e, 2);
            digBFS(y,x);
        }

    }

    /**
     * A method to perform the flag operation
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public synchronized void flag(int x,int y)
    {
        if(!isValid(y,x))
            return;
        AbstractMap.SimpleImmutableEntry e=new AbstractMap.SimpleImmutableEntry<>(y,x);
        if(board.get(e)==0)
           board.replace(e,1);
        if(board.get(e)==-2)
            board.replace(e,-1);
    }

    /**
     * A method to perform the deflag operation
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public synchronized void deflag(int x,int y)
    {
        if(!isValid(y,x))
            return;
        AbstractMap.SimpleImmutableEntry e=new AbstractMap.SimpleImmutableEntry<>(y,x);
        if(board.get(e)==1)
            board.replace(e,0);
        if(board.get(e)==-1)
            board.replace(e,-2);
    }

    /**
     * A method to return a string representation of the current board
     * @return A string representation of the board
     */
    public synchronized String look()
    {
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<m;i++) {
            for (int j = 0; j <n; j++) {
                if (Math.abs(board.get(new AbstractMap.SimpleImmutableEntry<>(i, j))) == 1)
                    sb.append("F");
                else if (board.get(new AbstractMap.SimpleImmutableEntry<>(i, j)) == 2) {
                    int c = neighborsWithBomb(i,j);
                    if (c == 0)
                        sb.append(" ");
                    else
                        sb.append(Integer.toString(c));
                } else
                    sb.append("-");
                if(j!=n-1)
                 sb.append(" ");
            }
            if(i!=m-1)
             sb.append("\n");
        }
            return sb.toString();
    }
}
