package pl.cinkus.backend.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import pl.cinkus.backend.dto.PredictRequestData;
import pl.cinkus.backend.dto.PredictResponseData;
import pl.cinkus.backend.exception.ErrorCode;
import pl.cinkus.backend.exception.WordServiceException;
import pl.cinkus.backend.mapper.WordDataMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PredictClient {
    private static final String REQUEST_URI = "/predict";
    private final RestClient restClient;

    public List<PredictResponseData> predict(List<PredictRequestData> requestDataList) {
        if (requestDataList == null || requestDataList.isEmpty()) {
            return List.of();
        }

        List<PredictResponseData> responseDataList;
        try {
            responseDataList = restClient.post()
                    .uri(REQUEST_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestDataList)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            throw new WordServiceException(ErrorCode.CANNOT_CALCULATE_PREDICTION, "We could not predict a chance");
        }

        if (responseDataList == null) {
            throw new WordServiceException(ErrorCode.PREDICTION_SERVICE_ERROR, "We could not predict a chance");
        }
        return responseDataList;
    }
}
