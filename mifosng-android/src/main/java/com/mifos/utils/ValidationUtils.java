package com.mifos.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author malice
 */
public class ValidationUtils {

    private static final String DOMAIN_NAME_REGEX_PATTERN = "^[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private static final String IP_ADDRESS_REGEX_PATTERN = "^(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))\\.(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))\\.(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))\\.(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))$";

    private static Pattern domainNamePattern = Pattern.compile(DOMAIN_NAME_REGEX_PATTERN);

    private static Matcher domainNameMatcher;
    private static Pattern ipAddressPattern = Pattern.compile(IP_ADDRESS_REGEX_PATTERN);
    private static Matcher ipAddressMatcher;


    /**
     * Removing protocol names and trailing slashes
     * from the user entered domain name.
     *
     * @param url
     * @return filteredString
     */
    public static String sanitizeDomainNameInput(String url) {

        String filteredUrl;

        if (url.contains("https://")) {

            //Strip https:// from the URL
            filteredUrl = url.replace("https://", "");

        } else if (url.contains("http://")) {

            //String http:// from the URL
            filteredUrl = url.replace("http://", "");
        } else {

            //String URL doesn't include protocol
            filteredUrl = url;
        }

        if (filteredUrl.charAt(filteredUrl.length() - 1) == '/') {
            filteredUrl = filteredUrl.replace("/", "");
        }

        return filteredUrl;
    }

    /**
     * Validates Domain name entered by user
     * against valid domain name patterns
     * and also IP address patterns.
     *
     * @param hex
     * @return true if pattern is valid
     * and false otherwise
     */
    public static boolean validateURL(final String hex) {

        domainNameMatcher = domainNamePattern.matcher(hex);
        ipAddressMatcher = ipAddressPattern.matcher(hex);
        if (domainNameMatcher.matches()) return true;
        if (ipAddressMatcher.matches()) return true;

        //TODO MAKE SURE YOU UPDATE THE REGEX to check for ports in the URL
        return false;
    }

}
