package languagequiz.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "sets")
public class Set {

    @Id
    private String id;
    private String name;
    private List<String> flashcardsId;

    public Set(String name) {
        this.name = name;
        this.flashcardsId = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFlashcardsId() {
        return flashcardsId;
    }

    public void addFlashcardId(String _flashcardId) {
        this.flashcardsId.add(_flashcardId);
    }

    public void deleteFlashcardId(String _flashcardId) {
        this.flashcardsId.remove(_flashcardId);
    }
}
