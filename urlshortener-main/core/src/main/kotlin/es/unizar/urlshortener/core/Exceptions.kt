package es.unizar.urlshortener.core

// Indicates an invalid URL exception.
class InvalidUrlException(url: String) : Exception("[$url] does not follow a supported schema")

// Represents a redirection not found exception for an unknown key.
class RedirectionNotFound(key: String) : Exception("[$key] is not known")

// Represents an exception for QR not available for a given key.
class QRNotAvailable(key: String) : Exception("[$key] QR is not available")

// Exception for cases where reachability hasn't been checked yet for a redirection key.
class ReachabilityNotChecked(key: String): Exception("[$key] redirection is not available yet")

// Exception when the URL being redirected to is not reachable.
class UrlRegisteredButNotReachable(key: String) : Exception("[$key] The Uri you're trying to redirect " +
        "to is not reachable")

// Indicates an exception when a key (alias) already exists.
class KeyAlreadyExists(key: String) : Exception("[$key] already exists")

// Indicates an exception when an alias already exists.
class AliasAlreadyExists(key: String) : Exception("[$key] this alias already exists")

// Indicates an exception when an alias contains a slash.
class AliasContainsSlash(key: String) : Exception("[$key] this alias contains a slash")
