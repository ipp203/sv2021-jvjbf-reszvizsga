package cinema;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final List<Movie> movies = new ArrayList<>();
    private final AtomicLong id = new AtomicLong();

    private final ModelMapper modelMapper;

    public MovieService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    public List<MovieDTO> getMovies(Optional<String> title) {
        return movies.stream()
                .filter(m -> title.isEmpty() || m.getTitle().equalsIgnoreCase(title.get()))
                .map(m -> modelMapper.map(m, MovieDTO.class))
                .collect(Collectors.toList());
    }

    public void deleteAll() {
        id.set(0);
        movies.clear();
    }

    public MovieDTO addNewMovie(CreateMovieCommand command) {
        Movie movie = new Movie(id.incrementAndGet(),
                command.getTitle(),
                command.getDate(),
                command.getTotalSpace(),
                command.getTotalSpace());
        movies.add(movie);
        return modelMapper.map(movie, MovieDTO.class);
    }

    public MovieDTO reserveMovieById(long id, CreateReservationCommand command) {
        Movie movie = findMovieById(id);
        movie.reserveSpaces(command.getReservedSpaces());
        return modelMapper.map(movie, MovieDTO.class);
    }

    public MovieDTO updateMovieDate(long id, UpdateDateCommand command) {
        Movie movie = findMovieById(id);
        movie.setDate(command.getDate());
        return modelMapper.map(movie, MovieDTO.class);
    }

    public MovieDTO getMovieById(long id) {
        return modelMapper.map(findMovieById(id), MovieDTO.class);
    }

    /////////////////////////////////////////

    private Movie findMovieById(long id) {
        return movies.stream()
                .filter(m -> m.getId() == id)
                .findFirst()
                .orElseThrow(() -> new MovieNotFoundException("Movie not found"));
    }


}
