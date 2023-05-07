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
        addr = AddressParser.parse("Rued Langgaards Vej 7 2300 København S");
        assertEquals("Rued Langgaards Vej", addr.getStreet());
        assertEquals("7", addr.getHouse());
        assertEquals("2300", addr.getPostcode());
        assertEquals("København S", addr.getCity());
        assertEquals("Rued Langgaards Vej 7, 2300 København S", addr.toString());
    }

    @Test
    //tests case sensitivity
    void caseSensitivityTest(){
        addr = AddressParser.parse("Rued Langgaards Vej 7 2300 København S");
        Address addr2 = AddressParser.parse("rued langgaards vej 7 2300 københavn s");
        Address addr3 = AddressParser.parse("rUed langGaArds vej 7 2300 kØBENHAvn S");
        assertEquals(addr, addr2);
        assertEquals(addr, addr3);
    }

    @Test
    //tests an address with letters in the house number
    void houseNumbersWithLettersTest(){
        addr = AddressParser.parse("Vejnavn 23D 1234 Bynavn");
        assertEquals("23D", addr.getHouse());
    }

    @Test
    //tests rare characters in road and city names
    void rareCharactersTest(){
        addr = AddressParser.parse("10. Februar Vej 1 1234 Bylderup-Bov");
        assertEquals("10. Februar Vej", addr.getStreet());
        assertEquals("Bylderup-Bov", addr.getCity());
        addr = null;

        addr = AddressParser.parse("Christian II's Allé 1 1234 Nr. Snede");
        assertEquals("Christian II's Allé", addr.getStreet());
        assertEquals("Nr. Snede", addr.getCity());
        addr = null;

        addr = AddressParser.parse("Härnösandvej 1 1234 Veksø Sjælland");
        assertEquals("Härnösandvej", addr.getStreet());
        assertEquals("Veksø Sjælland", addr.getCity());
        addr = null;

        addr = AddressParser.parse("Tårs Fiskeri- Og Lystbådehavn 1 1234 Gjerlev J");
        assertEquals("Tårs Fiskeri- Og Lystbådehavn", addr.getStreet());
        assertEquals("Gjerlev J", addr.getCity());
        addr = null;

        addr = AddressParser.parse("Tove Maës Vej 1 1234 Århus");
        assertEquals("Tove Maës Vej", addr.getStreet());
        assertEquals("Aarhus", addr.getCity());
        addr = null;
    }

    @Test
    //tests that exceptions are thrown when the formatting is off
    void badFormattingTest(){
        try {
            addr = AddressParser.parse("Vejnavn 1AA 1234 Bynavn"); //Illegal house number
            fail();
        } catch (AddressParser.InvalidAddressException e){
            addr = null;
        }

        try {
            addr = AddressParser.parse("Vejnavn 1Q 1234 Bynavn"); //Illegal house number
            fail();
        } catch (AddressParser.InvalidAddressException e){
            addr = null;
        }

        try {
            addr = AddressParser.parse("Vejnavn 1000 1234 Bynavn"); //Illegal house number
            fail();
        } catch (AddressParser.InvalidAddressException e){
            addr = null;
        }

        try {
            addr = AddressParser.parse("Vejnavn 1 12345 Bynavn"); //Illegal postal code
            fail();
        } catch (AddressParser.InvalidAddressException e){
            addr = null;
        }
    }

    @Test
    //tests that address specifications like floor number don't disrupt the creation of the address
    void extraFluffTest(){
        addr = AddressParser.parse("Refshalevej 182X, 1. tv, 1, 1432 København");
        assertEquals("Refshalevej", addr.getStreet());
        assertEquals("182X", addr.getHouse());
        assertEquals("1432", addr.getPostcode());
        assertEquals("København", addr.getCity());
        assertEquals("Refshalevej 182X, 1432 København", addr.toString());
    }



    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        addr = null;
    }
}