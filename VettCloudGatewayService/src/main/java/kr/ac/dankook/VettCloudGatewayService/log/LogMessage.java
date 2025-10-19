package kr.ac.dankook.VettCloudGatewayService.log;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogMessage {

    GATEWAY_REQUEST,
    SUCCESS_GATEWAY_JWT_AUTHENTICATION,
    SUCCESS_GATEWAY_PASSPORT,
    GATEWAY_RESPONSE,

    FALLBACK_CALLED_PASSPORT_GRPC,
    ERROR_IN_GATEWAY,
    ERROR_RESPONSE_SERIALIZATION_FAILED

}
