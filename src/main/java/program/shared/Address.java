package program.shared;

public class Address {
    private final String street, house, postcode, city;

    public Address (
            final String street,
            final String house,
            final String postcode,
            final String city
    ) {
        this.street = street.toLowerCase().intern();
        this.house = house == null ? house : house.toLowerCase();
        this.postcode = postcode;
        this.city = city == null ? city : city.toLowerCase();
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