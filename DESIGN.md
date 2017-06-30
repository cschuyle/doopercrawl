# Some Notes on what I was thinking

I TDD'd starting from LinkExtractor (which then became "wrapped" by PageProcessor).  FollowPolicy quickly became "injectable" (although I don't actually use any DI framework; I just construct the wired-up production stuff in `Main`), and lastly LinkNormalizer.  LinkNormalizer was sadly the place where I spent most effort - because instead of noticing that java.net.URL has most of the stuff I wanted in order to general URLs from relative URL HREFs, I invented my own at first.  Which is tedious.  Then I ripped most of that invented code out and delegated to java.net.URL instead.

## The Things are:

`Crawler` is the entry point.  It starts a traversal of the link "graph" starting from the initial URL.

`FollowPolicy` is a the strategy used to decide whether to follow the link and keep on extracting.

`PageProcessor` is in charge of actually downloading the pages.  It's the big piece of code without tests.  It's mostly StackOverflow-borrowed Apache HttpClient code.

`LinkExtractor` grabs the &lt;A HREF&gt; links (using regular expressions, and yes, I *know* that's my *first* problem...)

`LinkNormalizer` attempts to scrub the extracted links and normalize them.

`Link` is just a wrapper around Commons-Lang `Pair<T,R>`, adding `toString` (and making the code cleaner to look at).

`SiteMap` is the Crawler's private "state".  It remembers where it's been (to avoid circular paths, and to allow output at the end).
