package zw.co.zss.interview.payment;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import zw.co.zss.interview.book.Book;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table
public class Payment {
    @Id
    @Type(type = "uuid-char")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID paymentId;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @NonNull
    @Column(nullable = false)
    private double amount;

    @NonNull
    @Column(nullable = false)
    private String pan;

    @NonNull
    @Column(nullable = false)
    private String email;

    @NonNull
    @Column(nullable = false)
    private String addressLine1;

    @NonNull
    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(nullable = false)
    private String responseCode;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}
