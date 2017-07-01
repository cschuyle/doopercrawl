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
* The biggest problem is the current inaccuracy in normaizing URLs.  This results in 404's, as well as endless loops (for which I inserted an ugly temporary truncation in Crawler).  The next step in fixing this is to detect redirects (which should inform of the *actual* location), which will make the LinkNormalizer simpler, and cut down on warnings.  To see the current plethora of warnings, just try to crawl google.com.
* Lots of sites will limit request rates.  A mechanism to be "nice" enough to these sites is not implemented.  Because of this, the committed version of the code doesn't exploit parallelism at all
    * HOWEVER, there is a commented - out line that if uncommented makes it parallel.  For this reason, SitMap - the one shared-state object - is thread-safe.
* A number of assumptions have been made regarding how to "canonicalize" URLs:
    * These transformations are done to URLs:
        * Any tags (#tag suffix) are stripped.  Otherwise, URL canonicalization is delegated to java.net.URL (after a long time spent trying to do it myself)..
        * Sadly, there appear still to be bugs; trying to crawl google.com is sad.  This is a TO-DO.
    * There are sooo many other things that could be done, among them:
        * Detect redirects and use the last-redirected-to URL (as stated above).
        * Query strings are left untouched - they could stripped; or the order of the parameters could be normalized.
        * No adjustment of case is done.  In reality host names at least should be case-insensitive.
* A sub-domain (i.e. www.domain.com versus domain.com) is considered completely separate.  In reality sub-domains could be considered valid targets for crawling.
