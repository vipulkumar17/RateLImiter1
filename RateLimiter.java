public interface RateLimiter{
    boolean tryAcquire();

    

}

private final long capacity;
private final double refillRate;
private double tokens;
private long Lastrefilltime;

public TokenBucketRateLimiter(long capacity,double refillpersec){
    this.capacity=capacity;
    this.refillRate=refillpersec/1_000_000_000;
    this.tokens=capacity;
    this.Lastrefilltime=System.nanoTime();
}


public synchronized boolean tryAcquire(){
    refill();
    if(tokens>=1){
        tokens-=1;
        return true;
    }
    return false;
}

private void refill(){
    long now=System.nanoTime();
    tokens=Math.min(capacity,tokens+(now-Lastrefilltime)*refillpersec;
    Lastrefilltime=now;
}
public synchronized boolean tryAcquire(){
    long now=System.nanoTimne();
    while(!timestamps.isEmpty() && now-timestamps.peekFirst()>=windowNanos){
        timestamps.pollFirst();
    }

    if(timestamps.size()<maxRequests){
        timestamps.addLast(now);
        return true;

    }
    return false;
}

private final ConcurrentMap<K,RateLimiter>limiters=new ConcurrentHashMap<>();
private final Supplier<RateLimiter> factory;

public boolean tryAcquire(K key){
    return limiters.computeIfAbsent(key,k->factory.get()).tryAcquire();
}
    

