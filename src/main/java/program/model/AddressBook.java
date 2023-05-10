package program.model;

import program.shared.Address;
import program.shared.MapElement;
import program.shared.MapPoint;
import program.shared.Point;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains all addresses in a ternary search trie and permits retrieval of their corresponding MapPoints
 */

public class AddressBook implements Serializable {
    private static AddressBook addressBook = null;
    private Trie tst;

    /**
     * Returns the singleton instance of the address book
     * @return
     */

    public static AddressBook getInstance() {
        if (addressBook == null) addressBook = new AddressBook();
        return addressBook;
    }

    private AddressBook(){
        tst = new Trie();
    }

    /**
     * Sets the current instance of the address book; used for opening with .obj files
     * @param _addressBook
     */
    public static void setInstance(AddressBook _addressBook) {
        addressBook = _addressBook;
    }

    /**
     * Adds an address to the address book
     * @param address
     * @param location
     */
    public void addAddress(Address address, Point location) {
        tst.put(address, location);
    }

    /**
     * Takes an address object and returns associated MapPoint
     * @param address
     * @return
     */
    public MapPoint addressSearch(Address address) {
        Map<String, Point> addresses = tst.get(address);
        if (addresses == null) throw new AddressParser.InvalidAddressException("Invalid address", address.toString());

        String restOfAddress = address.restOfAddress();

        for (String a : addresses.keySet()) {
            if (a.startsWith(address.restOfAddress())) return new MapPoint(addresses.get(a), "address");
        }

        return new MapPoint(addresses.values().iterator().next(), "address");
    }

    /**
     * Removes all addresses from the address book; should only be used when changing file
     */
    public void clear(){
        tst = new Trie();
    }
}

//Ternary search trie
class Trie implements Serializable {
    Node root;

    void put(Address address, Point location){
        String restOfAddress = address.getHouse() + " " + address.getCity();
        root = put(root, 0, address.getStreet(), restOfAddress, location);
    }

    Node put(Node node, int depth, String street, String restOfAddress, Point location){
        char c = street.charAt(depth);
        if (node == null) node = new Node(c);
        if (c < node.character) node.left = put(node.left, depth, street, restOfAddress, location);
        else if (c > node.character) node.right = put(node.right, depth, street, restOfAddress, location);
        else if (depth < street.length() - 1) node.middle = put(node.middle, depth+1, street, restOfAddress, location);
        else {
            if (node.addresses == null) node.addresses = new HashMap<>();
            node.addresses.put(restOfAddress, location);
        }
        return node;
    }

    Map<String, Point> get(Address address){
        String street = address.getStreet();

        Node node = get(root, street, 0);
        return node.addresses;
    }

    Node get(Node node, String query, int depth) {
        char c = query.charAt(depth);
        if (node == null) throw new IllegalArgumentException("No such address exists");
        if (c < node.character) return get(node.left, query, depth);
        else if (c > node.character) return get(node.right, query, depth);
        else if (depth < query.length() - 1) return get(node.middle, query, depth + 1);

        while (true){
            if (node.addresses != null)
                return node;
            node = node.middle;
        }
    }

}

class Node implements Serializable {
    Node left, middle, right;
    char character;
    Map<String, Point> addresses;

    Node (char c) {
        character = c;
    }
}