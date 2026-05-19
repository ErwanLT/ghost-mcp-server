package fr.eletutour.ghostmcpserver.client;

public class GhostApiException extends RuntimeException {

    private final String apiName;
    private final String operation;
    private final Integer statusCode;
    private final String responseBody;

    public GhostApiException(String message,
                             String apiName,
                             String operation,
                             Integer statusCode,
                             String responseBody,
                             Throwable cause) {
        super(message, cause);
        this.apiName = apiName;
        this.operation = operation;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public String apiName() {
        return apiName;
    }

    public String operation() {
        return operation;
    }

    public Integer statusCode() {
        return statusCode;
    }

    public String responseBody() {
        return responseBody;
    }
}
