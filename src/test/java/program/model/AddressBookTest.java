package program.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import program.shared.Address;
import program.shared.MapPoint;
import program.shared.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the address book.
 * Testing bigger datasets has been left out due to time constraints and the evidence gathered from using it in context of the program.
 */

class AddressBookTest {

    AddressBook addressBook;

    @Test
    void retrieveAddressTest(){
        MapPoint mp = addressBook.addressSearch(new Address("Testvej", "12", "1234", "Testby"));
        assertEquals("0.0 0.0", mp.toString());
    }

    @Test
    void specialSymbolsTest(){
        MapPoint mp = addressBook.addressSearch(new Address("Kål", "999", "1111", "Hækkerup"));
        assertEquals("1.0 1.0", mp.toString());
    }

    @Test
    void autocompleteTest(){
        MapPoint mp = addressBook.addressSearch(new Address("K", null, null, null));
        assertEquals("1.0 1.0", mp.toString());
    }

    @Test
    void retrieveSpecificHouseNumberTest(){
        MapPoint mp = addressBook.addressSearch(new Address("Testvej", "11", "1234", "Testby"));
        assertEquals("2.0 2.0", mp.toString());
    }

    //Fails
    @Test
    void retrieveSpecificHouseNumberFromIncompleteQueryTest(){
        MapPoint mp = addressBook.addressSearch(new Address("Testvej", "11", null, null));
        assertEquals("2.0 2.0", mp.toString());
    }


    @BeforeEach
    void setUp() {
        addressBook = AddressBook.getInstance();
        List<Address> addresses = createAddresses();

        for (int i = 0; i < addresses.size(); i++)
            addressBook.addAddress(addresses.get(i), new Point(i,i));
    }

    private List<Address> createAddresses(){
        List<Address> addresses = new ArrayList<>();

        Address a = new Address("Testvej", "12", "1234", "Testby");
        addresses.add(a);

        a = new Address("Kål", "999", "1111", "Hækkerup");
        addresses.add(a);

        a = new Address("Testvej", "11", "1234", "Testby");
        addresses.add(a);

        return addresses;
    }

    @AfterEach
    void tearDown() {
        addressBook.clear();
    }
}