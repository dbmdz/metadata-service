package io.github.dbmdz.metadata.server.backend.impl.jdbi.identifiable.entity.agent;

import de.digitalcollections.model.identifiable.entity.agent.Agent;
import de.digitalcollections.model.text.LocalizedText;
import de.digitalcollections.model.validation.ValidationException;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.IdentifierRepository;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.alias.UrlAliasRepository;
import io.github.dbmdz.metadata.server.backend.impl.jdbi.AbstractIdentifiableRepositoryImplTest;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(
    webEnvironment = WebEnvironment.MOCK,
    classes = {AgentRepositoryImpl.class})
@DisplayName("The Agent Repository")
class AgentRepositoryImplTest
    extends AbstractIdentifiableRepositoryImplTest<AgentRepositoryImpl<Agent>> {
  @Autowired private IdentifierRepository identifierRepository;
  @Autowired private UrlAliasRepository urlAliasRepository;

  @BeforeEach
  public void beforeEach() {
    repo = new AgentRepositoryImpl<>(jdbi, cudamiConfig, identifierRepository, urlAliasRepository);
  }

  @Test
  @DisplayName("can save (create) an agent")
  void testSave() throws RepositoryException, ValidationException {
    Agent agent = Agent.builder().label("Test").addName(Locale.ENGLISH, "a name").build();
    saveAndAssertTimestampsAndEqualityToSaveable(agent);
  }

  @Test
  @DisplayName("can update an agent")
  void testUpdate() throws RepositoryException, ValidationException {
    Agent agent = Agent.builder().label("Test").addName("some name").build();
    repo.save(agent);

    agent.setLabel("changed test");
    LocalizedText name =
        LocalizedText.builder()
            .text(Locale.ENGLISH, "some english name")
            .text(LOCALE_ZH_HANI, "難經辨眞")
            .build();
    agent.setName(name);
    agent.setNameLocalesOfOriginalScripts(Set.of(Locale.ENGLISH, LOCALE_ZH_HANI));

    Agent beforeUpdate = createDeepCopy(agent);
    updateAndAssertUpdatedLastModifiedTimestamp(agent);
    assertInDatabaseIsEqualToUpdateable(agent, beforeUpdate, Function.identity());
  }
}
