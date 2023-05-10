package program.model;

import program.shared.Address;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a string and returns an address
 */
public class AddressParser {
    //private final static Pattern PATTERN = Pattern.compile("^(?<street>[\\d]*\\s*[^,\\d]+)\\s+(?<house>\\d{0,3}[^\\d\\sojqiOJQI,.]?)\\s*,?.*(?<postcode>\\d{4})\\s*(?<city>[^\\d,]+)$");
    //private final static Pattern INCOMPLETEPATTERN = Pattern.compile("^(?<street>[\\d]*\\s*[^,\\d]+)\\s+(?<house>\\d{0,3}[^\\d\\sojqiOJQI,.]?).*$");
    //private final static Pattern PATTERN = Pattern.compile("^(?<street>[^,]*[^,\\d]+)\\s*(?<house>\s+[\\d]{1,3}[^\\d\\sojqiOJQI,.]?)?[\\s]*,?\\s*(?<postcode>[\\d]{4})?\\s*(?<city>\\s+[\\D\\s]*)?$");
    private final static Pattern PATTERN = Pattern.compile("^(?<street>[^,\\d]*)\\s*(?<house>\\d{1,3}[^\\d\\sojqiOJQI,.]?)?,?\\s*(?<postcode>\\d{4})?(?<city>[^,\\d]+)?$");

    public static Address parse(String address) {
        if (address.equals("")) throw new InvalidAddressException("Cannot search for address without the address", address);
        Matcher matcher = PATTERN.matcher(address);
        if (matcher.matches()) {
            String street = matcher.group("street");
            String house = matcher.group("house");
            String postcode = matcher.group("postcode");
            String city = matcher.group("city");

            return new Address(
                street != null ? street.trim() : street,
                house != null ? house.trim() : house,
                postcode != null ? postcode.trim() : postcode,
                city != null ? city.trim() : city
            );
        } else {
            throw new InvalidAddressException("Given address is formated incorrectly", address);
        }

    }

    public static class InvalidAddressException extends RuntimeException {
        public InvalidAddressException(String message, String address) {
            super(message + " " + address);
        }
    }

}