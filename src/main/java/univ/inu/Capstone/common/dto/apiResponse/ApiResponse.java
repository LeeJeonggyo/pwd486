package univ.inu.Capstone.common.dto.apiResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private ResponseStatus status;
    private T data;

    public ApiResponse(Integer code, ResponseStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public ApiResponse(Integer code, ResponseStatus status, String message, T data) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
    }
    public static <T> ApiResponse<T> SUCCESS (String message) {
        // front 요청으로 SUCCESS 코드는 201로 통일
        return new ApiResponse<>(201, ResponseStatus.SUCCESS, message);
    }
    public static <T> ApiResponse<T> SUCCESS (String message, T data) {
        // front 요청으로 SUCCESS 코드는 201로 통일
        return new ApiResponse<>(201, ResponseStatus.SUCCESS, message, data);
    }

    public static <T> ApiResponse<T> FAILURE (Integer code, String message) {
        return new ApiResponse<>(code, ResponseStatus.FAIL, message);
    }

    public static <T> ApiResponse<T> ERROR (Integer code, String message) {
        return new ApiResponse<>(code, ResponseStatus.ERROR, message);
    }

}
