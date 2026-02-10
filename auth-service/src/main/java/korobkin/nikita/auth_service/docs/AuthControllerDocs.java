package korobkin.nikita.auth_service.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import korobkin.nikita.auth_service.dto.request.LoginRequest;
import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import korobkin.nikita.auth_service.dto.response.JwtResponse;
import korobkin.nikita.auth_service.exception.ApiError;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "Registration, login, token refresh, and logout")
public interface AuthControllerDocs {

    @Operation(
            summary = "Register a new user",
            description = """
                    Creates a new user.
                    
                    Returns access token in response body.
                    Sets refresh token in HttpOnly cookie.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User successfully registered",
                            headers = {
                                    @Header(
                                            name = "Set-Cookie",
                                            description = "HttpOnly cookie containing refresh token"
                                    )
                            },
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = JwtResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "email Email is required",
                                              "code": "VALIDATION_ERROR",
                                              "path": "/api/auth/register",
                                              "timestamp": "2026-10-02T00:00:00"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User already exists",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 409,
                                              "error": "Conflict",
                                              "message": "Email already exists",
                                              "code": "EMAIL_ALREADY_EXISTS",
                                              "path": "/api/auth/register",
                                              "timestamp": "2026-10-02T00:00:00Z"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<JwtResponse> register(@RequestBody(
            description = "Registration data",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = RegisterRequest.class)
            )
    ) RegisterRequest registerRequest, HttpServletResponse httpServletResponse);

    @Operation(
            summary = "User login",
            description = "Returns access token in response body. Sets refresh token in HttpOnly cookie.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login successful",
                            headers = {
                                    @Header(
                                            name = "Set-Cookie",
                                            description = "HttpOnly cookie containing refresh token"
                                    )
                            },
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = JwtResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                               "status": 400,
                                               "error": "Bad Request",
                                               "message": "email Email is required",
                                               "code": "VALIDATION_ERROR",
                                               "path": "/api/auth/login",
                                               "timestamp": "2026-10-02T00:00:00Z"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid login credentials",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "message": "Invalid email or password",
                                              "code": "INVALID_CREDENTIALS",
                                              "path": "/api/auth/login",
                                              "timestamp": "2026-10-02T00:00:00Z"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "Auth service internal error",
                                              "code": "AUTH_INTERNAL_ERROR",
                                              "path": "/api/auth/login",
                                              "timestamp": "2026-10-02T00:00:00Z"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<JwtResponse> login(
            @RequestBody(
                    description = "Login data",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginRequest.class)
                    )
            ) LoginRequest loginRequest, HttpServletResponse httpServletResponse
    );

    @Operation(
            summary = "Refresh tokens",
            description = "Returns access token in response body. Sets refresh token in HttpOnly cookie.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Refresh tokens successful",
                            headers = {
                                    @Header(
                                            name = "Set-Cookie",
                                            description = "HttpOnly cookie containing refresh token"
                                    )
                            },
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = JwtResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Refresh token invalid",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = {
                                            @ExampleObject(name = "Refresh token invalid", value = """
                                                    {
                                                      "status": 401,
                                                      "error": "Unauthorized",
                                                      "message": "Unauthorized",
                                                      "code": "REFRESH_TOKEN_INVALID",
                                                      "path": "/api/auth/refresh",
                                                      "timestamp": "2026-10-02T00:00:00Z"
                                                    }
                                                    """),
                                            @ExampleObject(name = "Refresh token expired", value = """
                                                    {
                                                      "status": 401,
                                                      "error": "Unauthorized",
                                                      "message": "Unauthorized",
                                                      "code": "REFRESH_TOKEN_EXPIRED",
                                                      "path": "/api/auth/refresh",
                                                      "timestamp": "2026-10-02T00:00:00Z"
                                                    }
                                                    """),
                                            @ExampleObject(name = "Refresh token missing", value = """
                                                    {
                                                      "status": 401,
                                                      "error": "Unauthorized",
                                                      "message": "Unauthorized",
                                                      "code": "REFRESH_TOKEN_MISSING",
                                                      "path": "/api/auth/refresh",
                                                      "timestamp": "2026-10-02T00:00:00Z"
                                                    }
                                                    """)
                                    }
                            )
                    )
            }
    )
    ResponseEntity<JwtResponse> refresh(
            HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest
    );


    @Operation(
            summary = "Logout user",
            description = "Return nothing. Delete refresh token from HttpOnly cookie.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Logout user successful",
                            headers = {
                                    @Header(
                                            name = "Set-Cookie",
                                            description = "HttpOnly cookie containing empty string"
                                    )
                            },
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = JwtResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Refresh token invalid",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "message": "Unauthorized",
                                              "code": "REFRESH_TOKEN_MISSING",
                                              "path": "/api/auth/logout",
                                              "timestamp": "2026-10-02T00:00:00Z"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<Void> logout(
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse
    );
}
