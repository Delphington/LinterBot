package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.AccessFilter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessFilterRepository extends JpaRepository<AccessFilter, Long> {

    boolean existsAccessFilterByFilter(
            @NotBlank @Size(max = 50, message = "Длина фильтра не должна превышать 50 символов") String filter);
}
