package korobkin.nikita.portfolio_service.exception;

public class PortfolioNotFoundException extends AppException {
    public PortfolioNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
