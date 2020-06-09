package languagequiz.api.controller;

import languagequiz.api.model.Flashcard;
import languagequiz.api.service.FlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FlashcardController {

    @Autowired
    FlashcardService flashcardService;

    @GetMapping("/api/flashcard/{id}")
    public ResponseEntity<Flashcard> getFlashcardById(@RequestHeader("token") String token, @PathVariable String id) {
        return flashcardService.getFlashcardById(token, id);
    }

    @GetMapping("/api/flashcard")
    public ResponseEntity<List<Flashcard>> getAllFlashcards(@RequestHeader String token) {
        return flashcardService.getAllFlashcards(token);
    }

    @PostMapping("/api/flashcard")
    public ResponseEntity<Flashcard> createFlashcard(@RequestHeader("token") String token, @RequestBody Flashcard flashcard) {
        return flashcardService.createFlashcard(token, flashcard);
    }

    @DeleteMapping("/api/flashcard/{id}")
    public ResponseEntity<HttpStatus> deleteFlashcardById(@RequestHeader("token") String token, @PathVariable String id) {
        return flashcardService.deleteFlashcardById(token, id);
    }
}
