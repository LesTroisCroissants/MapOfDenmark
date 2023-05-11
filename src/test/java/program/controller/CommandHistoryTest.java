package program.controller;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandHistoryTest {

    CommandHistory history = CommandHistory.getInstance();

    @Test
    void addAndRetrieveCommandTest(){
        history.add("test1");
        assertEquals("test1", history.getNext(""));
    }

    //Tests that the order of commands returned is correct
    @Test
    void orderOfCommandsTest(){
        history.add("0");
        history.add("1");
        history.add("2");

        for (int i = 2; i >= 0; i--)
            assertEquals(i + "", history.getNext(""));

    }

    //Tests that the order of commands is correctly reestablished when using a new command
    @Test
    void reestablishOrderTest(){
        history.add("0");
        history.add("1");
        history.add("2");
        history.add("3");

        for (int i = 0; i < 3; i++)
            history.getNext("");

        history.add("4");

        for (int i = 4; i >= 0; i--)
            assertEquals(i + "", history.getNext(""));
    }

    @Test
    void skipDuplicatesTest(){
        history.add("0");
        history.add("1");
        history.add("1");
        history.add("1");
        history.add("1");
        history.add("1");

        assertEquals("0", history.getNext("1"));
    }

    @Test
    void reachingTheEndOfTheHistoryTest(){
        try {
            history.getNext("");
        } catch (Exception e){
            assertEquals("No previous command", e.getMessage());
        }
    }

    @Test
    void reachingTheBeginningOfTheHistoryTest(){
        try {
            history.getPrevious("");
        } catch (Exception e){
            assertEquals("No previous command", e.getMessage());
        }
    }

    @Test
    void ignoreEmptyCommandsTest(){
        history.add("0");
        history.add("");

        assertEquals("0", history.getNext("1"));
    }
}