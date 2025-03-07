package backend.academy.scrapper.api.repository;

import backend.academy.scrapper.api.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkRepository extends JpaRepository<Link, Long> {
}
