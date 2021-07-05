package cinema;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/cinema")
public class MovieController {

    private final MovieService service;

    public MovieController(MovieService service) {
        this.service = service;
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMovies() {
        service.deleteAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieDTO createMovie(@Valid @RequestBody CreateMovieCommand command) {
        return service.addNewMovie(command);
    }

    @PostMapping("/{id}/reserve")
    public MovieDTO reserveMovie(@PathVariable("id") long id, @RequestBody CreateReservationCommand command) {
        return service.reserveMovieById(id, command);
    }

    @PutMapping("{id}")
    public MovieDTO updateMovieDate(@PathVariable("id") long id, @RequestBody UpdateDateCommand command) {
        return service.updateMovieDate(id, command);
    }

    @GetMapping
    public List<MovieDTO> getMovies(@RequestParam Optional<String> title) {
        return service.getMovies(title);
    }

    @GetMapping("{id}")
    public MovieDTO getMovieById(@PathVariable("id") long id) {
        return service.getMovieById(id);
    }


    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Problem> handleBadReservation(IllegalStateException exception) {
        Problem problem = Problem.builder()
                .withType(URI.create("cinema/bad-reservation"))
                .withTitle("Bad reservation")
                .withStatus(Status.BAD_REQUEST)
                .withDetail(exception.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<Problem> handleMovieNotFound(MovieNotFoundException exception) {
        Problem problem = Problem.builder()
                .withType(URI.create("cinema/not-found"))
                .withTitle("Not found")
                .withStatus(Status.NOT_FOUND)
                .withDetail(exception.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handleNotValidFields(MethodArgumentNotValidException exception) {
        List<Violation> violations = exception.getBindingResult().getFieldErrors().stream()
                .map(fe -> new Violation(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());

        Problem problem = Problem.builder()
                .withType(URI.create("Cinema/not-valid"))
                .withTitle("Not valid")
                .withStatus(Status.BAD_REQUEST)
                .withDetail(exception.getMessage())
                .with("Violations", violations)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }
}
