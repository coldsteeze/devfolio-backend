package korobkin.nikita.skill_service.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import korobkin.nikita.skill_service.entity.Skill;
import korobkin.nikita.skill_service.exception.ErrorCode;
import korobkin.nikita.skill_service.fixtures.RoleFixtures;
import korobkin.nikita.skill_service.fixtures.SkillFixtures;
import korobkin.nikita.skill_service.fixtures.SkillRequestFixtures;
import korobkin.nikita.skill_service.repository.SkillRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SkillControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SkillRepository skillRepository;

    @Test
    void getSkills_shouldReturnActiveSkills() throws Exception {
        skillRepository.save(SkillFixtures.activeJavaSkill());
        skillRepository.save(SkillFixtures.inactivePascalSkill());

        mockMvc.perform(get("/api/skills")
                        .with(auth(RoleFixtures.ROLE_USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].name").value(SkillFixtures.NAME_JAVA_SKILL));
    }

    @Test
    void getSkills_shouldReturnAllSkills() throws Exception {
        skillRepository.save(SkillFixtures.activeJavaSkill());
        skillRepository.save(SkillFixtures.inactivePascalSkill());

        mockMvc.perform(get("/api/skills")
                        .with(auth(RoleFixtures.ROLE_ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].name",
                        containsInAnyOrder(SkillFixtures.NAME_JAVA_SKILL, SkillFixtures.NAME_INACTIVE_SKILL)));
    }

    @Test
    void getSkills_withFilters_shouldReturnFrameworks() throws Exception {
        skillRepository.save(SkillFixtures.activeJavaSkill());
        skillRepository.save(SkillFixtures.inactivePascalSkill());
        skillRepository.save(SkillFixtures.activeSpringSkill());

        mockMvc.perform(get("/api/skills")
                        .param("category", "FRAMEWORK")
                        .with(auth(RoleFixtures.ROLE_USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].name").value(SkillFixtures.NAME_SPRING_SKILL))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    void getSkills_withIncompleteName_shouldReturnSkills() throws Exception {
        skillRepository.save(SkillFixtures.activeJavaSkill());
        skillRepository.save(SkillFixtures.activeJsSkill());

        mockMvc.perform(get("/api/skills")
                        .param("search", "jav")
                        .with(auth(RoleFixtures.ROLE_USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].name",
                        containsInAnyOrder(SkillFixtures.NAME_JAVA_SKILL, SkillFixtures.NAME_JS_SKILL)))
                .andExpect(jsonPath("$.totalElements", is(2)));
    }

    @Test
    void getSkills_withSortByNameAsc_shouldReturnSortedSkills() throws Exception {
        skillRepository.save(SkillFixtures.activeSpringSkill());
        skillRepository.save(SkillFixtures.activeJavaSkill());

        mockMvc.perform(get("/api/skills")
                        .param("sort", "name,asc")
                        .with(auth(RoleFixtures.ROLE_USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value(SkillFixtures.NAME_JAVA_SKILL))
                .andExpect(jsonPath("$.content[1].name").value(SkillFixtures.NAME_SPRING_SKILL));
    }

    @Test
    void getSkills_shouldReturnPaginatedResponse() throws Exception {
        for (int i = 0; i < 15; i++) {
            skillRepository.save(SkillFixtures.activeSkill("Skill " + i, SkillFixtures.LANGUAGE_CATEGORY));
        }

        mockMvc.perform(get("/api/skills")
                        .param("page", "0")
                        .param("size", "10")
                        .with(auth(RoleFixtures.ROLE_USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").hasJsonPath())
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void getSkill_shouldReturnActiveSkill() throws Exception {
        Skill skill = SkillFixtures.activeJavaSkill();
        skillRepository.save(skill);

        mockMvc.perform(get("/api/skills/" + skill.getId())
                        .param("search", "jav")
                        .with(auth(RoleFixtures.ROLE_USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(SkillFixtures.NAME_JAVA_SKILL))
                .andExpect(jsonPath("$.category").value(SkillFixtures.LANGUAGE_CATEGORY.name()));
    }

    @Test
    void getSkill_inactiveSkill_shouldReturnNotFound() throws Exception {
        Skill skill = SkillFixtures.inactivePascalSkill();
        skillRepository.save(skill);

        mockMvc.perform(get("/api/skills/" + skill.getId())
                        .with(auth(RoleFixtures.ROLE_USER)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ErrorCode.SKILL_NOT_FOUND.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.SKILL_NOT_FOUND.name()));
    }

    @Test
    void getSkill_shouldReturnInactiveSkill() throws Exception {
        Skill skill = SkillFixtures.inactivePascalSkill();
        skillRepository.save(skill);

        mockMvc.perform(get("/api/skills/" + skill.getId())
                        .with(auth(RoleFixtures.ROLE_ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(SkillFixtures.NAME_INACTIVE_SKILL))
                .andExpect(jsonPath("$.category").value(SkillFixtures.LANGUAGE_CATEGORY.name()));
    }

    @Test
    void getSkill_withInvalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/skills/" + UUID.randomUUID())
                        .with(auth(RoleFixtures.ROLE_USER)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ErrorCode.SKILL_NOT_FOUND.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.SKILL_NOT_FOUND.name()));
    }

    @Test
    void getBulkSkills_shouldReturnActiveSkills() throws Exception {
        Skill javaSkill = SkillFixtures.activeJavaSkill();
        Skill springSkill = SkillFixtures.activeSpringSkill();
        Skill pascalSkill = SkillFixtures.inactivePascalSkill();
        skillRepository.save(javaSkill);
        skillRepository.save(springSkill);
        skillRepository.save(pascalSkill);

        List<String> skillIds = List.of(
                javaSkill.getId().toString(),
                springSkill.getId().toString(),
                pascalSkill.getId().toString()
        );

        mockMvc.perform(post("/api/skills/by-ids")
                        .with(auth(RoleFixtures.ROLE_USER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(SkillRequestFixtures.bulkSkillRequest(skillIds))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name",
                        containsInAnyOrder(SkillFixtures.NAME_JAVA_SKILL, SkillFixtures.NAME_SPRING_SKILL)))
                .andExpect(jsonPath("$[*].name").value(not(hasItem(SkillFixtures.NAME_INACTIVE_SKILL))));
    }

    @Test
    void getBulkSkills_shouldReturnAllSkills() throws Exception {
        Skill javaSkill = SkillFixtures.activeJavaSkill();
        Skill pascalSkill = SkillFixtures.inactivePascalSkill();
        skillRepository.save(javaSkill);
        skillRepository.save(pascalSkill);

        List<String> skillIds = List.of(javaSkill.getId().toString(), pascalSkill.getId().toString());

        mockMvc.perform(post("/api/skills/by-ids")
                        .with(auth(RoleFixtures.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(SkillRequestFixtures.bulkSkillRequest(skillIds))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name",
                        containsInAnyOrder(SkillFixtures.NAME_JAVA_SKILL, SkillFixtures.NAME_INACTIVE_SKILL)));
    }

    @Test
    void getBulkSkills_withInvalidId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/skills/by-ids")
                        .with(auth(RoleFixtures.ROLE_USER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(SkillRequestFixtures.bulkSkillRequest(List.of("123")))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void createSkill_shouldReturnCreatedSkill() throws Exception {
        Skill skill = SkillFixtures.activeJavaSkill();

        mockMvc.perform(post("/api/skills")
                        .with(auth(RoleFixtures.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(SkillRequestFixtures.createSkillRequest(skill))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(skill.getName()))
                .andExpect(jsonPath("$.category").value(skill.getCategory().name()));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t"})
    void createSkill_withBlankName_shouldReturnBadRequest(String invalidName) throws Exception {
        mockMvc.perform(post("/api/skills")
                        .with(auth(RoleFixtures.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(SkillRequestFixtures.createSkillRequest(
                                SkillFixtures.activeSkill(invalidName, SkillFixtures.LANGUAGE_CATEGORY)
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void createSkill_withInvalidRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/skills")
                        .with(auth(RoleFixtures.ROLE_USER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(SkillRequestFixtures.createSkillRequest(SkillFixtures.activeJavaSkill()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void createSkill_withExistsName_shouldReturnConflict() throws Exception {
        skillRepository.save(SkillFixtures.activeJavaSkill());

        mockMvc.perform(post("/api/skills")
                .with(auth(RoleFixtures.ROLE_ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(SkillRequestFixtures.createSkillRequest(SkillFixtures.activeJavaSkill()))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(ErrorCode.SKILL_ALREADY_EXISTS.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.SKILL_ALREADY_EXISTS.name()));
    }

    @Test
    void updateSkill_shouldReturnUpdatedSkill() throws Exception {
        Skill skill = SkillFixtures.activeJavaSkill();
        skillRepository.save(skill);

        mockMvc.perform(put("/api/skills/" + skill.getId())
                        .with(auth(RoleFixtures.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(SkillRequestFixtures.updateSkillRequest(SkillFixtures.activeSpringSkill()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(skill.getId().toString()))
                .andExpect(jsonPath("$.name").value(SkillFixtures.NAME_SPRING_SKILL))
                .andExpect(jsonPath(("$.category")).value(SkillFixtures.FRAMEWORK_CATEGORY.name()));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t"})
    void updateSkill_withBlankName_shouldReturnBadRequest(String invalidName) throws Exception {
        Skill skill = SkillFixtures.activeJavaSkill();
        skillRepository.save(skill);

        mockMvc.perform(put("/api/skills/" + skill.getId())
                        .with(auth(RoleFixtures.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(SkillRequestFixtures.updateSkillRequest(
                                SkillFixtures.activeSkill(invalidName, SkillFixtures.LANGUAGE_CATEGORY)))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void updateSkill_withInvalidRole_shouldReturnForbidden() throws Exception {
        Skill skill = SkillFixtures.activeJavaSkill();
        skillRepository.save(skill);

        mockMvc.perform(put("/api/skills/" + skill.getId())
                        .with(auth(RoleFixtures.ROLE_USER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(SkillRequestFixtures.updateSkillRequest(SkillFixtures.activeSpringSkill()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateSkill_withInvalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(put("/api/skills/" + UUID.randomUUID())
                        .with(auth(RoleFixtures.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(SkillRequestFixtures.updateSkillRequest(SkillFixtures.activeSpringSkill()))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ErrorCode.SKILL_NOT_FOUND.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.SKILL_NOT_FOUND.name()));
    }

    @Test
    void updateSkill_withExistsName_shouldReturnConflict() throws Exception {
        Skill skill = SkillFixtures.activeJavaSkill();
        skillRepository.save(skill);

        mockMvc.perform(put("/api/skills/" + skill.getId())
                        .with(auth(RoleFixtures.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(SkillRequestFixtures.updateSkillRequest(SkillFixtures.activeJavaSkill()))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(ErrorCode.SKILL_ALREADY_EXISTS.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.SKILL_ALREADY_EXISTS.name()));
    }

    @Test
    void deactivateSkill_shouldReturnNoContent() throws Exception {
        Skill skill = SkillFixtures.activeJavaSkill();
        skillRepository.save(skill);

        mockMvc.perform(delete("/api/skills/" + skill.getId())
                .with(auth(RoleFixtures.ROLE_ADMIN)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deactivateSkill_withInvalidRole_shouldReturnForbidden() throws Exception {
        Skill skill = SkillFixtures.activeJavaSkill();
        skillRepository.save(skill);

        mockMvc.perform(delete("/api/skills/" + skill.getId())
                        .with(auth(RoleFixtures.ROLE_USER)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deactivateSkill_withInvalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/skills/" + UUID.randomUUID())
                        .with(auth(RoleFixtures.ROLE_ADMIN)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ErrorCode.SKILL_NOT_FOUND.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.SKILL_NOT_FOUND.name()));
    }

    private String json(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    private RequestPostProcessor auth(String role) {
        return authentication(new UsernamePasswordAuthenticationToken(
                null,
                null,
                Collections.singletonList(new SimpleGrantedAuthority(role))));
    }
}
