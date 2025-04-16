package kr.hhplus.be.server.interfaces.api.payment;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.interfaces.api.common.ApiResponse;
import kr.hhplus.be.server.service.payment.PaymentInfo;
import kr.hhplus.be.server.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentControllerDocs {

	private final PaymentService paymentService;

	@PostMapping("/api/v1/payment")
	public ApiResponse<PaymentInfo> pay(@RequestHeader("WAITTING-TOKEN") String uuid, @RequestBody PaymentRequest request) {
		return ApiResponse.CREATE(paymentService.pay(request.toCommand(uuid)));
	}
}
