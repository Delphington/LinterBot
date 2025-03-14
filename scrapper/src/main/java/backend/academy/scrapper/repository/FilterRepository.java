package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.Filter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilterRepository  extends JpaRepository<Filter, Long> {
}
