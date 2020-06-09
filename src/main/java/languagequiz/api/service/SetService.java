package languagequiz.api.service;

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
import java.util.Optional;

@Service
public class SetService {
    @Autowired
    SetRepository setRepository;
    @Autowired
    FlashcardRepository flashcardRepository;
    @Autowired
    UserService userService;

    public ResponseEntity<List<Flashcard>> getSetContentById(String _token, String _setId) {
        FirebaseToken firebaseToken = userService.verifyToken(_token);
        if (firebaseToken != null) {
            String userId = firebaseToken.getUid();
            Optional<Set> set = setRepository.findById(_setId);
            if (set.isPresent() && userService.setBelongsToUser(userId, _setId)) {
                Set set_data = set.get();
                List<Flashcard> flashcards = new ArrayList<>();
                for (String flashcardId : set_data.getFlashcardsId()) {
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

    public ResponseEntity<List<Set>> getAllSets(String _token) {
        FirebaseToken firebaseToken = userService.verifyToken(_token);
        if (firebaseToken != null) {
            String userId = firebaseToken.getUid();
            Optional<User> user = userService.userRepository.findById(userId);
            if (user.isPresent()) {
                User user_data = user.get();
                List<Set> sets = new ArrayList<>();
                for (String setId : user_data.getSetsId()) {
                    Optional<Set> set = setRepository.findById(setId);
                    set.ifPresent(sets::add);
                }
                return new ResponseEntity<>(sets, HttpStatus.OK);
            }
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<Set> createSet(String _token, String _name) {
        FirebaseToken firebaseToken = userService.verifyToken(_token);
        if (firebaseToken != null) {
            String userId = firebaseToken.getUid();
            Set _set = setRepository.save(new Set(_name));
            if (userService.addSetIdToUser(userId, _set.getId()))
                return new ResponseEntity<>(_set, HttpStatus.CREATED);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<HttpStatus> deleteSetById(String _token, String _setId) {
        FirebaseToken firebaseToken = userService.verifyToken(_token);
        if (firebaseToken != null) {
            String userId = firebaseToken.getUid();
            if (userService.setBelongsToUser(userId, _setId) && userService.deleteSetIdFromUser(userId, _setId)) {
                setRepository.deleteById(_setId);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<Set> addFlashcardIdToSet(String _token, String _setId, String _flashcardId) {
        FirebaseToken firebaseToken = userService.verifyToken(_token);
        if (firebaseToken != null) {
            String userId = firebaseToken.getUid();
            if (userService.setBelongsToUser(userId, _setId) && userService.flashcardBelongsToUser(userId, _flashcardId)) {
                Optional<Set> set = setRepository.findById(_setId);
                if (set.isPresent()) {
                    Set set_data = set.get();
                    set_data.addFlashcardId(_flashcardId);
                    setRepository.save(set_data);
                    return new ResponseEntity<>(set_data, HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<Set> deleteFlashcardIdFromSet(String _token, String _setId, String _flashcardId) {
        FirebaseToken firebaseToken = userService.verifyToken(_token);
        if (firebaseToken != null) {
            String userId = firebaseToken.getUid();
            if (userService.setBelongsToUser(userId, _setId) && userService.flashcardBelongsToUser(userId, _flashcardId)) {
                Optional<Set> set = setRepository.findById(_setId);
                if (set.isPresent()) {
                    Set set_data = set.get();
                    set_data.deleteFlashcardId(_flashcardId);
                    setRepository.save(set_data);
                    return new ResponseEntity<>(set_data, HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    public boolean _deleteFlashcardIdFromSet(String _setId, String _flashcardId) {
        Optional<Set> set = setRepository.findById(_setId);
        if (set.isPresent()) {
            Set _set = set.get();
            if (_set.getFlashcardsId().contains(_flashcardId)) {
                _set.deleteFlashcardId(_flashcardId);
                setRepository.save(_set);
                return true;
            }
            else
                return false;
        }
        else
            return false;
    }
}
