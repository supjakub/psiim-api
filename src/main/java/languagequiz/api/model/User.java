package languagequiz.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String email;
    private List<String> flashcardsId;
    private List<String> setsId;

    public User(String id, String email) {
        this.id = id;
        this.email = email;
        this.flashcardsId = new ArrayList<>();
        this.setsId = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getFlashcardsId() {
        return flashcardsId;
    }

    public List<String> getSetsId() {
        return setsId;
    }

    public void addFlashcardId(String _flashcardId) {
        this.flashcardsId.add(_flashcardId);
    }

    public void deleteFlashcardId(String _flashcardId) {
        this.flashcardsId.remove(_flashcardId);
    }

    public void addSetId(String _setId) {
        this.setsId.add(_setId);
    }

    public void deleteSetId(String _setId) {
        this.setsId.remove(_setId);
    }
}
