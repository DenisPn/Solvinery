package Persistence.Entities.Image.Operational;

import Persistence.Entities.Image.ImageComponentKey;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PreferenceModuleEntityTest {

    @Test
    public void testEquals_SameObject () {
        ImageComponentKey key = new ImageComponentKey(UUID.randomUUID(), "Component1");
        Set<PreferenceEntity> preferences = new HashSet<>();
        preferences.add(new PreferenceEntity("Preference1"));
        PreferenceModuleEntity entity = new PreferenceModuleEntity();
        entity.setImageComponentKey(key);
        entity.setDescription("Test Description");
        entity.setPreferences(preferences);

        assertTrue(entity.equals(entity));
    }

    @Test
    public void testEquals_EqualObjects () {
        ImageComponentKey key1 = new ImageComponentKey(UUID.randomUUID(), "Component1");
        Set<PreferenceEntity> preferences1 = new HashSet<>();
        preferences1.add(new PreferenceEntity("Preference1"));

        ImageComponentKey key2 = new ImageComponentKey(key1.getImageId(), key1.getName());
        Set<PreferenceEntity> preferences2 = new HashSet<>();
        preferences2.add(new PreferenceEntity("Preference1"));

        PreferenceModuleEntity entity1 = new PreferenceModuleEntity();
        entity1.setImageComponentKey(key1);
        entity1.setDescription("Test Description");
        entity1.setPreferences(preferences1);

        PreferenceModuleEntity entity2 = new PreferenceModuleEntity();
        entity2.setImageComponentKey(key2);
        entity2.setDescription("Test Description");
        entity2.setPreferences(preferences2);

        assertTrue(entity1.equals(entity2));
    }

    @Test
    public void testEquals_DifferentKeys () {
        ImageComponentKey key1 = new ImageComponentKey(UUID.randomUUID(), "Component1");
        ImageComponentKey key2 = new ImageComponentKey(UUID.randomUUID(), "Component2");

        PreferenceModuleEntity entity1 = new PreferenceModuleEntity();
        entity1.setImageComponentKey(key1);
        entity1.setDescription("Test Description");
        entity1.setPreferences(Collections.emptySet());

        PreferenceModuleEntity entity2 = new PreferenceModuleEntity();
        entity2.setImageComponentKey(key2);
        entity2.setDescription("Test Description");
        entity2.setPreferences(Collections.emptySet());

        assertFalse(entity1.equals(entity2));
    }

    @Test
    public void testEquals_DifferentDescriptions () {
        ImageComponentKey key = new ImageComponentKey(UUID.randomUUID(), "Component1");

        PreferenceModuleEntity entity1 = new PreferenceModuleEntity();
        entity1.setImageComponentKey(key);
        entity1.setDescription("Description1");
        entity1.setPreferences(Collections.emptySet());

        PreferenceModuleEntity entity2 = new PreferenceModuleEntity();
        entity2.setImageComponentKey(key);
        entity2.setDescription("Description2");
        entity2.setPreferences(Collections.emptySet());

        assertFalse(entity1.equals(entity2));
    }

    @Test
    public void testEquals_DifferentPreferences () {
        ImageComponentKey key = new ImageComponentKey(UUID.randomUUID(), "Component1");

        Set<PreferenceEntity> preferences1 = new HashSet<>();
        preferences1.add(new PreferenceEntity("Preference1"));

        Set<PreferenceEntity> preferences2 = new HashSet<>();
        preferences2.add(new PreferenceEntity("Preference2"));

        PreferenceModuleEntity entity1 = new PreferenceModuleEntity();
        entity1.setImageComponentKey(key);
        entity1.setDescription("Test Description");
        entity1.setPreferences(preferences1);

        PreferenceModuleEntity entity2 = new PreferenceModuleEntity();
        entity2.setImageComponentKey(key);
        entity2.setDescription("Test Description");
        entity2.setPreferences(preferences2);

        assertFalse(entity1.equals(entity2));
    }

    @Test
    public void testEquals_NullObject () {
        ImageComponentKey key = new ImageComponentKey(UUID.randomUUID(), "Component1");
        Set<PreferenceEntity> preferences = new HashSet<>();
        preferences.add(new PreferenceEntity("Preference1"));
        PreferenceModuleEntity entity = new PreferenceModuleEntity();
        entity.setImageComponentKey(key);
        entity.setDescription("Test Description");
        entity.setPreferences(preferences);

        assertFalse(entity.equals(null));
    }

    @Test
    public void testEquals_DifferentClass () {
        ImageComponentKey key = new ImageComponentKey(UUID.randomUUID(), "Component1");
        Set<PreferenceEntity> preferences = new HashSet<>();
        preferences.add(new PreferenceEntity("Preference1"));
        PreferenceModuleEntity entity = new PreferenceModuleEntity();
        entity.setImageComponentKey(key);
        entity.setDescription("Test Description");
        entity.setPreferences(preferences);

        assertFalse(entity.equals("Some String"));
    }
}