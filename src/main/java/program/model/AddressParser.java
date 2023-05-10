package program.model;

import program.shared.Address;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a string and returns an address
 */
public class AddressParser {
    private final static Pattern PATTERN = Pattern.compile("^(?<street>[\\d]*\\s*[^,\\d]+)\\s+(?<house>\\d{0,3}[^\\d\\sojqiOJQI,.]?)\\s*,?.*(?<postcode>\\d{4})\\s*(?<city>[^\\d,]+)$");
    //private final static Pattern INCOMPLETEPATTERN = Pattern.compile("^(?<street>[\\d]*\\s*[^,\\d]+)\\s+(?<house>\\d{0,3}[^\\d\\sojqiOJQI,.]?).*$");
    //private final static Pattern NEWPATTERN = Pattern.compile("^(?<street>[\\d]*\\s*[^,\\d]+)\\s*(?<house>[\\d]{1,3}[^\\d\\sojqiOJQI,.]?)?[\\s]*,?\\s*(?<postcode>[\\d]{4})?\\s*(?<city>\\s+[\\D\\s]*)?$");
    public static Address parse(String address) {
        if (address.equals("")) throw new InvalidAddressException("Cannot search for address without the address", address);
        Matcher matcher = PATTERN.matcher(address);
        /*if(!matcher.matches()) {
            matcher = INCOMPLETEPATTERN.matcher(address);
            if (!matcher.matches())
                return new Address(
                        address,
                        null,
                        null,
                        null
                );
            return new Address(
                    matcher.group("street"),
                    matcher.group("house"),
                    null,
                    null
            );
        }*/
        return new Address(
                matcher.group("street").trim(),
                matcher.group("house").trim(),
                matcher.group("postcode").trim(),
                matcher.group("city").trim()
        );
    }

    public static class InvalidAddressException extends RuntimeException {
        public InvalidAddressException(String message, String address) {
            super(message + " " + address);
        }
    }

}