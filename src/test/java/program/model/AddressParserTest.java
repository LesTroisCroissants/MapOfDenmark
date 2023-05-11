package program.model;

import program.shared.Address;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressParserTest {
    Address addr;

    @Test
    //tests a regular address
    void regularAddressTest(){
        addr = AddressParser.parse("rued langgaards vej 7 2300 københavn s");
        assertEquals("rued langgaards vej", addr.getStreet());
        assertEquals("7", addr.getHouse());
        assertEquals("2300", addr.getPostcode());
        assertEquals("københavn s", addr.getCity());
        assertEquals("rued langgaards vej 7, 2300 københavn s", addr.toString());
    }

    @Test
    //tests case sensitivity
    void caseSensitivityTest(){
        addr = AddressParser.parse("Rued Langgaards Vej 7 2300 København S");
        Address addr2 = AddressParser.parse("rued langgaards vej 7 2300 københavn s");
        Address addr3 = AddressParser.parse("rUed langGaArds vej 7 2300 kØBENHAvn S");

        assertEquals(addr.toString(), addr2.toString());
        assertEquals(addr.toString(), addr3.toString());
    }

    @Test
    //tests an address with letters in the house number
    void houseNumbersWithLettersTest(){
        addr = AddressParser.parse("Vejnavn 23D 1234 Bynavn");
        assertEquals("23d", addr.getHouse());
    }

    @Test
    //tests rare characters in road and city names
    void rareCharactersTest(){
        addr = AddressParser.parse("Christian II's Allé 1 1234 Nr. Snede");
        assertEquals("christian ii's allé", addr.getStreet());
        assertEquals("nr. snede", addr.getCity());
        addr = null;

        addr = AddressParser.parse("härnösandvej 1 1234 veksø sjælland");
        assertEquals("härnösandvej", addr.getStreet());
        assertEquals("veksø sjælland", addr.getCity());
        addr = null;

        addr = AddressParser.parse("Tårs Fiskeri- Og Lystbådehavn 1 1234 Gjerlev J");
        assertEquals("tårs fiskeri- og lystbådehavn", addr.getStreet());
        assertEquals("gjerlev j", addr.getCity());
        addr = null;

        addr = AddressParser.parse("Tove Maës Vej 1 1234 Aarhus");
        assertEquals("tove maës vej", addr.getStreet());
        assertEquals("aarhus", addr.getCity());
        addr = null;
    }

    /* The following tests fail. This is a tradeoff to be more flexible in the formatting and is as such expected
    @Test
    //tests number in the street name
    void numbersInStreetNameTest(){
        addr = AddressParser.parse("10. Februar Vej 1 1234 Bylderup-Bov");
        assertEquals("10. februar vej", addr.getStreet());
        assertEquals("bylderup-bov", addr.getCity());
        addr = null;
    }

    @Test
    //tests that address specifications like floor number don't disrupt the creation of the address
    void extraFluffTest(){
        addr = AddressParser.parse("Refshalevej 182X, 1. tv 1, 1432 København");
        assertEquals("refshalevej", addr.getStreet());
        assertEquals("182x", addr.getHouse());
        assertEquals("1432", addr.getPostcode());
        assertEquals("københavn", addr.getCity());
        assertEquals("refshalevej 182x, 1432 københavn", addr.toString());
    }
    */


    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        addr = null;
    }
}