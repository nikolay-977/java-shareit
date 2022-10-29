package ru.practicum.shareit.server.booking;

import lombok.*;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date")
    private LocalDateTime start;
    @Column(name = "end_date")
    private LocalDateTime end;
    @Enumerated(EnumType.STRING)
    private BookingState status;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
}
