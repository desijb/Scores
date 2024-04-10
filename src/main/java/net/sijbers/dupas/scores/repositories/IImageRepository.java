package net.sijbers.dupas.scores.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sijbers.dupas.scores.model.db.ImageEntity;

public interface IImageRepository extends JpaRepository<ImageEntity, Long>{
	List<ImageEntity> findByGameId(String gameId);
}
