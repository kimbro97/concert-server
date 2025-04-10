package kr.hhplus.be.server.interfaces.api.payment;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.interfaces.api.common.ApiResponse;
import kr.hhplus.be.server.service.payment.PaymentInfo;
import kr.hhplus.be.server.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentControllerDocs {

	private final PaymentService paymentService;

	@PostMapping
	public ApiResponse<PaymentInfo> pay(@RequestBody PaymentRequest request) {
		return ApiResponse.CREATE(paymentService.pay(request.toCommand()));
	}
}
