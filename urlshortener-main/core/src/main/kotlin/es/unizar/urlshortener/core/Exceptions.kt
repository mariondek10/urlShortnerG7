package es.unizar.urlshortener.core

class InvalidUrlException(url: String) : Exception("[$url] does not follow a supported schema")

class RedirectionNotFound(key: String) : Exception("[$key] is not known")

class QRException(key: String) : Exception("[$key] does not exist")

class UrlToShortNotReachable(url: String) : Exception("[$url] is not reachable, it is not possible to short it")

class UrlRegisteredButNotReachable(key: String) : Exception("[$key] The Uri you're trying to redirect to is not reachable ")
