package korobkin.nikita.skill_service.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import korobkin.nikita.jwtsecuritystarter.security.jwt.JwtService;
import korobkin.nikita.skill_service.controller.SkillController;
import korobkin.nikita.skill_service.dto.request.BulkSkillRequest;
import korobkin.nikita.skill_service.dto.request.CreateSkillRequest;
import korobkin.nikita.skill_service.dto.request.SkillFilterRequest;
import korobkin.nikita.skill_service.dto.request.UpdateSkillRequest;
import korobkin.nikita.skill_service.dto.response.SkillResponse;
import korobkin.nikita.skill_service.entity.Skill;
import korobkin.nikita.skill_service.exception.ErrorCode;
import korobkin.nikita.skill_service.exception.SkillAlreadyExistsException;
import korobkin.nikita.skill_service.exception.SkillNotFoundException;
import korobkin.nikita.skill_service.fixtures.SkillFixtures;
import korobkin.nikita.skill_service.fixtures.SkillRequestFixtures;
import korobkin.nikita.skill_service.fixtures.SkillResponseFixtures;
import korobkin.nikita.skill_service.service.SkillService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SkillController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SkillControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SkillService skillService;

    @Nested
    @DisplayName("GET /api/skills - Get skills")
    class getSkills_Endpoint {

        @Test
        @DisplayName("Should return active skills successfully")
        void getSkills_shouldReturnActiveSkills() throws Exception {
            List<SkillResponse> skills = List.of(SkillResponseFixtures.skillResponse(SkillFixtures.activeJavaSkill()));

            given(skillService.findSkills(any(SkillFilterRequest.class), any(Pageable.class)))
                    .willReturn(SkillResponseFixtures.pagedResponse(
                            skills,
                            0,
                            10,
                            1,
                            1)
                    );

            mockMvc.perform(get("/api/skills"))
                    .andExpect(status().isOk())
                    .andExpect(pagination(0, 10, 1, 1))
                    .andExpect(jsonPath("$.content[0].name").value(SkillFixtures.NAME_JAVA_SKILL));

            verify(skillService).findSkills(any(SkillFilterRequest.class), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return active frameworks successfully")
        void getSkills_withFilters_shouldReturnFrameworks() throws Exception {
            List<SkillResponse> skills = List.of(
                    SkillResponseFixtures.skillResponse(SkillFixtures.activeSpringSkill())
            );

            given(skillService.findSkills(any(SkillFilterRequest.class), any(Pageable.class)))
                    .willReturn(SkillResponseFixtures.pagedResponse(
                            skills,
                            0,
                            10,
                            1,
                            1
                    ));

            mockMvc.perform(get("/api/skills")
                            .param("category", "FRAMEWORK"))
                    .andExpect(status().isOk())
                    .andExpect(pagination(0, 10, 1, 1))
                    .andExpect(jsonPath("$.content[0].name").value(SkillFixtures.NAME_SPRING_SKILL));

            verify(skillService).findSkills(any(SkillFilterRequest.class), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return skills by incomplete name successfully")
        void getSkills_withIncompleteName_shouldReturnSkills() throws Exception {
            List<SkillResponse> skills = List.of(
                    SkillResponseFixtures.skillResponse(SkillFixtures.activeJavaSkill()),
                    SkillResponseFixtures.skillResponse(SkillFixtures.activeJsSkill())
            );

            given(skillService.findSkills(any(SkillFilterRequest.class), any(Pageable.class)))
                    .willReturn(SkillResponseFixtures.pagedResponse(
                            skills,
                            0,
                            10,
                            2,
                            1
                    ));

            mockMvc.perform(get("/api/skills")
                            .param("search", "jav"))
                    .andExpect(status().isOk())
                    .andExpect(pagination(0, 10, 2, 1))
                    .andExpect(jsonPath("$.content[*].name",
                            containsInAnyOrder(SkillFixtures.NAME_JAVA_SKILL, SkillFixtures.NAME_JS_SKILL)));

            verify(skillService).findSkills(any(SkillFilterRequest.class), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return skills sorted by name ascending")
        void getSkills_withSortByNameAsc_shouldReturnSortedSkills() throws Exception {
            List<SkillResponse> skills = List.of(
                    SkillResponseFixtures.skillResponse(SkillFixtures.activeJavaSkill()),
                    SkillResponseFixtures.skillResponse(SkillFixtures.activeSpringSkill())
            );

            given(skillService.findSkills(any(SkillFilterRequest.class), any(Pageable.class)))
                    .willReturn(SkillResponseFixtures.pagedResponse(
                            skills,
                            0,
                            10,
                            2,
                            1
                    ));

            mockMvc.perform(get("/api/skills")
                            .param("sort", "name,asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].name").value(SkillFixtures.NAME_JAVA_SKILL))
                    .andExpect(jsonPath("$.content[1].name").value(SkillFixtures.NAME_SPRING_SKILL));

            verify(skillService).findSkills(any(SkillFilterRequest.class), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return response with paginated parameters")
        void getSkills_shouldReturnPaginatedResponse() throws Exception {
            List<SkillResponse> skills = new ArrayList<>();

            for (int i = 0; i < 15; i++) {
                skills.add(SkillResponseFixtures.skillResponse(
                        SkillFixtures.activeSkill("Skill " + i, SkillFixtures.LANGUAGE_CATEGORY))
                );
            }

            given(skillService.findSkills(any(SkillFilterRequest.class), any(Pageable.class)))
                    .willReturn(SkillResponseFixtures.pagedResponse(
                            skills,
                            0,
                            10,
                            15,
                            2
                    ));

            mockMvc.perform(get("/api/skills")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").hasJsonPath())
                    .andExpect(pagination(0, 10, 15, 2));

            verify(skillService).findSkills(any(SkillFilterRequest.class), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("GET /api/skills/{id} - Get skills by id")
    class getSkill_Endpoint {

        @Test
        @DisplayName("Should return active skill successfully")
        void getSkill_shouldReturnActiveSkill() throws Exception {
            Skill skill = SkillFixtures.activeJavaSkill();

            given(skillService.findSkill(any(UUID.class))).willReturn(SkillResponseFixtures.skillResponse(skill));

            mockMvc.perform(get("/api/skills/" + UUID.randomUUID()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(skill.getName()))
                    .andExpect(jsonPath("$.category").value(skill.getCategory().name()));

            verify(skillService).findSkill(any(UUID.class));
        }

        @Test
        @DisplayName("Should return 404 when skill not found")
        void getSkill_skillNotFound_shouldReturnNotFound() throws Exception {
            given(skillService.findSkill(any(UUID.class)))
                    .willThrow(new SkillNotFoundException(ErrorCode.SKILL_NOT_FOUND));

            mockMvc.perform(get("/api/skills/" + UUID.randomUUID()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(ErrorCode.SKILL_NOT_FOUND.message))
                    .andExpect(jsonPath("$.code").value(ErrorCode.SKILL_NOT_FOUND.name()));

            verify(skillService).findSkill(any(UUID.class));
        }
    }

    @Nested
    @DisplayName("POST /api/skills/by-ids - Get skills by id")
    class getBulkSkills_Endpoint {

        @Test
        @DisplayName("Should return active skills successfully")
        void getBulkSkills_shouldReturnActiveSkills() throws Exception {
            Skill javaSkill = SkillFixtures.activeJavaSkill();
            Skill springSkill = SkillFixtures.activeSpringSkill();

            List<String> skillIds = List.of(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString()
            );

            List<SkillResponse> skillResponses = List.of(
                    SkillResponseFixtures.skillResponse(javaSkill),
                    SkillResponseFixtures.skillResponse(springSkill)
            );

            given(skillService.findBulkSkills(any(BulkSkillRequest.class)))
                    .willReturn(skillResponses);

            mockMvc.perform(post("/api/skills/by-ids")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(SkillRequestFixtures.bulkSkillRequest(skillIds))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[*].name",
                            containsInAnyOrder(javaSkill.getName(), springSkill.getName())));

            verify(skillService).findBulkSkills(any(BulkSkillRequest.class));
        }

        @Test
        @DisplayName("Should return 400 when skill id is invalid")
        void getBulkSkills_withInvalidId_shouldReturnBadRequest() throws Exception {
            mockMvc.perform(post("/api/skills/by-ids")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(SkillRequestFixtures.bulkSkillRequest(List.of("123")))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        }
    }

    @Nested
    @DisplayName("POST /api/skills - Create skills")
    class createSkills_Endpoint {

        @Test
        @DisplayName("Should return created skill response successfully")
        void createSkill_shouldReturnCreatedSkill() throws Exception {
            Skill skill = SkillFixtures.activeJavaSkill();

            given(skillService.createSkill(any(CreateSkillRequest.class)))
                    .willReturn(SkillResponseFixtures.skillResponse(skill));

            mockMvc.perform(post("/api/skills")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(SkillRequestFixtures.createSkillRequest(skill))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value(skill.getName()))
                    .andExpect(jsonPath("$.category").value(skill.getCategory().name()));

            verify(skillService).createSkill(any(CreateSkillRequest.class));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  ", "\t"})
        @DisplayName("Should return 400 when create skills with blank name")
        void createSkill_withBlankName_shouldReturnBadRequest(String invalidName) throws Exception {
            mockMvc.perform(post("/api/skills")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(SkillRequestFixtures.createSkillRequest(
                                    SkillFixtures.activeSkill(invalidName, SkillFixtures.LANGUAGE_CATEGORY)
                            ))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        }

        @Test
        @DisplayName("Should return 409 when create skills with exists name")
        void createSkill_withExistsName_shouldReturnConflict() throws Exception {
            given(skillService.createSkill(any(CreateSkillRequest.class)))
                    .willThrow(new SkillAlreadyExistsException(ErrorCode.SKILL_ALREADY_EXISTS));

            mockMvc.perform(post("/api/skills")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(SkillRequestFixtures.createSkillRequest(SkillFixtures.activeJavaSkill()))))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value(ErrorCode.SKILL_ALREADY_EXISTS.message))
                    .andExpect(jsonPath("$.code").value(ErrorCode.SKILL_ALREADY_EXISTS.name()));

            verify(skillService).createSkill(any(CreateSkillRequest.class));
        }
    }

    @Nested
    @DisplayName("PUT /api/skills/{id} - Update skills")
    class updateSkills_Endpoint {

        @Test
        @DisplayName("Should return updated skill response successfully")
        void updateSkill_shouldReturnUpdatedSkill() throws Exception {
            Skill skill = SkillFixtures.activeJavaSkill();

            given(skillService.updateSkill(any(UUID.class), any(UpdateSkillRequest.class)))
                    .willReturn(SkillResponseFixtures.skillResponse(skill));

            mockMvc.perform(put("/api/skills/" + UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(SkillRequestFixtures.updateSkillRequest(SkillFixtures.activeJavaSkill()))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(skill.getName()))
                    .andExpect(jsonPath(("$.category")).value(skill.getCategory().name()));

            verify(skillService).updateSkill(any(UUID.class), any(UpdateSkillRequest.class));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  ", "\t"})
        @DisplayName("Should return 400 when update skills with blank name")
        void updateSkill_withBlankName_shouldReturnBadRequest(String invalidName) throws Exception {
            mockMvc.perform(put("/api/skills/" + UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(SkillRequestFixtures.updateSkillRequest(
                                    SkillFixtures.activeSkill(invalidName, SkillFixtures.LANGUAGE_CATEGORY)))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        }

        @Test
        @DisplayName("Should return 404 when update skills with invalid id")
        void updateSkill_withInvalidId_shouldReturnNotFound() throws Exception {
            given(skillService.updateSkill(any(UUID.class), any(UpdateSkillRequest.class)))
                    .willThrow(new SkillNotFoundException(ErrorCode.SKILL_NOT_FOUND));

            mockMvc.perform(put("/api/skills/" + UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(SkillRequestFixtures.updateSkillRequest(SkillFixtures.activeSpringSkill()))))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(ErrorCode.SKILL_NOT_FOUND.message))
                    .andExpect(jsonPath("$.code").value(ErrorCode.SKILL_NOT_FOUND.name()));

            verify(skillService).updateSkill(any(UUID.class), any(UpdateSkillRequest.class));
        }

        @Test
        @DisplayName("Should return 409 when update skills with exists name")
        void updateSkill_withExistsName_shouldReturnConflict() throws Exception {
            given(skillService.updateSkill(any(UUID.class), any(UpdateSkillRequest.class)))
                    .willThrow(new SkillAlreadyExistsException(ErrorCode.SKILL_ALREADY_EXISTS));

            mockMvc.perform(put("/api/skills/" + UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(SkillRequestFixtures.updateSkillRequest(SkillFixtures.activeJavaSkill()))))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value(ErrorCode.SKILL_ALREADY_EXISTS.message))
                    .andExpect(jsonPath("$.code").value(ErrorCode.SKILL_ALREADY_EXISTS.name()));

            verify(skillService).updateSkill(any(UUID.class), any(UpdateSkillRequest.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/skills/{id} - Delete skills")
    class deactivateSkills_Endpoint {

        @Test
        @DisplayName("Should return 204 successfully")
        void deactivateSkill_shouldReturnNoContent() throws Exception {
            doNothing().when(skillService).deactivateSkill(any(UUID.class));

            mockMvc.perform(delete("/api/skills/" + UUID.randomUUID()))
                    .andExpect(status().isNoContent());

            verify(skillService).deactivateSkill(any(UUID.class));
        }

        @Test
        @DisplayName("Should return 400 when delete skills with invalid id")
        void deactivateSkill_withInvalidId_shouldReturnNotFound() throws Exception {
            doThrow(new SkillNotFoundException(ErrorCode.SKILL_NOT_FOUND))
                    .when(skillService)
                    .deactivateSkill(any(UUID.class));

            mockMvc.perform(delete("/api/skills/" + UUID.randomUUID()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(ErrorCode.SKILL_NOT_FOUND.message))
                    .andExpect(jsonPath("$.code").value(ErrorCode.SKILL_NOT_FOUND.name()));

            verify(skillService).deactivateSkill(any(UUID.class));
        }
    }

    public static ResultMatcher pagination(int pageNumber, int pageSize, long totalElements, int totalPages) {
        return result -> {
            jsonPath("$.content").isArray().match(result);
            jsonPath("$.pageNumber").value(pageNumber).match(result);
            jsonPath("$.pageSize").value(pageSize).match(result);
            jsonPath("$.totalElements").value(totalElements).match(result);
            jsonPath("$.totalPages").value(totalPages).match(result);
        };
    }

    private String json(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }
}
