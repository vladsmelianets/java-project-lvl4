package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity
public final class Url extends Model {

    @Id
    private Long id;

    private String name;

    @WhenCreated
    private Instant createdAt;

    public Url(Long urlId, String urlName, Instant urlCreatedAt) {
        this.id = urlId;
        this.name = urlName;
        this.createdAt = urlCreatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
