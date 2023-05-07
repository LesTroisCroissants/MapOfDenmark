package program.model;

import program.shared.Address;

public class AddressBuilder {
    private String street, house, postcode, city;
    public AddressBuilder() {}
    public void setStreet(String street) { this.street = street; }
    public void setHouse(String house) { this.house = house; }
    public void setPostcode(String postcode) { this.postcode = postcode; }
    public void setCity(String city) { this.city = city; }
    public Address build() {
        return new Address(this.street, this.house, this.postcode, this.city);
    }
}