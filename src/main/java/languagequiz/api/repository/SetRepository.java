package languagequiz.api.repository;

import languagequiz.api.model.Set;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SetRepository extends MongoRepository<Set, String> {
}
