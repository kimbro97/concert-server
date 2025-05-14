package kr.hhplus.be.server.support.lock;

public class DistributedService {
	@DistributedLock(key = "#id", leaseTime = 2)
	public String lock(String id) {
		return "success";
	}

	@DistributedLock(key = "#id", leaseTime = 2)
	public String lockException(String id) {
		throw new RuntimeException("비즈니스 실패");
	}
}
