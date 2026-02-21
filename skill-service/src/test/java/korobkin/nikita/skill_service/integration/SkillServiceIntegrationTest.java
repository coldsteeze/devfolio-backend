package korobkin.nikita.skill_service.integration;

import korobkin.nikita.skill_service.dto.request.SkillFilterRequest;
import korobkin.nikita.skill_service.dto.request.UpdateSkillRequest;
import korobkin.nikita.skill_service.dto.response.PagedResponse;
import korobkin.nikita.skill_service.dto.response.SkillResponse;
import korobkin.nikita.skill_service.entity.Skill;
import korobkin.nikita.skill_service.exception.ErrorCode;
import korobkin.nikita.skill_service.exception.SkillAlreadyExistsException;
import korobkin.nikita.skill_service.exception.SkillNotFoundException;
import korobkin.nikita.skill_service.fixtures.SkillFixtures;
import korobkin.nikita.skill_service.fixtures.SkillRequestFixtures;
import korobkin.nikita.skill_service.repository.SkillRepository;
import korobkin.nikita.skill_service.security.SecurityUtils;
import korobkin.nikita.skill_service.service.SkillService;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;

public class SkillServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private SkillService skillService;

    @Autowired
    private SkillRepository skillRepository;

    @Test
    void findSkills_shouldReturnActiveSkills() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(() -> SecurityUtils.hasRole("ADMIN")).thenReturn(false);
            skillRepository.save(SkillFixtures.activeJavaSkill());
            skillRepository.save(SkillFixtures.activeSpringSkill());

            PagedResponse<SkillResponse> skills = skillService.findSkills(
                    new SkillFilterRequest(),
                    PageRequest.of(0, 10)
            );

            assertThat(skills.content())
                    .extracting(SkillResponse::name)
                    .containsExactly(SkillFixtures.NAME_JAVA_SKILL, SkillFixtures.NAME_SPRING_SKILL);
        }
    }

    @Test
    void findSkills_shouldReturnAllSkills() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(() -> SecurityUtils.hasRole("ADMIN")).thenReturn(true);

            skillRepository.save(SkillFixtures.activeJavaSkill());
            skillRepository.save(SkillFixtures.activeSpringSkill());
            skillRepository.save(SkillFixtures.inactivePascalSkill());

            PagedResponse<SkillResponse> skills = skillService.findSkills(
                    new SkillFilterRequest(),
                    PageRequest.of(0, 10)
            );

            assertThat(skills.content())
                    .extracting(SkillResponse::name)
                    .containsExactly(
                            SkillFixtures.NAME_JAVA_SKILL,
                            SkillFixtures.NAME_SPRING_SKILL,
                            SkillFixtures.NAME_INACTIVE_SKILL
                    );
        }
    }

    @Test
    void findSkills_withFilters_shouldReturnFrameworks() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(() -> SecurityUtils.hasRole("ADMIN")).thenReturn(false);

            skillRepository.save(SkillFixtures.activeJavaSkill());
            skillRepository.save(SkillFixtures.activeSpringSkill());
            skillRepository.save(SkillFixtures.inactivePascalSkill());


            PagedResponse<SkillResponse> skills = skillService.findSkills(
                    SkillRequestFixtures.skillFilterRequest(null, SkillFixtures.FRAMEWORK_CATEGORY),
                    PageRequest.of(0, 10)
            );

            assertThat(skills.content())
                    .extracting(SkillResponse::name)
                    .containsExactly(
                            SkillFixtures.NAME_SPRING_SKILL
                    );
        }
    }

    @Test
    void findSkills_withIncompleteName_shouldReturnSkills() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(() -> SecurityUtils.hasRole("ADMIN")).thenReturn(false);

            skillRepository.save(SkillFixtures.activeJavaSkill());
            skillRepository.save(SkillFixtures.activeJsSkill());

            PagedResponse<SkillResponse> skills = skillService.findSkills(
                    SkillRequestFixtures.skillFilterRequest("jav", null),
                    PageRequest.of(0, 10)
            );

            assertThat(skills.content())
                    .extracting(SkillResponse::name)
                    .containsExactly(
                            SkillFixtures.NAME_JAVA_SKILL,
                            SkillFixtures.NAME_JS_SKILL
                    );
        }
    }

    @Test
    void findSkills_withSortByNameAsc_shouldReturnSortedSkills() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(() -> SecurityUtils.hasRole("ADMIN")).thenReturn(false);

            skillRepository.save(SkillFixtures.activeSpringSkill());
            skillRepository.save(SkillFixtures.activeJavaSkill());

            PagedResponse<SkillResponse> skills = skillService.findSkills(
                    new SkillFilterRequest(),
                    PageRequest.of(0, 10, Sort.by("name").ascending())
            );

            assertThat(skills.content())
                    .extracting(SkillResponse::name)
                    .containsExactly(
                            SkillFixtures.NAME_JAVA_SKILL,
                            SkillFixtures.NAME_SPRING_SKILL
                    );
        }
    }

    @Test
    void findSkills_withPageable_shouldReturnCorrectPage() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.hasRole("ADMIN")).thenReturn(false);

            for (int i = 0; i < 15; i++) {
                skillRepository.save(SkillFixtures.activeSkill("Skill " + i, SkillFixtures.LANGUAGE_CATEGORY));
            }

            PagedResponse<SkillResponse> skills = skillService.findSkills(
                    new SkillFilterRequest(), PageRequest.of(0, 10)
            );

            assertThat(skills.content()).hasSize(10);
            assertThat(skills.totalElements()).isEqualTo(15);
            assertThat(skills.pageNumber()).isEqualTo(0);
            assertThat(skills.totalPages()).isEqualTo(2);
        }
    }

    @Test
    void createSkill_shouldReturnCreatedSkill() {
        Skill skill = SkillFixtures.activeJavaSkill();

        SkillResponse skillResponse = skillService.createSkill(
                SkillRequestFixtures.createSkillRequest(skill)
        );

        assertThat(skillResponse.name()).isEqualTo(skill.getName());
        assertThat(skillResponse.category()).isEqualTo(skill.getCategory());
    }

    @Test
    void createSkill_withExistsName_shouldReturnConflict() {
        Skill skill = SkillFixtures.activeJavaSkill();
        skillRepository.save(skill);

        assertThatThrownBy(() -> skillService.createSkill(SkillRequestFixtures.createSkillRequest(skill)))
                .isInstanceOf(SkillAlreadyExistsException.class)
                .hasMessageContaining(ErrorCode.SKILL_ALREADY_EXISTS.message);
    }

    @Test
    void findSkill_shouldReturnActiveSkill() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.hasRole("ADMIN")).thenReturn(false);

            Skill skill = SkillFixtures.activeJavaSkill();
            skillRepository.save(skill);

            SkillResponse skillResponse = skillService.findSkill(skill.getId());

            assertThat(skillResponse.id()).isEqualTo(skill.getId());
            assertThat(skillResponse.name()).isEqualTo(skill.getName());
            assertThat(skillResponse.category()).isEqualTo(skill.getCategory());
        }
    }

    @Test
    void findSkill_shouldReturnInactiveSkill() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.hasRole("ADMIN")).thenReturn(true);

            Skill skill = SkillFixtures.inactivePascalSkill();
            skillRepository.save(skill);

            SkillResponse skillResponse = skillService.findSkill(skill.getId());

            assertThat(skillResponse.id()).isEqualTo(skill.getId());
            assertThat(skillResponse.name()).isEqualTo(skill.getName());
            assertThat(skillResponse.category()).isEqualTo(skill.getCategory());
        }
    }

    @Test
    void findSkill_inactiveSkill_shouldReturnNotFound() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.hasRole("ADMIN")).thenReturn(false);

            Skill skill = SkillFixtures.inactivePascalSkill();
            skillRepository.save(skill);

            assertThatThrownBy(() -> skillService.findSkill(skill.getId()))
                    .isInstanceOf(SkillNotFoundException.class)
                    .hasMessageContaining(ErrorCode.SKILL_NOT_FOUND.message);
        }
    }

    @Test
    void findSkill_withInvalidId_shouldReturnNotFound() {
        assertThatThrownBy(() -> skillService.findSkill(UUID.randomUUID()))
                .isInstanceOf(SkillNotFoundException.class)
                .hasMessageContaining(ErrorCode.SKILL_NOT_FOUND.message);
    }

    @Test
    void findBulkSkills_shouldReturnActiveSkills() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.hasRole("ADMIN")).thenReturn(false);

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


            List<SkillResponse> skillResponses = skillService.findBulkSkills(
                    SkillRequestFixtures.bulkSkillRequest(skillIds)
            );

            assertThat(skillResponses)
                    .hasSize(2)
                    .extracting(SkillResponse::name)
                    .containsExactlyInAnyOrder(javaSkill.getName(), springSkill.getName());
        }
    }

    @Test
    void findBulkSkills_shouldReturnAllSkills() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.hasRole("ADMIN")).thenReturn(true);

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


            List<SkillResponse> skillResponses = skillService.findBulkSkills(
                    SkillRequestFixtures.bulkSkillRequest(skillIds)
            );

            assertThat(skillResponses)
                    .hasSize(3)
                    .extracting(SkillResponse::name)
                    .containsExactlyInAnyOrder(javaSkill.getName(), springSkill.getName(), pascalSkill.getName());
        }
    }

    @Test
    void updateSkill_shouldReturnUpdatedSkill() {
        Skill javaSkill = SkillFixtures.activeJavaSkill();
        skillRepository.save(javaSkill);

        SkillResponse skillResponse = skillService.updateSkill(
                javaSkill.getId(),
                SkillRequestFixtures.updateSkillRequest(SkillFixtures.activeJsSkill())
        );

        assertThat(skillResponse.id()).isEqualTo(javaSkill.getId());
        assertThat(skillResponse.name()).isEqualTo(SkillFixtures.NAME_JS_SKILL);
        assertThat(skillResponse.category()).isEqualTo(SkillFixtures.LANGUAGE_CATEGORY);
    }

    @Test
    void updateSkill_withInvalidId_shouldReturnNotFound() {
        UpdateSkillRequest updateSkillRequest = SkillRequestFixtures.updateSkillRequest(
                SkillFixtures.activeJsSkill()
        );

        assertThatThrownBy(() -> skillService.updateSkill(UUID.randomUUID(), updateSkillRequest))
                .isInstanceOf(SkillNotFoundException.class)
                .hasMessageContaining(ErrorCode.SKILL_NOT_FOUND.message);
    }

    @Test
    void updateSkill_withExistsName_shouldReturnConflict() {
        Skill javaSkill = SkillFixtures.activeJavaSkill();
        skillRepository.save(javaSkill);

        UpdateSkillRequest updateSkillRequest = SkillRequestFixtures.updateSkillRequest(
                SkillFixtures.activeJavaSkill()
        );

        assertThatThrownBy(() -> skillService.updateSkill(javaSkill.getId(), updateSkillRequest))
                .isInstanceOf(SkillAlreadyExistsException.class)
                .hasMessageContaining(ErrorCode.SKILL_ALREADY_EXISTS.message);

    }

    @Test
    void deactivateSkill_shouldUpdateDatabase() {
        Skill javaSkill = SkillFixtures.activeJavaSkill();
        skillRepository.save(javaSkill);

        skillService.deactivateSkill(javaSkill.getId());

        Skill updatedSkill = skillRepository.findById(javaSkill.getId()).orElseThrow();
        assertThat(updatedSkill.isActive()).isFalse();
    }

    @Test
    void deactivateSkill_withInvalidId_shouldReturnNotFound() {
        assertThatThrownBy(() -> skillService.deactivateSkill(UUID.randomUUID()))
                .isInstanceOf(SkillNotFoundException.class)
                .hasMessageContaining(ErrorCode.SKILL_NOT_FOUND.message);
    }
}