package kr.hhplus.be.server.interfaces.concert;

public class ConcertScheduleSeatResponse {
	private Integer seatNo;
	private Boolean isReserved;

	public ConcertScheduleSeatResponse(Integer seatNo, Boolean isReserved) {
		this.seatNo = seatNo;
		this.isReserved = isReserved;
	}

	public Integer getSeatNo() {
		return seatNo;
	}

	public Boolean getReserved() {
		return isReserved;
	}
}
