package minesweeper;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.io.*;
/**
 * TODO: Description
 */
public class BoardTest {
    
    // TODO: Testing strategy
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test(timeout = 10000)
    public void blankBoardSizeTest()
    {
        Board board=Board.createBoard(7,7);
        String expected= "- - - - - - -\n- - - - - - -\n- - - - - - -\n- - - - - - -\n- - - - - - -\n- - - - - - -\n- - - - - - -";
        assertEquals(expected,board.look());
    }
    @Test(timeout = 10000)
    public void blankBoardFileTest()
    {
        Board board=Board.createBoard(new File("/home/sandy/Advanced Software Construction in Java/ps2/boards/board_file_5"));
        String expected= "- - - - - - -\n- - - - - - -\n- - - - - - -\n- - - - - - -\n- - - - - - -\n- - - - - - -\n- - - - - - -";
        assertEquals(expected,board.look());
    }
    
}
