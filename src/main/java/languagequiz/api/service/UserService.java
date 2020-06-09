package languagequiz.api.service;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import languagequiz.api.model.Credentials;
import languagequiz.api.model.Flashcard;
import languagequiz.api.model.User;
import languagequiz.api.repository.FlashcardRepository;
import languagequiz.api.repository.SetRepository;
import languagequiz.api.repository.UserRepository;
import languagequiz.api.security.FirebaseAuthSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import languagequiz.api.model.Set;

import java.util.*;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    FlashcardRepository flashcardRepository;
    @Autowired
    SetRepository setRepository;

    public ResponseEntity<User> createUser(Credentials _credentials) {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(_credentials.getEmail())
                .setPassword(_credentials.getPassword());
        UserRecord userRecord;
        try {
            userRecord = FirebaseAuthSingleton.getInstance().getFirebaseAuth().createUser(request);
        } catch (FirebaseAuthException exception) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
        User _user = new User(userRecord.getUid(), _credentials.getEmail());

        Set exampleSet = new Set("przykład");
        List<Flashcard> exampleFlashcards = new ArrayList<>();
        exampleFlashcards.add(new Flashcard("ulubiony", "favourite"));
        exampleFlashcards.add(new Flashcard("spotykać", "meet"));
        exampleFlashcards.add(new Flashcard("dawać", "give"));
        exampleFlashcards.add(new Flashcard("stolica", "capital city"));
        exampleFlashcards.add(new Flashcard("sztuka", "art"));
        for (Flashcard flashcard : exampleFlashcards) {
            flashcardRepository.save(flashcard);
            exampleSet.addFlashcardId(flashcard.getId());
            _user.addFlashcardId(flashcard.getId());
        }
        setRepository.save(exampleSet);
        _user.addSetId(exampleSet.getId());

        userRepository.save(_user);
        return new ResponseEntity<>(_user, HttpStatus.CREATED);
    }

    public ResponseEntity<HttpStatus> updateUser(String _token, Credentials _credentials) {
        FirebaseToken firebaseToken = verifyToken(_token);
        if (firebaseToken != null) {
            String userId = firebaseToken.getUid();
            UserRecord.UpdateRequest updateRequest = new UserRecord.UpdateRequest(userId);
            if (_credentials.getEmail() != null)
                updateRequest.setEmail(_credentials.getEmail());
            if (_credentials.getPassword() != null)
                updateRequest.setPassword(_credentials.getPassword());
            try {
                UserRecord userRecord = FirebaseAuthSingleton.getInstance().getFirebaseAuth().updateUser(updateRequest);
            }
            catch (FirebaseAuthException exception) {
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
            }
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                User user_data = user.get();
                if (_credentials.getEmail() != null) {
                    user_data.setEmail(_credentials.getEmail());
                    userRepository.save(user_data);
                }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<HttpStatus> deleteUser(String _token) {
        FirebaseToken firebaseToken = verifyToken(_token);
        if (firebaseToken != null) {
            String userId = firebaseToken.getUid();
            try {
                FirebaseAuthSingleton.getInstance().getFirebaseAuth().deleteUser(userId);
            }
            catch (FirebaseAuthException exception) {
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
            }
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                User user_data = user.get();
                for (String flashcardId : user_data.getFlashcardsId())
                    flashcardRepository.deleteById(flashcardId);
                for (String setId : user_data.getSetsId())
                    setRepository.deleteById(setId);
                userRepository.deleteById(userId);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<String> loginUser(Credentials _credentials) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        RestTemplate restTemplate = restTemplateBuilder.build();

        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=API_KEY";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> map = new HashMap<>();
        map.put("email", _credentials.getEmail());
        map.put("password", _credentials.getPassword());
        map.put("returnSecureToken", true);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
        return restTemplate.postForEntity(url, entity, String.class);
    }

    public FirebaseToken verifyToken(String _token) {
        FirebaseToken firebaseToken;
        try {
            firebaseToken = FirebaseAuthSingleton.getInstance().getFirebaseAuth().verifyIdToken(_token);
        }
        catch (FirebaseAuthException exception) {
            return null;
        }
        return firebaseToken;
    }

    public ResponseEntity<User> getUser(String _token) {
        FirebaseToken firebaseToken = verifyToken(_token);
        if (firebaseToken != null) {
            String id = firebaseToken.getUid();
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent())
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<Map<String, Integer>> getStatistics(String _token) {
        FirebaseToken firebaseToken = verifyToken(_token);
        if (firebaseToken != null) {
            String userId = firebaseToken.getUid();
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                Map<String, Integer> response = new HashMap<>();
                response.put("numberOfFlashcards", user.get().getFlashcardsId().size());
                response.put("numberOfSets", user.get().getSetsId().size());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    public boolean addFlashcardIdToUser(String _userId, String _flashcardId) {
        Optional<User> user = userRepository.findById(_userId);
        if (user.isPresent()) {
            User user_data = user.get();
            user_data.addFlashcardId(_flashcardId);
            userRepository.save(user_data);
            return true;
        }
        else return false;
    }

    public boolean deleteFlashcardIdFromUser(String _userId, String _flashcardId) {
        Optional<User> user = userRepository.findById(_userId);
        if (user.isPresent()) {
            User user_data = user.get();
            user_data.deleteFlashcardId(_flashcardId);
            userRepository.save(user_data);
            return true;
        }
        else return false;
    }

    public boolean addSetIdToUser(String _userId, String _setId) {
        Optional<User> user = userRepository.findById(_userId);
        if (user.isPresent()) {
            User user_data = user.get();
            user_data.addSetId(_setId);
            userRepository.save(user_data);
            return true;
        }
        else return false;
    }

    public boolean deleteSetIdFromUser(String _userId, String _setId) {
        Optional<User> user = userRepository.findById(_userId);
        if (user.isPresent()) {
            User user_data = user.get();
            user_data.deleteSetId(_setId);
            userRepository.save(user_data);
            return true;
        }
        else return false;
    }

    public boolean flashcardBelongsToUser(String _userId, String _flashcardId) {
        Optional<User> user = userRepository.findById(_userId);
        if (user.isPresent()) {
            User user_data = user.get();
            return user_data.getFlashcardsId().contains(_flashcardId);
        }
        return false;
    }

    public boolean setBelongsToUser(String _userId, String _setId) {
        Optional<User> user = userRepository.findById(_userId);
        if (user.isPresent()) {
            User user_data = user.get();
            return user_data.getSetsId().contains(_setId);
        }
        return false;
    }
}