package languagequiz.api.controller;

import languagequiz.api.model.Flashcard;
import languagequiz.api.model.Set;
import languagequiz.api.service.SetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SetController {

    @Autowired
    SetService setService;

    @GetMapping("/api/set/{id}")
    public ResponseEntity<List<Flashcard>> getSetContentById(@RequestHeader("token") String token, @PathVariable String id) {
        return setService.getSetContentById(token, id);
    }

    @GetMapping("/api/set")
    public ResponseEntity<List<Set>> getAllSets(@RequestHeader("token") String token) {
        return setService.getAllSets(token);
    }

    @PostMapping("/api/set/{name}")
    public ResponseEntity<Set> createSet(@RequestHeader("token") String token, @PathVariable String name) {
        return setService.createSet(token, name);
    }

    @DeleteMapping("/api/set/{id}")
    public ResponseEntity<HttpStatus> deleteSetById(@RequestHeader("token") String token, @PathVariable String id) {
        return setService.deleteSetById(token, id);
    }

    @PutMapping("/api/set/add")
    public ResponseEntity<Set> addFlashcardIdToSet(@RequestHeader("token") String token,
                                                   @RequestParam(value = "setId") String setId,
                                                   @RequestParam(value = "cardId") String flashcardId)
    {
        return setService.addFlashcardIdToSet(token, setId, flashcardId);
    }

    @PutMapping("/api/set/delete")
    public ResponseEntity<Set> deleteFlashcardIdFromSet(@RequestHeader("token") String token,
                                                        @RequestParam(value = "setId") String setId,
                                                        @RequestParam(value = "cardId") String flashcardId)
    {
        return setService.deleteFlashcardIdFromSet(token, setId, flashcardId);
    }
}
