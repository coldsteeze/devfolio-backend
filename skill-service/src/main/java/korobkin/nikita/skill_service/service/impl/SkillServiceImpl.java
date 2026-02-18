package korobkin.nikita.skill_service.service.impl;

import korobkin.nikita.skill_service.dto.request.BulkSkillRequest;
import korobkin.nikita.skill_service.dto.request.CreateSkillRequest;
import korobkin.nikita.skill_service.dto.request.UpdateSkillRequest;
import korobkin.nikita.skill_service.entity.Skill;
import korobkin.nikita.skill_service.exception.ErrorCode;
import korobkin.nikita.skill_service.exception.SkillAlreadyExistsException;
import korobkin.nikita.skill_service.exception.SkillNotFoundException;
import korobkin.nikita.skill_service.mapper.SkillMapper;
import korobkin.nikita.skill_service.repository.SkillRepository;
import korobkin.nikita.skill_service.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import korobkin.nikita.skill_service.dto.request.SkillFilterRequest;
import korobkin.nikita.skill_service.dto.response.PagedResponse;
import korobkin.nikita.skill_service.dto.response.SkillResponse;
import korobkin.nikita.skill_service.service.SkillService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SkillResponse> findSkills(
            SkillFilterRequest request, Pageable pageable) {
        log.info("Fetching skills: search={}, category={}, page={}, size={} sort={}",
                request.getSearch(),
                request.getCategory(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());


        boolean includeInactive = SecurityUtils.hasRole("ADMIN");

        System.out.println("includeInactive: " + includeInactive);

        request.setIncludeInactive(includeInactive);

        Page<Skill> page = skillRepository.findAllByFilters(request, pageable);

        List<SkillResponse> content = page.stream()
                .map(skillMapper::toDto)
                .toList();

        return new PagedResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    @Transactional
    public SkillResponse createSkill(CreateSkillRequest createSkillRequest) {
        Skill skill = skillMapper.toEntity(createSkillRequest);

        if (skillRepository.existsByName(skill.getName())) {
            throw new SkillAlreadyExistsException(ErrorCode.SKILL_ALREADY_EXISTS);
        }

        skillRepository.save(skill);
        log.info("Skill with id {} successfully saved", skill.getId());

        return skillMapper.toDto(skill);
    }

    @Override
    @Transactional(readOnly = true)
    public SkillResponse findSkill(UUID skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new SkillNotFoundException(ErrorCode.SKILL_NOT_FOUND));

        if (!skill.isActive() && SecurityUtils.hasRole("USER")) {
            throw new SkillNotFoundException(ErrorCode.SKILL_NOT_FOUND);
        }

        log.info("Skill with id {} successfully found", skillId);

        return skillMapper.toDto(skill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> findBulkSkills(BulkSkillRequest request) {
        List<UUID> skillIds = request.getSkillIds().stream()
                .map(UUID::fromString)
                .toList();

        List<Skill> skills;

        if (SecurityUtils.hasRole("ADMIN")) {
            skills = skillRepository.findAllById(skillIds);
        } else {
            skills = skillRepository.findAllByIdInAndActiveTrue(skillIds);
        }

        log.info("Bulk skills found with parameters: {}", request.getSkillIds());

        return skills.stream()
                .map(skillMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public SkillResponse updateSkill(UUID skillId, UpdateSkillRequest request) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new SkillNotFoundException(ErrorCode.SKILL_NOT_FOUND));

        if (skillRepository.existsByName(request.getName())) {
            throw new SkillAlreadyExistsException(ErrorCode.SKILL_ALREADY_EXISTS);
        }

        skillMapper.updateEntityFromDto(request, skill);
        skill.setUpdatedAt(LocalDateTime.now());

        log.info("Successfully updated skill with id {}", skillId);

        return skillMapper.toDto(skill);
    }

    @Override
    @Transactional
    public void deactivateSkill(UUID skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new SkillNotFoundException(ErrorCode.SKILL_NOT_FOUND));

        skill.setActive(false);
        skill.setUpdatedAt(LocalDateTime.now());

        log.info("Skill with id {} deactivated", skillId);
    }
}
