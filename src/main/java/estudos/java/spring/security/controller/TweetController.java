package estudos.java.spring.security.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import estudos.java.spring.security.dto.CreateTweetDto;
import estudos.java.spring.security.dto.FeedDto;
import estudos.java.spring.security.dto.FeedItemDto;
import estudos.java.spring.security.entities.Role;
import estudos.java.spring.security.entities.Tweet;
import estudos.java.spring.security.repository.TweetRepository;
import estudos.java.spring.security.repository.UserRepository;

@RestController
public class TweetController {

  private final TweetRepository tweetRepository;
  private final UserRepository userRepository;

  public TweetController(TweetRepository tweetRepository, UserRepository userRepository) {
    this.tweetRepository = tweetRepository;
    this.userRepository = userRepository;
  }
  
  @PostMapping("/tweets")
  public ResponseEntity<Void> createTweet(@RequestBody CreateTweetDto dto, JwtAuthenticationToken token) {
    
    var user = userRepository.findById(Long.parseLong(token.getName()));

    var tweet = new Tweet();
    tweet.setUser(user.get());
    tweet.setContent(dto.content());

    tweetRepository.save(tweet);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/tweets/{id}")
  public ResponseEntity<Void> deleteTweet(@PathVariable("id") Long tweetId, JwtAuthenticationToken token) {

    var user = userRepository.findById(Long.parseLong(token.getName()));

    var tweet = tweetRepository.findById(tweetId)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    var isAdmin = user.get().getRole()
                    .stream()
                    .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

    if (isAdmin || tweet.getUser().getUserId().equals(Long.parseLong(token.getName()))) {
      tweetRepository.deleteById(tweetId);
    } else
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

    return ResponseEntity.ok().build();

  }

  @GetMapping("/feed")
  public ResponseEntity<FeedDto> feed(@RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
    
    var tweets = tweetRepository.findAll(
          PageRequest.of(page, pageSize, Sort.Direction.DESC, "creationTimestamp"))
          .map(tweet -> new FeedItemDto(tweet.getTweetId(), tweet.getContent(), tweet.getUser().getUsername()));

    return ResponseEntity.ok(new FeedDto(tweets.getContent(), page, pageSize, tweets.getTotalElements()));
  } 
}
