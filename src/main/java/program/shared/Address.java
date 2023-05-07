package program.shared;

public class Address {
    private final String street, house, postcode, city;

    public Address (
            final String street,
            final String house,
            final String postcode,
            final String city
    ) {
        this.street = street;
        this.house = house;
        this.postcode = postcode;
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public String getHouse() {
        return house;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getCity() {
        return city;
    }

    public String toString() {
        return street + " " + house + ", " + postcode + " " + city;
    }

    public String restOfAddress() { return house + ", " + postcode + " " + city; }
}

class AddressBuilder {
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
