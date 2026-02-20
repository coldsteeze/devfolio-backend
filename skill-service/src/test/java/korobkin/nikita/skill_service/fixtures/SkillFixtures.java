package korobkin.nikita.skill_service.fixtures;

import korobkin.nikita.skill_service.entity.Skill;
import korobkin.nikita.skill_service.entity.enums.SkillCategory;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SkillFixtures {

    public static final String NAME_JAVA_SKILL = "Java";
    public static final String NAME_INACTIVE_SKILL = "Pascal";
    public static final String NAME_SPRING_SKILL = "Spring";
    public static final String NAME_JS_SKILL = "JavaScript";
    public static final SkillCategory LANGUAGE_CATEGORY = SkillCategory.LANGUAGE;
    public static final SkillCategory FRAMEWORK_CATEGORY = SkillCategory.FRAMEWORK;


    public static Skill activeJavaSkill() {
        return activeSkill(NAME_JAVA_SKILL, LANGUAGE_CATEGORY);
    }

    public static Skill inactivePascalSkill() {
        return inactiveSkill(NAME_INACTIVE_SKILL, LANGUAGE_CATEGORY);
    }

    public static Skill activeSpringSkill() {
        return activeSkill(NAME_SPRING_SKILL, FRAMEWORK_CATEGORY);
    }

    public static Skill activeJsSkill() {
        return activeSkill(NAME_JS_SKILL, LANGUAGE_CATEGORY);
    }

    public static Skill activeSkill(String name, SkillCategory category) {
        Skill skill = new Skill();
        skill.setName(name);
        skill.setCategory(category);

        return skill;
    }

    public static Skill inactiveSkill(String name, SkillCategory category) {
        Skill skill = new Skill();
        skill.setName(name);
        skill.setCategory(category);
        skill.setActive(false);

        return skill;
    }
}
