package korobkin.nikita.skill_service.unit;

import korobkin.nikita.skill_service.dto.request.CreateSkillRequest;
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
import korobkin.nikita.skill_service.fixtures.SkillResponseFixtures;
import korobkin.nikita.skill_service.mapper.SkillMapper;
import korobkin.nikita.skill_service.repository.SkillRepository;
import korobkin.nikita.skill_service.security.SecurityUtils;
import korobkin.nikita.skill_service.service.impl.SkillServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SkillService unit tests")
public class SkillServiceUnitTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;

    @InjectMocks
    private SkillServiceImpl skillService;

    @BeforeEach
    void setUpSecurity() {
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Nested
    @DisplayName("Find skills")
    class FindSkills {

        @Test
        @DisplayName("Should return active skills without filters")
        void findSkills_shouldReturnActiveSkills() {
            Skill javaSkill = SkillFixtures.activeJavaSkill();
            Skill jsSkill = SkillFixtures.activeJsSkill();
            List<Skill> skills = List.of(javaSkill, jsSkill);

            given(skillRepository.findAllByFilters(any(SkillFilterRequest.class), any(Pageable.class)))
                    .willReturn(new PageImpl<>(skills, PageRequest.of(0, 10), skills.size()));
            given(skillMapper.toDto(javaSkill)).willReturn(SkillResponseFixtures.skillResponse(javaSkill));
            given(skillMapper.toDto(jsSkill)).willReturn(SkillResponseFixtures.skillResponse(jsSkill));

            PagedResponse<SkillResponse> result = skillService.findSkills(new SkillFilterRequest(), PageRequest.of(0, 10));

            assertThat(result.content().get(0).name()).isEqualTo(javaSkill.getName());
            assertThat(result.content().get(1).name()).isEqualTo(jsSkill.getName());
            assertThat(result.totalElements()).isEqualTo(skills.size());
        }

        @Test
        @DisplayName("Should return empty page when no skills match filters")
        void findSkills_noMatches_shouldReturnEmpty() {
            given(skillRepository.findAllByFilters(any(), any()))
                    .willReturn(Page.empty());

            var result = skillService.findSkills(new SkillFilterRequest(), PageRequest.of(0, 10));

            assertThat(result.content().isEmpty()).isEqualTo(true);
            assertThat(result.totalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("Create skill")
    class CreateSkill {

        @Test
        @DisplayName("Should return created skill when skill created successfully")
        void createSkill_shouldReturnCreatedSkill() {
            Skill skill = SkillFixtures.activeJavaSkill();

            given(skillMapper.toEntity(any(CreateSkillRequest.class))).willReturn(skill);
            given(skillRepository.existsByName(skill.getName())).willReturn(false);
            given(skillMapper.toDto(skill)).willReturn(SkillResponseFixtures.skillResponse(skill));

            SkillResponse skillResponse = skillService.createSkill(SkillRequestFixtures.createSkillRequest(skill));

            ArgumentCaptor<Skill> captor = ArgumentCaptor.forClass(Skill.class);
            verify(skillMapper).toEntity(any(CreateSkillRequest.class));
            verify(skillRepository).existsByName(any(String.class));
            verify(skillRepository).save(captor.capture());
            verify(skillMapper).toDto(any(Skill.class));

            assertThat(skillResponse.name()).isEqualTo(skill.getName());
            assertThat(skillResponse.category()).isEqualTo(skill.getCategory());
        }

        @Test
        @DisplayName("Should throw exception when create skill with exists name")
        void createSkill_withExistsName_shouldThrowException() {
            Skill skill = SkillFixtures.activeJavaSkill();

            given(skillMapper.toEntity(any(CreateSkillRequest.class))).willReturn(skill);
            given(skillRepository.existsByName(skill.getName())).willReturn(true);

            assertThatThrownBy(() -> skillService.createSkill(SkillRequestFixtures.createSkillRequest(skill)))
                    .isInstanceOf(SkillAlreadyExistsException.class)
                    .hasMessageContaining(ErrorCode.SKILL_ALREADY_EXISTS.message);
        }
    }

    @Nested
    @DisplayName("Find skill")
    class FindSkill {

        @Test
        @DisplayName("Should return active skill")
        void findSkill_shouldReturnActiveSkill() {
            Skill skill = SkillFixtures.activeJavaSkill();

            given(skillRepository.findById(any(UUID.class))).willReturn(Optional.of(skill));
            given(skillMapper.toDto(any(Skill.class))).willReturn(SkillResponseFixtures.skillResponse(skill));

            SkillResponse skillResponse = skillService.findSkill(UUID.randomUUID());

            verify(skillRepository).findById(any(UUID.class));
            verify(skillMapper).toDto(any(Skill.class));

            assertThat(skillResponse.name()).isEqualTo(skill.getName());
            assertThat(skillResponse.category()).isEqualTo(skill.getCategory());
        }

        @Test
        @DisplayName("Should throw when non-admin finds inactive skill")
        void findSkill_inactiveSkillNonAdmin_shouldThrow() {
            try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
                mocked.when(() -> SecurityUtils.hasRole("ADMIN")).thenReturn(false);
                Skill inactive = SkillFixtures.inactivePascalSkill();
                given(skillRepository.findById(any())).willReturn(Optional.of(inactive));

                assertThatThrownBy(() -> skillService.findSkill(UUID.randomUUID()))
                        .isInstanceOf(SkillNotFoundException.class);
            }
        }

        @Test
        @DisplayName("Should throw exception when find skill with invalid id")
        void findSkill_withInvalidId_shouldThrowException() {
            given(skillRepository.findById(any(UUID.class))).willReturn(Optional.empty());

            assertThatThrownBy(() -> skillService.findSkill(UUID.randomUUID()))
                    .isInstanceOf(SkillNotFoundException.class)
                    .hasMessageContaining(ErrorCode.SKILL_NOT_FOUND.message);
        }
    }

    @Nested
    @DisplayName("Find bulk skills")
    class FindBulkSkills {

        @Test
        @DisplayName("Should return active bulk skills")
        void findBulkSkills_shouldReturnActiveSkills() {
            try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(() -> SecurityUtils.hasRole("ADMIN")).thenReturn(false);
                Skill javaSkill = SkillFixtures.activeJavaSkill();
                javaSkill.setId(UUID.randomUUID());
                Skill jsSkill = SkillFixtures.activeJsSkill();
                jsSkill.setId(UUID.randomUUID());
                List<String> skillIds = List.of(javaSkill.getId().toString(), jsSkill.getId().toString());
                List<Skill> skills = List.of(javaSkill, jsSkill);

                given(skillRepository.findAllByIdInAndActiveTrue(List.of(javaSkill.getId(), jsSkill.getId())))
                        .willReturn(skills);
                given(skillMapper.toDto(javaSkill)).willReturn(SkillResponseFixtures.skillResponse(javaSkill));
                given(skillMapper.toDto(jsSkill)).willReturn(SkillResponseFixtures.skillResponse(jsSkill));

                List<SkillResponse> skillResponseList = skillService.findBulkSkills(SkillRequestFixtures.bulkSkillRequest(skillIds));

                verify(skillRepository).findAllByIdInAndActiveTrue(anyList());
                verify(skillMapper).toDto(javaSkill);
                verify(skillMapper).toDto(jsSkill);

                assertThat(skillResponseList.get(0).name()).isEqualTo(javaSkill.getName());
                assertThat(skillResponseList.get(1).name()).isEqualTo(jsSkill.getName());
            }
        }

        @Test
        @DisplayName("Should return all bulk skills")
        void findBulkSkills_shouldReturnAllSkills() {
            try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(() -> SecurityUtils.hasRole("ADMIN")).thenReturn(true);

                Skill javaSkill = SkillFixtures.activeJavaSkill();
                javaSkill.setId(UUID.randomUUID());
                Skill pascalSkill = SkillFixtures.inactivePascalSkill();
                pascalSkill.setId(UUID.randomUUID());

                List<String> skillIds = List.of(javaSkill.getId().toString(), pascalSkill.getId().toString());
                List<Skill> skills = List.of(javaSkill, pascalSkill);

                given(skillRepository.findAllById(List.of(javaSkill.getId(), pascalSkill.getId())))
                        .willReturn(skills);
                given(skillMapper.toDto(javaSkill)).willReturn(SkillResponseFixtures.skillResponse(javaSkill));
                given(skillMapper.toDto(pascalSkill)).willReturn(SkillResponseFixtures.skillResponse(pascalSkill));

                List<SkillResponse> skillResponseList = skillService.findBulkSkills(SkillRequestFixtures.bulkSkillRequest(skillIds));

                verify(skillRepository).findAllById(anyList());
                verify(skillMapper).toDto(javaSkill);
                verify(skillMapper).toDto(pascalSkill);

                assertThat(skillResponseList.get(0).name()).isEqualTo(javaSkill.getName());
                assertThat(skillResponseList.get(1).name()).isEqualTo(pascalSkill.getName());
            }
        }
    }

    @Nested
    @DisplayName("Update skill")
    class UpdateSkill {

        @Test
        @DisplayName("Should return updated skill when update skill is successfully")
        void updateSkill_shouldReturnUpdatedSkill() {
            UUID skillId = UUID.randomUUID();
            Skill skill = SkillFixtures.activeJavaSkill();
            skill.setId(skillId);

            UpdateSkillRequest request = SkillRequestFixtures.updateSkillRequest(SkillFixtures.activeJsSkill());

            given(skillRepository.findById(skillId)).willReturn(Optional.of(skill));
            given(skillRepository.existsByName(any(String.class))).willReturn(false);

            doAnswer(invocation -> {
                UpdateSkillRequest dto = invocation.getArgument(0);
                Skill entity = invocation.getArgument(1);

                entity.setName(dto.getName());
                entity.setCategory(dto.getCategory());

                return null;
            }).when(skillMapper).updateEntityFromDto(
                    any(UpdateSkillRequest.class),
                    any(Skill.class)
            );

            given(skillMapper.toDto(skill))
                    .willAnswer(invocation -> {
                        Skill updatedSkill = invocation.getArgument(0);
                        return new SkillResponse(
                                updatedSkill.getId(),
                                updatedSkill.getName(),
                                updatedSkill.getCategory()
                        );
                    });


            SkillResponse result = skillService.updateSkill(skillId, request);

            verify(skillRepository).findById(skillId);
            verify(skillRepository).existsByName(request.getName());
            verify(skillMapper).updateEntityFromDto(eq(request), eq(skill));
            verify(skillMapper).toDto(skill);

            assertThat(result.name()).isEqualTo(request.getName());
            assertThat(result.category()).isEqualTo(request.getCategory());
            assertThat(skill.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception when update skill with invalid id")
        void updateSkill_withInvalidId_shouldThrowException() {
            given(skillRepository.findById(any(UUID.class))).willReturn(Optional.empty());

            assertThatThrownBy(() -> skillService.updateSkill(
                    UUID.randomUUID(),
                    SkillRequestFixtures.updateSkillRequest(SkillFixtures.activeJsSkill()))
            )
                    .isInstanceOf(SkillNotFoundException.class)
                    .hasMessageContaining(ErrorCode.SKILL_NOT_FOUND.message);
        }

        @Test
        @DisplayName("Should throw exception when update skill with exists name")
        void updateSkill_withExistsName_shouldThrowException() {
            Skill skill = SkillFixtures.activeJavaSkill();
            given(skillRepository.findById(any(UUID.class))).willReturn(Optional.of(skill));
            given(skillRepository.existsByName(SkillRequestFixtures.updateSkillRequest(skill).getName()))
                    .willReturn(true);

            assertThatThrownBy(() -> skillService.updateSkill(
                    UUID.randomUUID(), SkillRequestFixtures.updateSkillRequest(skill))
            )
                    .isInstanceOf(SkillAlreadyExistsException.class)
                    .hasMessageContaining(ErrorCode.SKILL_ALREADY_EXISTS.message);
        }
    }

    @Nested
    @DisplayName("Deactivate skill")
    class DeactivateSkill {

        @Test
        @DisplayName("Should deactivate skill and set updated at")
        void deactivateSkill_shouldDeactivateSkillAndSetUpdatedAt() {
            Skill skill = SkillFixtures.activeJavaSkill();
            skill.setId(UUID.randomUUID());

            given(skillRepository.findById(skill.getId())).willReturn(Optional.of(skill));

            skillService.deactivateSkill(skill.getId());

            verify(skillRepository).findById(skill.getId());

            assertThat(skill.isActive()).isEqualTo(false);
            assertThat(skill.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception when deactivate skill with invalid id")
        void deactivateSkill_withInvalidId_shouldThrowException() {
            given(skillRepository.findById(any(UUID.class))).willReturn(Optional.empty());

            assertThatThrownBy(() -> skillService.deactivateSkill(UUID.randomUUID()))
                    .isInstanceOf(SkillNotFoundException.class)
                    .hasMessageContaining(ErrorCode.SKILL_NOT_FOUND.message);
        }
    }
}

