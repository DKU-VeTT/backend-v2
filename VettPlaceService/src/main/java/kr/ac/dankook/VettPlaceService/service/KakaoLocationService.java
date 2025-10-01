package kr.ac.dankook.VettPlaceService.service;

import kr.ac.dankook.VettPlaceService.dto.response.CoordinateResponse;
import kr.ac.dankook.VettPlaceService.dto.response.RouteResponse;
import kr.ac.dankook.VettPlaceService.error.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoLocationService {

    @Value("${app.kakao.api.key}")
    private String KAKAO_APP_KEY;
    @Value("${app.kakao.address-to-coordinate.host}")
    private String KAKAO_ADDRESS_TO_COORDINATE_HOST;
    @Value("${app.kakao.address-to-coordinate.path}")
    private String KAKAO_ADDRESS_TO_COORDINATE_PATH;
    @Value("${app.kakao.route.host}")
    private String KAKAO_ROUTE_HOST;
    @Value("${app.kakao.route.path}")
    private String KAKAO_ROUTE_PATH;

    public RouteResponse getRouteByCoordinate(
            double statLatitude, double statLongitude, double desLatitude,double desLongitude) {

        String apiKey = "KakaoAK " + KAKAO_APP_KEY;
        RestClient restClient = RestClient.create();

        try{
            String responseBody = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host(KAKAO_ROUTE_HOST)
                            .path(KAKAO_ROUTE_PATH)
                            .queryParam("origin",statLongitude + "," + statLatitude)
                            .queryParam("destination",desLongitude + "," + desLatitude)
                            .queryParam("waypoints","")
                            .build())
                    .header("Authorization", apiKey)
                    .header("Content-Type","application/json")
                    .retrieve()
                    .body(String.class);

            JSONObject json = new JSONObject(responseBody);
            JSONArray routes = json.getJSONArray("routes");
            JSONObject routeObject = (JSONObject) routes.get(0);
            JSONObject summaryObject = routeObject.getJSONObject("summary");

            int distanceObject = summaryObject.getInt("distance");
            String distance = (distanceObject >= 1000) ?
                    Math.round(distanceObject / 1000.f) + "km" :
                    distanceObject + "m";
            // Minute
            int duration = Math.round(summaryObject.getInt("duration") / 60.0f);
            int taxiFare = summaryObject.getJSONObject("fare").getInt("taxi");
            int tollFare = summaryObject.getJSONObject("fare").getInt("toll");
            return RouteResponse.builder()
                    .statLongitude(statLongitude).statLatitude(statLatitude)
                    .desLongitude(desLongitude).desLatitude(desLatitude)
                    .taxiFare(taxiFare).tollFare(tollFare).distance(distance).time(duration).build();
        }catch(JSONException e){
            log.error("Error occurred during parsing route info - stat {} {} / des {} {}",statLatitude,statLongitude,desLatitude,desLongitude);
            throw new ExternalApiException("좌표 형식이 올바르지 않아 교통 정보를 받아올 수 없습니다. 다시 시도해주세요.");
        }catch (Exception e){
            log.error("Error occurred during call kakao navi api - {}",e.getMessage());

            String jsonPart = e.getMessage().split(": ", 2)[1];
            jsonPart = jsonPart.replaceAll("^\"|\"$", "");
            JSONObject errorObject = new JSONObject(jsonPart);

            int errorCode = errorObject.getInt("code");
            String errorMsg = errorObject.getString("msg");
            if (errorCode == -2) throw new ExternalApiException(errorMsg);

            throw new ExternalApiException("외부 호출에 문제가 생겼습니다. 나중에 다시 시도해주세요.");
        }
    }
    public CoordinateResponse getCoordinateByAddress(String address){

        String apiKey = "KakaoAK " + KAKAO_APP_KEY;
        RestClient restClient = RestClient.create();
        try {
            String apiResponseBody = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host(KAKAO_ADDRESS_TO_COORDINATE_HOST)
                            .path(KAKAO_ADDRESS_TO_COORDINATE_PATH)
                            .queryParam("query", address)
                            .build())
                    .header("Authorization", apiKey)
                    .retrieve()
                    .body(String.class);

            JSONObject json = new JSONObject(apiResponseBody);
            JSONArray documents = json.getJSONArray("documents");
            double longitude = documents.getJSONObject(0).getDouble("x");
            double latitude = documents.getJSONObject(0).getDouble("y");
            return CoordinateResponse.builder()
                    .latitude(latitude).longitude(longitude).build();
        } catch (JSONException e) {
            log.error("Error occurred during parsing address - {}",address);
            throw new ExternalApiException("주소 형식이 올바르지 않습니다. 다시 시도해주세요.");
        } catch (Exception e){
            log.error("Error occurred during call kakao api - {}",e.getMessage());
            throw new ExternalApiException("외부 호출에 문제가 생겼습니다. 나중에 다시 시도해주세요.");
        }
    }
}
