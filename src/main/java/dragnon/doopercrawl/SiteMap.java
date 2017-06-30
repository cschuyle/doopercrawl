package dragnon.doopercrawl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

class SiteMap {

    private final Set<String> fromUrls = new HashSet<>();
    private final Set<String> toUrls = new HashSet<>();
    private final Set<Link> links = new HashSet<>();
    private ReadWriteLock zuperlockr = new ReentrantReadWriteLock();

    public boolean containsFromLink(String fromUrl) {
        return iBeReading(() -> fromUrls.contains(fromUrl));
    }

    public boolean containsToLink(String fromUrl) {
        return iBeReading(() -> toUrls.contains(fromUrl));
    }

    public void addIfAbsent(Link link) {
        iBeWriting(() -> {
            if (links.contains(link)) {
                return;
            }
            links.add(link);

            markFollowedFrom(link.from());
        });
    }

    public void markFollowedTo(String toUrl) {
        iBeWriting(() -> toUrls.add(toUrl));
    }

    public Set<Link> getLinks() {
        return iBeReading(() -> links);
    }

    private void markFollowedFrom(String fromUrl) {
        iBeWriting(() -> fromUrls.add(fromUrl));
    }

    private <T> T iBeReading(Supplier<T> supplier) {
        zuperlockr.readLock().lock();
        try {
            return supplier.get();
        } finally {
            zuperlockr.readLock().unlock();
        }

    }

    private void iBeWriting(Runnable supplier) {
        zuperlockr.writeLock().lock();
        try {
            supplier.run();
        } finally {
            zuperlockr.writeLock().unlock();
        }

    }
}
