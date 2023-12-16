package es.unizar.urlshortener.core

class InvalidUrlException(url: String) : Exception("[$url] does not follow a supported schema")

class RedirectionNotFound(key: String) : Exception("[$key] is not known")

class QRNotAvailable(key: String) : Exception("[$key] QR is not available")

class UrlToShortNotReachable(url: String) : Exception("[$url] is not reachable, it is not possible to short it")

class UrlRegisteredButNotReachable(key: String) : Exception("[$key] The Uri you're trying to redirect to is not reachable ")

/**
 * Exception thrown when an alias already exists.
 */
class KeyAlreadyExists(key: String) : Exception("[$key] already exists")

class AliasAlreadyExists(alias: String) : Exception("[$alias] already exists")

class AliasContainsSlash(alias: String) : Exception("[$alias] contains a slash")