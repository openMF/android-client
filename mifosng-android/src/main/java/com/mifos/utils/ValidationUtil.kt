/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.utils

import com.mifos.core.network.BaseUrl
import java.net.URL
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author fomenkoo
 */
object ValidationUtil {
    private const val DOMAIN_NAME_REGEX_PATTERN = "^[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\" +
            ".[A-Za-z]{2,})$"
    private const val IP_ADDRESS_REGEX_PATTERN = "^(\\d|[1-9]\\d|1\\d\\d|2" +
            "([0-4]\\d|5[0-5]))\\.(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))\\." +
            "(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))\\.(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5])" +
            ")$"
    private const val NAME_REGEX_PATTERN = "^[\\p{Alpha} .'-]+$"
    private val domainNamePattern = Pattern.compile(DOMAIN_NAME_REGEX_PATTERN)
    private lateinit var domainNameMatcher: Matcher
    private val ipAddressPattern = Pattern.compile(IP_ADDRESS_REGEX_PATTERN)
    private lateinit var ipAddressMatcher: Matcher

    /**
     * Removing protocol names and trailing slashes
     * from the user entered domain name.
     *
     * @param url
     * @return filteredString
     */
    private fun sanitizeDomainNameInput(url: String): String {
        var filteredUrl: String
        filteredUrl = if (url.contains("https://")) {
            //Strip https:// from the URL
            url.replace("https://", "")
        } else if (url.contains("http://")) {
            //String http:// from the URL
            url.replace("http://", "")
        } else {
            //String URL doesn't include protocol
            url
        }
        if (filteredUrl.length > 0 && filteredUrl[filteredUrl.length - 1] == '/') filteredUrl =
            filteredUrl.replace("/", "")
        return filteredUrl
    }

    fun getInstanceUrl(validDomain: String, port: Int?): String {
        var validDomain = validDomain
        validDomain = sanitizeDomainNameInput(validDomain)
        return if (port != null) {
            BaseUrl.PROTOCOL_HTTPS + validDomain + ":" + port + BaseUrl.API_PATH
        } else {
            BaseUrl.PROTOCOL_HTTPS + validDomain + BaseUrl.API_PATH
        }
    }

    fun isValidUrl(url: String?): Boolean {
        return try {
            URL(url)
            true
        } catch (e: Exception) {
            false
        }
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
    fun isValidDomain(hex: String?): Boolean {
        domainNameMatcher = domainNamePattern.matcher(hex)
        ipAddressMatcher = ipAddressPattern.matcher(hex)
        if (domainNameMatcher.matches()) return true
        return ipAddressMatcher.matches()
        //TODO MAKE SURE YOU UPDATE THE REGEX to check for ports in the URL
    }

    /**
     * Validates the Name of Client, Group, Center etc.
     *
     * @param string Name
     * @return Boolean
     */
    fun isNameValid(string: String): Boolean {
        return string.matches(Regex(NAME_REGEX_PATTERN))
    }
}