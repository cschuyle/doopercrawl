# (super-)dooper-crawler

A little tiny web crawler in Java.  One of those "Toy Problems".

# Crawl a domain

`./run http://some.domain.com`

*OR* if that doesn't work:

`./gradlew run -Purl=some.domain.com`

The output format is a sequence of "links" of the format:

```
LINK: from-url ==> to-url
```

This means that on the `from-url` page, there is a link to the `to-url` page.  You can think of the individual links as edges in a graph.

# Run the tests

% `./gradlew test`

*OR*

C:\\> `gradlew.bat`

... then you can drive yourself to the test reports in HTML:

`./build/reports/tests/test/index.html`

# Caveats / TO-DO's
* A number of assumptions have been made regarding how to "canonicalize" URLs:
    * These transformations are done to URLs:
        * Any tags (#tag suffix) are stripped.
        * If a link is missing the domain, an attempt is made to "normalize"/"canonicalize" it by prefixing it with the root's full protocol, domain, and port.
        * Relative paths (../.. etc.) are normalized properly in many cases.
    * There are sooo many other things that could be done, among them:
        * Query strings are left untouched - they could stripped; or the order of the parameters could be normalized.
        * No adjustment of case is done.  In reality host names at least should be case-insensitive.
        * A sub-domain (i.e. www.domain.com versus domain.com) is considered completely separate.  In reality sub-domains could be considered valid targets for crawling.
* Both URL parsing and HTML parsing should be considered not production quality.  More robust behavior for varied input formats would make the crawling more precise, and capture more pages.  In summary:
    * Consider something more robust than java.net.URL/URI to parse URLs.
    * Consider something besides a regular expression to parse URLS out of HTML.
