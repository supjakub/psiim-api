package languagequiz.api.service;

import com.google.api.Http;
import com.google.firebase.auth.FirebaseToken;
import languagequiz.api.model.Flashcard;
import languagequiz.api.model.Set;
import languagequiz.api.model.User;
import languagequiz.api.repository.FlashcardRepository;
import languagequiz.api.repository.SetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FlashcardService {

    @Autowired
    FlashcardRepository flashcardRepository;
    @Autowired
    UserService userService;
    @Autowired
    SetService setService;

    public ResponseEntity<Flashcard> getFlashcardById(String _token, String _flashcard_id) {
        FirebaseToken firebaseToken = userService.verifyToken(_token);
        if (firebaseToken != null) {
            String id = firebaseToken.getUid();
            Optional<Flashcard> flashcard = flashcardRepository.findById(_flashcard_id);
            if (flashcard.isPresent() && userService.flashcardBelongsToUser(id, _flashcard_id))
                return new ResponseEntity<>(flashcard.get(), HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<List<Flashcard>> getAllFlashcards(String _token) {
        FirebaseToken firebaseToken = userService.verifyToken(_token);
        if (firebaseToken != null) {
            String id = firebaseToken.getUid();
            Optional<User> user = userService.userRepository.findById(id);
            if (user.isPresent()) {
                User userData = user.get();
                List<String> flashcardsId = userData.getFlashcardsId();
                List<Flashcard> flashcards = new ArrayList<>();
                for (String flashcardId : flashcardsId) {
                    Optional<Flashcard> flashcard = flashcardRepository.findById(flashcardId);
                    if (flashcard.isPresent())
                        flashcards.add(flashcard.get());
                }
                return new ResponseEntity<>(flashcards, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<Flashcard> createFlashcard(String _token, Flashcard _flashcard) {
        FirebaseToken firebaseToken = userService.verifyToken(_token);
        if (firebaseToken != null) {
            _flashcard.setId(null);
            flashcardRepository.save(_flashcard);
            String userId = firebaseToken.getUid();
            if (userService.addFlashcardIdToUser(userId, _flashcard.getId()))
                return new ResponseEntity<>(_flashcard, HttpStatus.CREATED);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<HttpStatus> deleteFlashcardById(String _token, String _flashcardId) {
        FirebaseToken firebaseToken = userService.verifyToken(_token);
        if (firebaseToken != null) {
            String userId = firebaseToken.getUid();
            Optional<User> user_data = userService.userRepository.findById(userId);
            User user = user_data.get();
            if (user.getFlashcardsId().contains(_flashcardId)) {
                userService.deleteFlashcardIdFromUser(userId, _flashcardId);
                flashcardRepository.deleteById(_flashcardId);
                for (String setId : user.getSetsId()) {
                    Optional<Set> set_data = setService.setRepository.findById(setId);
                    Set set = set_data.get();
                    setService._deleteFlashcardIdFromSet(setId, _flashcardId);
                }
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }


}
