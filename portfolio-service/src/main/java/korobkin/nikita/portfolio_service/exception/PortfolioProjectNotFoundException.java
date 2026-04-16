package korobkin.nikita.portfolio_service.exception;

public class PortfolioProjectNotFoundException extends AppException {
    public PortfolioProjectNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
