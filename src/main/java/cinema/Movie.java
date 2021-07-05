package cinema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    private long id;
    private String title;
    private LocalDateTime date;
    private int totalSpaces;
    private int freeSpaces;

    public void reserveSpaces(int number) {
        if (freeSpaces < number) {
            throw new IllegalStateException("Not enough free spaces");
        }
        freeSpaces -= number;
    }
}
