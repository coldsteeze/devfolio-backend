package korobkin.nikita.user_profile_service.exception;

public class NicknameAlreadyTakenException extends RuntimeException {
  public NicknameAlreadyTakenException(String message) {
    super(message);
  }
}
