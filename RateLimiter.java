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

